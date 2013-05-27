package be.kuleuven.cs.distrinet.jnome.core.language;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.core.factory.Factory;
import be.kuleuven.cs.distrinet.chameleon.core.language.LanguageFactory;
import be.kuleuven.cs.distrinet.chameleon.input.ModelFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.plugin.ObjectOrientedFactory;
import be.kuleuven.cs.distrinet.chameleon.plugin.output.Syntax;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.jnome.input.JavaFactory;
import be.kuleuven.cs.distrinet.jnome.input.JavaModelFactory;
import be.kuleuven.cs.distrinet.jnome.output.JavaCodeWriter;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfigurator;

public class JavaLanguageFactory implements LanguageFactory {

	public Java create() throws ConfigException {
		Java result = new Java();
		result.setPlugin(ModelFactory.class, new JavaModelFactory());
		result.setPlugin(Syntax.class, new JavaCodeWriter());
		// FIXME: Stupid and inefficient
		result.setPlugin(Factory.class, new JavaFactory());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		JarFile jarName = javaBaseJar();
		result.setPlugin(ProjectConfigurator.class, new JavaProjectConfigurator(jarName));
		return result;
	}

	public static JarFile javaBaseJar() throws ConfigException {
		URL objectLocation = Object.class.getResource("/java/lang/Object.class");
		try {
			JarURLConnection connection = (JarURLConnection) objectLocation.openConnection();
			return connection.getJarFile();
		} catch (IOException e) {
			throw new ConfigException("Cannot locate the jar file for "+Object.class.getName(),e);
		}
	}

	public static JarFile jarFile(Class klazz) throws ConfigException {
		URL objectLocation = klazz.getProtectionDomain().getCodeSource().getLocation();
		try {
			JarURLConnection connection = (JarURLConnection) objectLocation.openConnection();
			return connection.getJarFile();
		} catch (IOException e) {
			throw new ConfigException("Cannot locate the jar file for "+klazz.getName(),e);
		}
	}
}
