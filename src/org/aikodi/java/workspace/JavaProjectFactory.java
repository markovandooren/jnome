package org.aikodi.java.workspace;
//package be.kuleuven.cs.distrinet.jnome.workspace;
//
//import java.io.File;
//
//import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
//import be.kuleuven.cs.distrinet.chameleon.workspace.BootstrapProjectConfig;
//import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
//import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectFactory;
//import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
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
