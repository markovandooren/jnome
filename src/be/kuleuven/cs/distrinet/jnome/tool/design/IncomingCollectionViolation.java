package be.kuleuven.cs.distrinet.jnome.tool.design;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.validation.AtomicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.support.expression.AssignmentExpression;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.tool.IsCollectionType;

public class IncomingCollectionViolation extends Analysis<AssignmentExpression, VerificationResult> {

	public IncomingCollectionViolation() {
		super(AssignmentExpression.class);
	}

	@Override
	protected VerificationResult analyse(AssignmentExpression assignment) {
		VerificationResult result = Valid.create();
		try {
			Method method = assignment.nearestAncestor(Method.class);
			if(method != null && method.isTrue(method.language(Java.class).PUBLIC)) {
				Variable v = assignment.variable();
				if(v instanceof MemberVariable) {
					Expression e = assignment.getValue();
					if(e instanceof CrossReference) {
						Declaration rhs = ((CrossReference) e).getElement();
						if(rhs instanceof FormalParameter) {
							Type type_of_value = ((FormalParameter)rhs).getType();
							if(new IsCollectionType().eval(type_of_value)) {
								result = result.and(new IncomingCollectionViolationResult(v,(FormalParameter) rhs));
							}
						}
					}
				}
			}
		}
		catch(LookupException exc) {
			// swallow for now.
		}
		return result;
	}
	
	private static class IncomingCollectionViolationResult extends AtomicProblem {

		public IncomingCollectionViolationResult(Variable member, FormalParameter parameter) {
			super(parameter);
			_member = member;
			_parameter = parameter;
		}
		
		private Variable _member;
		
		private FormalParameter _parameter;

		@Override
		public String message() {
			Method m = _parameter.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			String msg = "Error: encapsulation: collection parameter "+_parameter.name()+ 
					         " of public method "+m.name()+" in "+t.getFullyQualifiedName()+ 
					         " is directly assigned to field "+_member.name();
			return msg;
		}
		
	}
}
