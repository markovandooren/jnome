package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.support.expression.AssignmentExpression;
import org.aikodi.chameleon.support.statement.StatementExpression;

public class AssignmentAsExpression extends Analysis<AssignmentExpression, Verification>{

	public AssignmentAsExpression() {
		super(AssignmentExpression.class, Valid.create());
	}

	public static class AssignmentUsedAsExpression extends BasicProblem {

	public AssignmentUsedAsExpression(Element element) {
		super(element, "The assignment is used as an expression. This can have unintented effects.");
	}
		
	}
	
	@Override
	protected void analyze(AssignmentExpression element) {
		if(!(element.parent() instanceof StatementExpression)) {
			setResult(result().and(new AssignmentUsedAsExpression(element)));
		}
	}

}
