package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyRootNamespace;
import be.kuleuven.cs.distrinet.chameleon.eclipse.connector.EclipseProjectLoader;
import be.kuleuven.cs.distrinet.chameleon.eclipse.util.Files;
import be.kuleuven.cs.distrinet.chameleon.eclipse.util.Projects;
import be.kuleuven.cs.distrinet.chameleon.eclipse.util.Workspaces;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePlugin;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePluginImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectoryLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoaderImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.jnome.input.PredefinedElementsFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JarLoader;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

public class JDTProjectLoader extends LanguagePluginImpl implements EclipseProjectLoader {

	@Override
	public boolean canLoad(IProject project) {
		return Projects.hasNature(project, JavaCore.NATURE_ID);
	}
	
	@SuppressWarnings("unused")
	@Override
	public Project load(IProject jdtProject) {
		try {
			// 1. Obtain the root directory of the project
			File root = jdtProject.getLocation().toFile();
			// 2. Obtain the name of the project.
			String projectName = jdtProject.getName();
			System.out.println("Loading Eclipse project: "+projectName);
			// 3. Create a Java view
			Java java = (Java) language();
			JavaView view = new JavaView(new LazyRootNamespace(), java);
			
			// 4. Create a Chameleon project
			Project chameleonProject = new Project(projectName, root, view);
			
			// 5. Set up the source and binary loaders.
			// FIXME This won't work flawlessly because we cannot yet properly simulate
			// a classpath.
			IJavaProject nature = (IJavaProject) jdtProject.getNature(JavaCore.NATURE_ID);
			IClasspathEntry[] rawClasspath = nature.getRawClasspath();
			addLoaders(view, jdtProject, rawClasspath,false);
			DocumentLoader loader = new DocumentLoaderImpl() {
				@Override
				public String label() {
				  return "Java built-in types";
				}
			};
			try {
				view.addBinary(loader);
				new PredefinedElementsFactory(view, loader).initializePredefinedElements();
			} catch(ProjectException exc) {
				// This goes wrong when no Java standard library is in the classpath.
				exc.printStackTrace();
			}
			System.out.println("Done loading JDT project.");
			return chameleonProject;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void addLoaders(JavaView view, IProject jdtProject, IClasspathEntry[] entries, boolean inContainer) throws CoreException {
		
		IJavaProject nature = (IJavaProject) jdtProject.getNature(JavaCore.NATURE_ID);
		
		Java java = (Java) language();
		Predicate<? super String, Nothing> sourceFileFilter = java.plugin(ProjectConfigurator.class).sourceFileFilter();
		Predicate<? super String, Nothing> binaryFileFilter = java.plugin(ProjectConfigurator.class).binaryFileFilter();
		for(IClasspathEntry entry: entries) {
			IPath path = entry.getPath();
			switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_SOURCE:
				IPath projectRelativePath = Workspaces.root().findMember(path).getProjectRelativePath();
				String sourceRoot = projectRelativePath.toString();
				DirectoryLoader loader = new DirectoryLoader(sourceRoot, sourceFileFilter, new LazyJavaFileInputSourceFactory());
				try {
					if(view.canAddSource(loader)) {
						view.addSource(loader);
					} else {
						System.out.println("Not adding source loader twice.");
					}
				} catch(ProjectException exc) {
					exc.printStackTrace();
				}
				break;
			case IClasspathEntry.CPE_CONTAINER:
				IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(path, nature);
				addLoaders(view, jdtProject, classpathContainer.getClasspathEntries(),true);
				break;
			case IClasspathEntry.CPE_LIBRARY:
				try {
					File libfile = null;
					// Not sure whether this is 100% correct.
					// We get a dumb Path object that is also absolute
					// when it is specified _relative_ to the workspace
					// Therefore, we cannot make the distinction between
					// true absolute paths and relative paths.
					// Within containers, the paths appear to be absolute,
					// so we perform that test to avoid using a project jar
					// when we should actually use the absolute file.
					// Chance of problem is tiny, but still.
					if(! inContainer) {
						IResource resource = Workspaces.root().findMember(path);
						if(resource instanceof IFile && ((IFile)resource).exists()) {
							libfile = Files.workspaceFileToAbsoluteFile(path);
						}
					} 
					if(libfile == null){
						libfile = new File(path.toString());
					}
					JarLoader jarLoader = new JarLoader(new JarFile(libfile), binaryFileFilter);
					try {
						if(view.canAddBinary(jarLoader)) {
							view.addBinary(jarLoader);
						} else {
							System.out.println("Not adding binary loader twice.");
						}
					} catch (ProjectException e) {
						e.printStackTrace();
					}
				} catch(IOException exc) {
					exc.printStackTrace();
				}
				break;
			case IClasspathEntry.CPE_PROJECT:
				IProject dependency = Projects.project(path);
				IJavaProject dependencyJDTProject = (IJavaProject) dependency.getNature(JavaCore.NATURE_ID);
				addLoaders(view, dependency, dependencyJDTProject.getRawClasspath(),false);
				break;
			default:
				break;
			}
		}
		
		
//		IClasspathEntry junit = entries[2];
//		IPath path = junit.getPath();
//		IClasspathEntry referencingEntry = junit.getReferencingEntry();
//		IPath outputLocation = junit.getOutputLocation();
//		IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(path, nature);
//		IClasspathEntry[] classpathEntries = classpathContainer.getClasspathEntries();
//		IPath path2 = classpathEntries[0].getPath();
//		IPath path3 = classpathEntries[1].getPath();
//		System.out.println("debug");

	}

	@Override
	public LanguagePluginImpl clone() {
		return new JDTProjectLoader();
	}

}
