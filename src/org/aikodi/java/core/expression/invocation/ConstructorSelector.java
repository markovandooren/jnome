package org.aikodi.java.core.expression.invocation;

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
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.method.JavaMethod;
import org.aikodi.java.core.type.BasicJavaTypeReference;

public class ConstructorSelector extends AbstractConstructorSelector {

	protected class ConstructorSelectionResult extends BasicMethodSelectionResult<NormalMethod> {
		protected ConstructorSelectionResult(NormalMethod template, TypeAssignmentSet assignment, int phase,
				boolean requiredUncheckedConversion) {
			super(template, assignment, phase, requiredUncheckedConversion);
		}

		@Override
		public Type returnType(Method method) throws LookupException {
			if (invocation().isDiamondInvocation()) {
				return method.returnType();
			} else {
				return super.returnType(method);
			}
		}

		@Override
		public NormalMethod finalDeclaration() throws LookupException {
			if (invocation().isDiamondInvocation()) {
				return instantiatedMethodTemplate(template());
			} else {
				return template();
			}
		}

		@Override
		public SelectionResult<NormalMethod> updatedTo(Declaration declaration) {
			return new ConstructorSelectionResult((NormalMethod) declaration, typeAssignment(), phase(),
					requiredUncheckedConversion());
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
		_name = name;
	}

	String _name;

	@Override
	public String selectionName(DeclarationContainer container) {
		return _name;
	}

	public List<? extends SelectionResult<NormalMethod>> selection(List<? extends Declaration> selectionCandidates)
			throws LookupException {
		List<Declaration> withoutNonConstructors = withoutNonConstructors(selectionCandidates);
		boolean diamondInvocation = invocation().isDiamondInvocation();
		if (diamondInvocation) {
			withoutNonConstructors = diamondConstructors(withoutNonConstructors);
		}
		List<SelectionResult<NormalMethod>> selection = (List) super.selection(withoutNonConstructors);
		if (diamondInvocation) {
			List<SelectionResult<NormalMethod>> tmp = selection;
			selection = new ArrayList<>();
			for (SelectionResult<NormalMethod> r : tmp) {
				MethodSelectionResult<NormalMethod> result = (MethodSelectionResult<NormalMethod>) r;
				NormalMethod normalMethod = (NormalMethod) result.finalDeclaration();
				NormalMethod origin = (NormalMethod) normalMethod.origin();
				selection.add(createSelectionResult(origin, result.typeAssignment(), result.phase(),
						result.requiredUncheckedConversion()));
			}
		}
		return selection;
	}

	@Override
	public MethodSelectionResult<NormalMethod> createSelectionResult(NormalMethod method, TypeAssignmentSet typeAssignment, int phase,
			boolean requiredUncheckedConversion) {
		return new ConstructorSelectionResult((NormalMethod)method, typeAssignment, phase, requiredUncheckedConversion);
	}

	public List<Declaration> diamondConstructors(List<? extends Declaration> selectionCandidates) throws LookupException {
		List<Declaration> result = new ArrayList<Declaration>();
		for (Declaration decl : selectionCandidates) {
			result.add(createDiamondConstructorDummy(decl));
		}
		return result;
	}

	private JavaMethod createDiamondConstructorDummy(Declaration decl) {
		JavaMethod method = (JavaMethod) decl.clone();
		ConstructorInvocation invocation = invocation();
		BasicJavaTypeReference returnTypeReference = (BasicJavaTypeReference) method.returnTypeReference();
		method.setUniParent(decl.lexical().parent());
		method.setOrigin(decl);
		Type type = decl.lexical().nearestAncestor(Type.class);
		Java7 language = invocation.language(Java7.class);
		for (TypeParameter param : type.parameters(TypeParameter.class)) {
			method.header().addTypeParameter(Util.clone(param));
			BasicJavaTypeReference tref = language.createTypeReference(param.name());
			returnTypeReference.addArgument(language.createEqualityTypeArgument(tref));
		}
		return method;
	}
	
	@Override
	public boolean isGreedy() {
		return invocation().nbActualParameters() == 0;
	}
}
