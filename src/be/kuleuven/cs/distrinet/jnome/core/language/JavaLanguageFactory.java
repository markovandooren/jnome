package be.kuleuven.cs.distrinet.jnome.core.language;

import java.net.URL;

import be.kuleuven.cs.distrinet.jnome.input.JavaFactory;
import be.kuleuven.cs.distrinet.jnome.input.JavaModelFactory;
import be.kuleuven.cs.distrinet.jnome.output.JavaCodeWriter;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.core.factory.Factory;
import be.kuleuven.cs.distrinet.chameleon.core.language.LanguageFactory;
import be.kuleuven.cs.distrinet.chameleon.input.ModelFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.plugin.ObjectOrientedFactory;
import be.kuleuven.cs.distrinet.chameleon.plugin.output.Syntax;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;

public class JavaLanguageFactory implements LanguageFactory {

	public Java create() {
		Java result = new Java();
		result.setPlugin(ModelFactory.class, new JavaModelFactory());
		result.setPlugin(Syntax.class, new JavaCodeWriter());
		// FIXME: Stupid and ineffient
		result.setPlugin(Factory.class, new JavaFactory());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		URL objectLocation = Object.class.getResource("/java/lang/Object.class");
		String fileName = objectLocation.getFile();
		String jarName = fileName.substring(5,fileName.indexOf('!'));
		result.setPlugin(ProjectConfigurator.class, new JavaProjectConfigurator(jarName));
		return result;
	}
}
