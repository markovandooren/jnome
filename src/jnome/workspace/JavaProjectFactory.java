package jnome.workspace;

import java.io.File;

import jnome.input.LazyJavaFileInputSourceFactory;
import chameleon.workspace.ConfigException;
import chameleon.workspace.ProjectConfig;
import chameleon.workspace.ProjectFactory;


/**
 * A class for creating Java projects from project description files.
 * 
 * @author Marko van Dooren
 */
public class JavaProjectFactory extends ProjectFactory {

	@Override
	public chameleon.workspace.Project createProject(File xmlFile) throws ConfigException {
		ProjectConfig pc = new JavaProjectConfig(xmlFile.getParentFile(),new LazyJavaFileInputSourceFactory());
		pc.readFromXML(xmlFile);
		return pc.project();
	}
		
}
