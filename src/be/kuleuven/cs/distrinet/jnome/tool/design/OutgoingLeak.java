package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.validation.AtomicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.MemberVariable;
import org.aikodi.chameleon.support.statement.ReturnStatement;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.tool.IsCollectionType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.tree.TreeStructure;

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
	protected <X extends ReturnStatement> void doPerform(TreeStructure<X> tree) throws Nothing {
	  ReturnStatement statement = tree.node();
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