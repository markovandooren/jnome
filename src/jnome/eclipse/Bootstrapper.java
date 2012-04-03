package jnome.eclipse;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import jnome.core.language.Java;
import jnome.eclipse.JavaEditorExtension;
import jnome.input.JavaFactory;
import jnome.input.JavaModelFactory;
import jnome.output.JavaCodeWriter;
import chameleon.core.language.Language;
import chameleon.eclipse.LanguageMgt;
import chameleon.eclipse.connector.EclipseBootstrapper;
import chameleon.eclipse.connector.EclipseEditorExtension;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.plugin.output.Syntax;

public class Bootstrapper extends EclipseBootstrapper {

	public final static String PLUGIN_ID = "be.chameleon.eclipse.java";
	
	public String getLanguageName() {
		return "Java";
	}

	public void registerFileExtensions() {
		addExtension("java");
	}

	public String getLanguageVersion() {
		return "1.5";
	}

	public String getVersion() {
		return "Jnome build Auguest 27 2009";
	}

	public String getDescription() {
		return "Jnome: a Chameleon model for Java";
	}

	public String getLicense() {
		return "";
	}

	public Syntax getCodeWriter() {
		return new JavaCodeWriter();
	}

	public Language createLanguage() throws IOException, ParseException {
		Java result = new Java();
		ModelFactory factory = new JavaModelFactory(result);
		factory.setLanguage(result, ModelFactory.class);

		try {
			FilenameFilter filter = LanguageMgt.fileNameFilter(".java");
			URL directory = LanguageMgt.pluginURL(PLUGIN_ID, "api/");
			List<File> files = LanguageMgt.allFiles(directory, filter);
			System.out.println("Loading "+files.size()+" API files.");
		  factory.initializeBase(files);
		} catch(ChameleonProgrammerException exc) {
			// Object and String may not be present yet.
		}

		
//		factory.initializeBase(new ArrayList<File>());
		
		result.setPlugin(EclipseEditorExtension.class, new JavaEditorExtension());
		result.setPlugin(Syntax.class, new JavaCodeWriter());
		result.setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		return result;
	}

}
