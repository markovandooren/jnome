package be.kuleuven.cs.distrinet.jnome.eclipse;

import org.aikodi.chameleon.analysis.dependency.DependencyOptionsFactory;
import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.eclipse.connector.EclipseBootstrapper;
import org.aikodi.chameleon.eclipse.connector.EclipseEditorExtension;
import org.aikodi.chameleon.eclipse.connector.EclipseProjectLoader;
import org.aikodi.chameleon.workspace.ProjectException;

import be.kuleuven.cs.distrinet.jnome.analysis.dependency.JavaDependencyOptionsFactory;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7LanguageFactory;

public class Bootstrapper extends EclipseBootstrapper {

	public final static String PLUGIN_ID = "be.chameleon.eclipse.java";
	
	public Language createLanguage() throws ProjectException {
		Java7 result = new Java7LanguageFactory().create();
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension());
		result.setPlugin(EclipseProjectLoader.class, new JDTProjectLoader());
		result.setPlugin(DependencyOptionsFactory.class, new JavaDependencyOptionsFactory());
		return result;
	}

}
