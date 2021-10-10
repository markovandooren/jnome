package org.aikodi.java.tool.design;

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
import org.aikodi.chameleon.oo.variable.RegularMemberVariable;
import org.aikodi.chameleon.support.statement.ReturnStatement;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.tool.Predicates;

public class OutgoingLeak extends Analysis<ReturnStatement, Verification,LookupException> {

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
			Type t = _method.lexical().nearestAncestor(Type.class);
			return "Encapsulation error. Outgoing leak of internal state. Method "+_method.name()+ 
					   " in "+t.getFullyQualifiedName()+
					   " is accessible from outside the type hierarchy, and directly returns the collection stored in field "+_member.name();
		}
		
	}

  /**
   * @{inheritDoc}
   */
  @Override
  protected void analyze(ReturnStatement statement) throws LookupException {
    Verification result = Valid.create();
    Method nearestAncestor = statement.lexical().nearestAncestor(Method.class);
    if(nearestAncestor != null && 
    		Predicates.EXTERNALLY_ACCESSIBLE.eval(nearestAncestor)) {
            Expression expr = statement.getExpression();
            if(expr instanceof CrossReference) {
                Declaration declaration = ((CrossReference) expr).getElement();
            		Java7 language = statement.language(Java7.class);
                if(declaration instanceof RegularMemberVariable && declaration.is(language.INSTANCE()).isTrue()) {
                    Type type = ((RegularMemberVariable) declaration).getType();
                    if((!Predicates.IMMUTABLE_COLLECTION.eval(type)) && Predicates.COLLECTION.eval(type)) {
                        result = new OutgoingCollectionEncapsulationViolation(nearestAncestor, (Variable) declaration);
                    }
                }
            }
    }
    setResult(result().and(result));
  }

}