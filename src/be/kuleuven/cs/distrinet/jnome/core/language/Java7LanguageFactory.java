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
import be.kuleuven.cs.distrinet.jnome.input.Java7Factory;
import be.kuleuven.cs.distrinet.jnome.input.JavaModelFactory;
import be.kuleuven.cs.distrinet.jnome.output.Java7Syntax;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfigurator;

/**
 * A factory for Java7.
 * 
 * @author Marko van Dooren
 */
public class Java7LanguageFactory implements LanguageFactory {

  /**
   * {@inheritDoc}
   * 
   * <p>The resulting language object has the following plugins:</p>
   * <ol>
   *   <li>{@link ModelFactory} : {@link JavaModelFactory}</li>
   *   <li>{@link Syntax} : {@link Java7Syntax}</li>
   *   <li>{@link Factory} : {@link Java7Factory}</li>
   *   <li>{@link ObjectOrientedFactory} : {@link Java7Factory}</li>
   *   <li>{@link ExpressionFactory} : {@link JavaExpressionFactory}</li>
   *   <li>{@link ProjectConfigurator} : {@link JavaProjectConfigurator}</li>
   * </ol>
   */
	public Java7 create() throws ConfigException {
		Java7 result = new Java7();
		result.setPlugin(ModelFactory.class, new JavaModelFactory());
		result.setPlugin(Syntax.class, new Java7Syntax());
		// FIXME: Stupid and inefficient
		result.setPlugin(Factory.class, new Java7Factory());
		result.setPlugin(ObjectOrientedFactory.class, new Java7Factory());
		result.setPlugin(ExpressionFactory.class, new JavaExpressionFactory());
		JarFile jarName = javaBaseJar();
		result.setPlugin(ProjectConfigurator.class, new JavaProjectConfigurator(jarName));
		return result;
	}

	/**
	 * <p>Determine the jar file that contains the base classes from the
	 * JVM that runs this code.</p>
	 * 
	 * @return the jar file that contains the base classes from the
   * JVM that runs this code.
   * 
	 * @throws ConfigException The jar file could not be found.
	 */
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
