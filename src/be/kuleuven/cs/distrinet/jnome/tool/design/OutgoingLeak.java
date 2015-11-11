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
import org.aikodi.rejuse.exception.Handler;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.tool.IsCollectionType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;

public class OutgoingLeak extends Analysis<ReturnStatement, Verification,Nothing> {

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
			return "Encapsulation error. Outgoing leak of internal state. Method "+_method.name()+ 
					   " in "+t.getFullyQualifiedName()+
					   " is accessible from outside the type hierarchy, and directly returns the collection stored in field "+_member.name();
		}
		
	}

  /**
   * @{inheritDoc}
   */
  @Override
  protected void analyze(ReturnStatement statement) {
    Verification result = Valid.create();
    Method nearestAncestor = statement.nearestAncestor(Method.class);
    if(nearestAncestor != null && 
    		externallyAccessible(statement, nearestAncestor)) {
        try {
            Expression expr = statement.getExpression();
            if(expr instanceof CrossReference) {
                Declaration declaration = ((CrossReference) expr).getElement();
                if(declaration instanceof MemberVariable) {
                    Type type = ((MemberVariable) declaration).getType();
                    if(IsCollectionType.PREDICATE.eval(type)) {
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

	private boolean externallyAccessible(ReturnStatement statement, Method nearestAncestor) {
	Java7 language = statement.language(Java7.class);
	return nearestAncestor.isTrue(language.PUBLIC) ||
			   nearestAncestor.isTrue(language.PACKAGE_ACCESSIBLE);
	}
	
}