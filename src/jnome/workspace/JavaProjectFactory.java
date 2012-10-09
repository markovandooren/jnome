//package jnome.workspace;
//
//import java.io.File;
//
//import jnome.input.LazyJavaFileInputSourceFactory;
//import chameleon.workspace.BootstrapProjectConfig;
//import chameleon.workspace.ConfigException;
//import chameleon.workspace.ProjectFactory;
//import chameleon.workspace.Workspace;
//
//
///**
// * A class for creating Java projects from project description files.
// * 
// * @author Marko van Dooren
// */
//public class JavaProjectFactory extends ProjectFactory {
//
//	@Override
//	public chameleon.workspace.Project createProject(File xmlFile, Workspace workspace) throws ConfigException {
//		BootstrapProjectConfig pc = new BootstrapProjectConfig(xmlFile.getParentFile(), workspace.languageRepository());
//		pc.readFromXML(xmlFile);
//		return pc.project();
//	}
//		
//}
