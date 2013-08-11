package be.kuleuven.cs.distrinet.jnome.tool.design;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analysis;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.validation.AtomicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ReturnStatement;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.tool.IsCollectionType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;

public class OutgoingLeak extends Analysis<ReturnStatement, Verification> {

	public OutgoingLeak() {
		super(ReturnStatement.class, Valid.create());
	}

	private static class OutgoingCollectionEncapsulationViolation extends AtomicProblem{

		public OutgoingCollectionEncapsulationViolation(Method method, Variable member) {
			super(method);
			this._method = method;
			this._member = member;
		}
		private Method _method;

		private Variable _member;
		
		@Override
		public String message() {
			Type t = _method.nearestAncestor(Type.class);
			return "Error: encapsulation: outgoing leak of internal state: public method "+_method.name()+ 
					   " in "+t.getFullyQualifiedName()+
					   " directly returns the collection stored in field "+_member.name();
		}
		
	}

	@Override
	protected void doPerform(ReturnStatement statement) throws Nothing {
		Verification result = Valid.create();
		Method nearestAncestor = statement.nearestAncestor(Method.class);
		if(nearestAncestor != null && nearestAncestor.isTrue(statement.language(Java.class).PUBLIC)) {
			try {
				Expression expr = statement.getExpression();
				if(expr instanceof CrossReference) {
					Declaration declaration = ((CrossReference) expr).getElement();
					if(declaration instanceof MemberVariable) {
						Type type = ((MemberVariable) declaration).getType();
						if(new IsCollectionType().eval(type)) {
							result = new OutgoingCollectionEncapsulationViolation(nearestAncestor, (Variable) declaration);
						}
					}
				}
			}catch(LookupException exc) {
				exc.printStackTrace();
			}
		}
		setResult(result().and(result));
	}
	
}