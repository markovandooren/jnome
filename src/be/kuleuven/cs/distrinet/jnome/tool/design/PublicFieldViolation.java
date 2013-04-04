package be.kuleuven.cs.distrinet.jnome.tool.design;

import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

public class PublicFieldViolation extends SafePredicate<VariableDeclaration> {

	@Override
	public boolean eval(VariableDeclaration declaration) {
			Java language = declaration.language(Java.class);
			Variable variable = declaration.variable();
			if(variable.isTrue(language.PUBLIC) && variable.isTrue(language.INSTANCE)) {
				return true;
			}
		return false;
	}

}
