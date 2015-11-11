package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.expression.AssignmentExpression;
import org.aikodi.chameleon.support.statement.StatementExpression;

import be.kuleuven.cs.distrinet.rejuse.action.Nothing;

public class AssignmentAsExpression extends Analysis<AssignmentExpression, Verification,Nothing>{

  public AssignmentAsExpression() {
    super(AssignmentExpression.class, Valid.create());
  }

  public static class AssignmentUsedAsExpression extends BasicProblem {

    public AssignmentUsedAsExpression(AssignmentExpression element) {
      super(element, msg(element));
    }

    private static String msg(AssignmentExpression element) {
    	Type type = element.nearestAncestor(Type.class);
    	Method method = element.nearestAncestor(Method.class);
    	String methodName = method == null ? "" : "method "+method.name()+" of ";
    	return "An assignment to variable "+element.variableExpression().toString()+" in " + methodName  +"class "+type.getFullyQualifiedName()+" is used as an expression. This can have unintented effects.";
    }

  }
  
  /**
   * @{inheritDoc}
   */
  @Override
  protected void analyze(AssignmentExpression element) {
    if(!(element.parent() instanceof StatementExpression)) {
      setResult(result().and(new AssignmentUsedAsExpression(element)));
    }
  }

}
