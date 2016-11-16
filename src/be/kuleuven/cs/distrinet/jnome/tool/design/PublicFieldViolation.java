package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.VariableDeclaration;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.tree.TreeStructure;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class PublicFieldViolation extends Analysis<VariableDeclaration, Verification,Nothing> {

	public PublicFieldViolation() {
		super(VariableDeclaration.class, Valid.create());
	}

	@Override
	protected void analyze(VariableDeclaration declaration) {
		Verification result = Valid.create();
		Java7 language = declaration.language(Java7.class);
		Variable variable = declaration.variable();
		if(variable.isTrue(language.PUBLIC) && variable.isTrue(language.INSTANCE) && (variable.isFalse(language.FINAL))) {
			String message = "Encapsulation error. Non-final instance variable "+variable.name() +
					" in class "+variable.nearestAncestor(Type.class).getFullyQualifiedName()+" is public.";
			result = new BasicProblem(declaration, message);
		}
		setResult(result().and(result));
	}

}
