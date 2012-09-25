package jnome.eclipse;

import java.io.IOException;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.JavaFactory;
import jnome.input.JavaFileInputSourceFactory;
import jnome.output.JavaCodeWriter;
import chameleon.core.language.Language;
import chameleon.core.namespace.RegularNamespaceFactory;
import chameleon.core.namespace.RootNamespace;
import chameleon.eclipse.connector.EclipseBootstrapper;
import chameleon.eclipse.connector.EclipseEditorExtension;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.plugin.output.Syntax;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

public class Bootstrapper extends EclipseBootstrapper {

	public final static String PLUGIN_ID = "be.chameleon.eclipse.java";
	
	public Bootstrapper() {
		super("Java","1.5","java",PLUGIN_ID);
	}
	
	public Language createLanguage() throws IOException, ParseException, ProjectException {
		String extension = ".java";
		Java result = new JavaLanguageFactory().create();
		Project project = new Project("Chameleon Eclipse project", new RootNamespace(new RegularNamespaceFactory()), result);
		JavaFileInputSourceFactory factory = new JavaFileInputSourceFactory(result.plugin(ModelFactory.class));
		project.addSource(new DirectoryLoader(extension,null, factory));
		try {
		  loadAPIFiles(extension, PLUGIN_ID, project, factory);
		} catch(ChameleonProgrammerException exc) {
			// Object and String may not be present yet.
		}
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension(getLanguageName()));
		result.setPlugin(Syntax.class, new JavaCodeWriter());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		return result;
	}

}
