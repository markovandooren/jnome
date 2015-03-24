package be.kuleuven.cs.distrinet.jnome.core.language;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;

import org.aikodi.chameleon.core.factory.Factory;
import org.aikodi.chameleon.core.language.LanguageFactory;
import org.aikodi.chameleon.input.ModelFactory;
import org.aikodi.chameleon.oo.expression.ExpressionFactory;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.plugin.output.Syntax;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.ProjectConfigurator;

import be.kuleuven.cs.distrinet.jnome.input.JavaExpressionFactory;
import be.kuleuven.cs.distrinet.jnome.input.JavaFactory;
import be.kuleuven.cs.distrinet.jnome.input.JavaModelFactory;
import be.kuleuven.cs.distrinet.jnome.output.JavaSyntax;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfigurator;

public class JavaLanguageFactory implements LanguageFactory {

	public Java7 create() throws ConfigException {
		Java7 result = new Java7();
		result.setPlugin(ModelFactory.class, new JavaModelFactory());
		result.setPlugin(Syntax.class, new JavaSyntax());
		// FIXME: Stupid and inefficient
		result.setPlugin(Factory.class, new JavaFactory());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		result.setPlugin(ExpressionFactory.class, new JavaExpressionFactory());
		JarFile jarName = javaBaseJar();
		result.setPlugin(ProjectConfigurator.class, new JavaProjectConfigurator(jarName));
		return result;
	}

//	public static JarFile javaBaseJarOld() throws ConfigException {
//		URL objectLocation = Object.class.getResource("/java/lang/Object.class");
//		try {
//			JarURLConnection connection = (JarURLConnection) objectLocation.openConnection();
//			return connection.getJarFile();
//		} catch (IOException e) {
//			throw new ConfigException("Cannot locate the jar file for "+Object.class.getName(),e);
//		}
//	}

	public static JarFile javaBaseJar() throws ConfigException {
		try {
			URL url = Object.class.getResource("/java/lang/Object.class");
			String path = URLDecoder.decode(url.getFile(),"UTF-8");
			path = path.substring(5, path.indexOf('!'));
			JarFile jarFile = new JarFile(path);
			return jarFile;
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}


}
