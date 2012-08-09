package jnome.eclipse;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.JavaFactory;
import jnome.input.JavaModelFactory;
import jnome.output.JavaCodeWriter;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.language.Language;
import chameleon.core.namespace.RootNamespace;
import chameleon.eclipse.LanguageMgt;
import chameleon.eclipse.connector.EclipseBootstrapper;
import chameleon.eclipse.connector.EclipseEditorExtension;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.plugin.output.Syntax;
import chameleon.test.provider.DirectoryProjectBuilder;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectBuilder;

public class Bootstrapper extends EclipseBootstrapper {

	public final static String PLUGIN_ID = "be.chameleon.eclipse.java";
	
	public Bootstrapper() {
		super("Java","1.5","java");
	}
	
	public Language createLanguage() throws IOException, ParseException {
		String extension = ".java";
		Java result = new JavaLanguageFactory().create();
		Project project = new Project("x", new RootNamespace(new SimpleNameSignature("")), result);
		ProjectBuilder builder = new DirectoryProjectBuilder(project, extension);
		try {
			FilenameFilter filter = LanguageMgt.fileNameFilter(extension);
			URL directory = LanguageMgt.pluginURL(PLUGIN_ID, "api/");
			List<File> files = LanguageMgt.allFiles(directory, filter);
			System.out.println("Loading "+files.size()+" API files.");
		  builder.initializeBase(files);
		} catch(ChameleonProgrammerException exc) {
			// Object and String may not be present yet.
		}
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension(getLanguageName()));
		result.setPlugin(Syntax.class, new JavaCodeWriter());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		return result;
	}

}
