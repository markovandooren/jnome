package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.VariableDeclaration;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;

public class PublicFieldViolation extends Analysis<VariableDeclaration, Verification> {

	public PublicFieldViolation() {
		super(VariableDeclaration.class, Valid.create());
	}

	@Override
	protected void doPerform(VariableDeclaration declaration) throws Nothing {
		Verification result = Valid.create();
		Java language = declaration.language(Java.class);
		Variable variable = declaration.variable();
		if(variable.isTrue(language.PUBLIC) && variable.isTrue(language.INSTANCE) && (variable.isFalse(language.FINAL))) {
			String message = "Error: encapsulation: non-final member variable "+variable.name() +
					" in class "+variable.nearestAncestor(Type.class).getFullyQualifiedName()+" is public.";
			result = new BasicProblem(declaration, message);
		}
		setResult(result().and(result));
	}

}
