package org.aikodi.java.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.validation.AtomicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.oo.variable.RegularMemberVariable;
import org.aikodi.chameleon.support.expression.AssignmentExpression;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.tool.Predicates;


public class IncomingLeak extends Analysis<AssignmentExpression, Verification, ModelException> {

	public IncomingLeak() {
		super(AssignmentExpression.class, Valid.create());
	}

	private static class IncomingCollectionEncapsulationViolationResult extends AtomicProblem {

		public IncomingCollectionEncapsulationViolationResult(Variable member, FormalParameter parameter) {
			super(parameter);
			_member = member;
			_parameter = parameter;
		}

		private Variable _member;

		private FormalParameter _parameter;

		@Override
		public String message() {
			Method m = _parameter.lexical().nearestAncestor(Method.class);
			Type t = m.lexical().nearestAncestor(Type.class);
			String msg = "Encapsulation error. Potential incoming leak of internal state: collection parameter "+_parameter.name()+ 
					" of public method "+m.name()+" in "+t.getFullyQualifiedName()+ 
					" is directly assigned to field "+_member.name();
			return msg;
		}

	}

	@Override
	public void analyze(AssignmentExpression assignment) throws LookupException {
		Verification result = Valid.create();
		Method method = assignment.lexical().nearestAncestor(Method.class);
		Java7 language = method.language(Java7.class);
		if(method != null && Predicates.EXTERNALLY_ACCESSIBLE.eval(method)) {
			Variable v = assignment.variable();
			if(v instanceof RegularMemberVariable && v.is(language.INSTANCE()).isTrue()) {
				Expression e = assignment.getValue();
				if(e instanceof CrossReference) {
					Declaration rhs = ((CrossReference) e).getElement();
					if(rhs instanceof FormalParameter) {
						Type type_of_value = ((FormalParameter)rhs).getType();
						if((!Predicates.IMMUTABLE_COLLECTION.eval(type_of_value)) && Predicates.COLLECTION.eval(type_of_value)) {
							result = result.and(new IncomingCollectionEncapsulationViolationResult(v,(FormalParameter) rhs));
						}
					}
				}
			}
		}

		setResult(result().and(result));
	}

}
