package org.aikodi.java.eclipse;

import org.aikodi.chameleon.analysis.dependency.DependencyOptionsFactory;
import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.eclipse.connector.EclipseBootstrapper;
import org.aikodi.chameleon.eclipse.connector.EclipseEditorExtension;
import org.aikodi.chameleon.eclipse.connector.EclipseProjectLoader;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.java.analysis.dependency.JavaDependencyOptionsFactory;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.language.Java7LanguageFactory;

public class Bootstrapper extends EclipseBootstrapper {

	public final static String PLUGIN_ID = "org.aikodi.java.eclipse";
	
	public Language createLanguage() throws ProjectException {
		Java7 result = new Java7LanguageFactory().create();
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension());
		result.setPlugin(EclipseProjectLoader.class, new JDTProjectLoader());
		result.setPlugin(DependencyOptionsFactory.class, new JavaDependencyOptionsFactory());
		return result;
	}

}
