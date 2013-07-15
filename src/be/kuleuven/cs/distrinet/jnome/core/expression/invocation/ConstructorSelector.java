package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.SelectionResult;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaNormalMethod;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;

public class ConstructorSelector extends AbstractConstructorSelector {

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
		//
		
		if(diamondInvocation) {
			List<SelectionResult> tmp = selection;
			selection = new ArrayList<SelectionResult>();
			for(SelectionResult method: tmp) {
				selection.add(((NormalMethod)method.finalDeclaration()).origin());
			}
		}
		return selection;
	}
	
	
	
	@Override
	public NormalMethod instance(NormalMethod method) throws LookupException {
		return instance(createDiamondConstructorDummy(method));
	}

	public List<Declaration> diamondConstructors(List<? extends Declaration> selectionCandidates) throws LookupException {
		List<Declaration> result = new ArrayList<Declaration>();
		for(Declaration decl: selectionCandidates) {
			result.add(createDiamondConstructorDummy(decl));
		}
		return result;
	}

	private JavaNormalMethod createDiamondConstructorDummy(Declaration decl) {
		JavaNormalMethod method = (JavaNormalMethod) decl.clone();
		ConstructorInvocation invocation = invocation();
		BasicJavaTypeReference returnTypeReference = (BasicJavaTypeReference) method.returnTypeReference();
		method.setUniParent(decl.parent());
		method.setOrigin(decl);
		Type type = decl.nearestAncestor(Type.class);
		for(TypeParameter param:type.parameters(TypeParameter.class)) {
			method.header().addTypeParameter(Util.clone(param));
			returnTypeReference.addArgument(new BasicTypeArgument(invocation.language(Java.class).createTypeReference(param.name())));
		}
		return method;
	}
}
