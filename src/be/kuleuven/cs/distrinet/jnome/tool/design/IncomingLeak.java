package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.validation.AtomicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.oo.variable.MemberVariable;
import org.aikodi.chameleon.support.expression.AssignmentExpression;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.tool.IsCollectionType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.tree.TreeStructure;

public class IncomingLeak extends Analysis<AssignmentExpression, Verification> {

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
			Method m = _parameter.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			String msg = "Warning: encapsulation: potential incoming leak of internal state: collection parameter "+_parameter.name()+ 
					         " of public method "+m.name()+" in "+t.getFullyQualifiedName()+ 
					         " is directly assigned to field "+_member.name();
			return msg;
		}
		
	}

	@Override
	public void analyze(AssignmentExpression assignment) throws Nothing {
		Verification result = Valid.create();
		try {
			Method method = assignment.nearestAncestor(Method.class);
			if(method != null && method.isTrue(method.language(Java7.class).PUBLIC)) {
				Variable v = assignment.variable();
				if(v instanceof MemberVariable) {
					Expression e = assignment.getValue();
					if(e instanceof CrossReference) {
						Declaration rhs = ((CrossReference) e).getElement();
						if(rhs instanceof FormalParameter) {
							Type type_of_value = ((FormalParameter)rhs).getType();
              if(IsCollectionType.PREDICATE.eval(type_of_value)) {
								result = result.and(new IncomingCollectionEncapsulationViolationResult(v,(FormalParameter) rhs));
							}
						}
					}
				}
			}
		}
		catch(LookupException exc) {
			// swallow for now.
		}
		setResult(result().and(result));
	}
}
