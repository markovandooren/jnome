package jnome.workspace;

import java.io.File;


/**
 * A class for creating Java projects from project description files.
 * 
 * Project = 
 * 
 * @author Marko van Dooren
 */
public class JavaProjectFactory {

	public chameleon.workspace.Project createProject(File xmlFile) throws ConfigException {
		ProjectConfig pc = new ProjectConfig(xmlFile.getParentFile());
		pc.readFromXML(xmlFile);
		return pc.project();
	}
		
}
