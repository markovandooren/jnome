package jnome.core.language;

import java.net.URL;

import jnome.input.JavaFactory;
import jnome.input.JavaModelFactory;
import jnome.output.JavaCodeWriter;
import jnome.workspace.JavaConfigLoader;
import chameleon.core.language.LanguageFactory;
import chameleon.input.ModelFactory;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.plugin.output.Syntax;
import chameleon.workspace.ConfigLoader;

public class JavaLanguageFactory implements LanguageFactory {

	public Java create() {
		Java result = new Java();
		result.setPlugin(ModelFactory.class, new JavaModelFactory());
		result.setPlugin(Syntax.class, new JavaCodeWriter());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		URL objectLocation = Object.class.getResource("/java/lang/Object.class");
		String fileName = objectLocation.getFile();
		String jarName = fileName.substring(5,fileName.indexOf('!'));
		result.setPlugin(ConfigLoader.class, new JavaConfigLoader(jarName));
		return result;
	}
}
