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
		super("Java","1.5","java",PLUGIN_ID);
	}
	
	public Language createLanguage() throws ProjectException {
		String extension = ".java";
		Java result = new JavaLanguageFactory().create();
		
//		JavaFileInputSourceFactory factory = new JavaFileInputSourceFactory();
//		View view = new View(new RootNamespace(new RegularNamespaceFactory()), result);
//		Project project = new Project("Chameleon Eclipse project", view, new File("."));
//		view.addSource(new DirectoryLoader(extension,null, factory));
//		try {
//		  loadAPIFiles(extension, PLUGIN_ID, view, factory);
//		} catch(ChameleonProgrammerException exc) {
//			// Object and String may not be present yet.
//		}
		
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension(getLanguageName()));
		return result;
	}

}
