package jnome.core.language;

import jnome.input.JavaFactory;
import jnome.input.JavaModelFactory;
import jnome.output.JavaCodeWriter;
import chameleon.core.namespace.NamespaceFactory;
import chameleon.core.namespace.RegularNamespaceFactory;
import chameleon.input.ModelFactory;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.plugin.output.Syntax;

public class JavaLanguageFactory {

	public Java create() {
		Java result = new Java();
		result.setPlugin(NamespaceFactory.class, new RegularNamespaceFactory());
		result.setPlugin(ModelFactory.class, new JavaModelFactory());
		result.setPlugin(Syntax.class, new JavaCodeWriter());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		return result;
	}
}
