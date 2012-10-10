package jnome.eclipse;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import chameleon.core.language.Language;
import chameleon.eclipse.connector.EclipseBootstrapper;
import chameleon.eclipse.connector.EclipseEditorExtension;
import chameleon.workspace.ProjectException;

public class Bootstrapper extends EclipseBootstrapper {

	public final static String PLUGIN_ID = "be.chameleon.eclipse.java";
	
	public Bootstrapper() {
		super("Java","1.6",PLUGIN_ID);
	}
	
	public Language createLanguage() throws ProjectException {
		Java result = new JavaLanguageFactory().create();
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension(getLanguageName()));
		return result;
	}

}
