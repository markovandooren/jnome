package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.util.Util;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaMethod;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;

public class ConstructorSelector extends AbstractConstructorSelector {

	protected class ConstructorSelectionResult extends BasicMethodSelectionResult {
		protected ConstructorSelectionResult(Method template, TypeAssignmentSet assignment, int phase,boolean requiredUncheckedConversion) {
			super(template, assignment, phase,requiredUncheckedConversion);
		}

		@Override
		public Type returnType(Method method) throws LookupException {
			if(invocation().isDiamondInvocation()) {
				return method.returnType();
			} else {
				return super.returnType(method);
			}
		}
		
		@Override
		public Declaration finalDeclaration() throws LookupException {
			if(invocation().isDiamondInvocation()) {
//				return instantiatedMethodTemplate(createDiamondConstructorDummy(template()));
			return instantiatedMethodTemplate(template());
			} else {
				return template();
			}
		}
		
		@Override
		public SelectionResult updatedTo(Declaration declaration) {
			return new ConstructorSelectionResult((Method) declaration, typeAssignment(), phase(),requiredUncheckedConversion());
		}

	}

	private ConstructorInvocation _invocation;

	protected ConstructorInvocation invocation() {
		return _invocation;
	}

	/**
	 * 
	 */
	public ConstructorSelector(ConstructorInvocation constructorInvocation, String name) {
		_invocation = constructorInvocation;
		__name = name;
	}

	String __name;

	@Override
	public String selectionName(DeclarationContainer container) {
		return __name;
	}

//  public List<NormalMethod> declarations(DeclarationContainer container) throws LookupException {
//  	if(invocation().isDiamondInvocation()) {
//  		return ((DeclarationContainer)container.origin()).declarations(this);
//  	} 
//  	else {
//  	  return container.declarations(this);
//  	}
//  }

	public List<? extends SelectionResult> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
		List<Declaration> withoutNonConstructors = withoutNonConstructors(selectionCandidates);
		boolean diamondInvocation = invocation().isDiamondInvocation();
		if(diamondInvocation) {
			withoutNonConstructors = diamondConstructors(withoutNonConstructors);
		}
		List<SelectionResult> selection = (List)super.selection(withoutNonConstructors);
		if(diamondInvocation) {
			List<SelectionResult> tmp = selection;
			selection = new ArrayList<SelectionResult>();
			for(SelectionResult r: tmp) {
				MethodSelectionResult result = (MethodSelectionResult) r;
				NormalMethod normalMethod = (NormalMethod)result.finalDeclaration();
				NormalMethod origin = (NormalMethod)normalMethod.origin();
				selection.add(createSelectionResult(origin, result.typeAssignment(), result.phase(), result.requiredUncheckedConversion()));
			}
		}
		return selection;
	}
	
	@Override
	public be.kuleuven.cs.distrinet.jnome.core.expression.invocation.MethodSelectionResult createSelectionResult(
			Method method, TypeAssignmentSet typeAssignment, int phase, boolean requiredUncheckedConversion) {
		return new ConstructorSelectionResult(method, typeAssignment, phase, requiredUncheckedConversion);
	}
	
//	@Override
//	public NormalMethod instance(NormalMethod method) throws LookupException {
//		return instance(createDiamondConstructorDummy(method));
//	}

	public List<Declaration> diamondConstructors(List<? extends Declaration> selectionCandidates) throws LookupException {
		List<Declaration> result = new ArrayList<Declaration>();
		for(Declaration decl: selectionCandidates) {
			result.add(createDiamondConstructorDummy(decl));
		}
		return result;
	}

	private JavaMethod createDiamondConstructorDummy(Declaration decl) {
		JavaMethod method = (JavaMethod) decl.clone();
		ConstructorInvocation invocation = invocation();
		BasicJavaTypeReference returnTypeReference = (BasicJavaTypeReference) method.returnTypeReference();
		method.setUniParent(decl.parent());
		method.setOrigin(decl);
		Type type = decl.nearestAncestor(Type.class);
		Java language = invocation.language(Java.class);
		for(TypeParameter param:type.parameters(TypeParameter.class)) {
			method.header().addTypeParameter(Util.clone(param));
			BasicJavaTypeReference tref = language.createTypeReference(param.name());
			returnTypeReference.addArgument(language.createBasicTypeArgument(tref));
		}
		return method;
	}
}
