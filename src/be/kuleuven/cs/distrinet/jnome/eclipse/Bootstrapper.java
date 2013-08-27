package be.kuleuven.cs.distrinet.jnome.eclipse;

import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyOptionsFactory;
import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.eclipse.connector.EclipseBootstrapper;
import be.kuleuven.cs.distrinet.chameleon.eclipse.connector.EclipseEditorExtension;
import be.kuleuven.cs.distrinet.chameleon.eclipse.connector.EclipseProjectLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.jnome.analysis.dependency.JavaDependencyOptionsFactory;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;

public class Bootstrapper extends EclipseBootstrapper {

	public final static String PLUGIN_ID = "be.chameleon.eclipse.java";
	
	public Language createLanguage() throws ProjectException {
		Java result = new JavaLanguageFactory().create();
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension());
		result.setPlugin(EclipseProjectLoader.class, new JDTProjectLoader());
		result.setPlugin(DependencyOptionsFactory.class, new JavaDependencyOptionsFactory());
		return result;
	}

}
