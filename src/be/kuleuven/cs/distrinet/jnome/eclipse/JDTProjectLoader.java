package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.core.namespace.LazyRootNamespace;
import org.aikodi.chameleon.eclipse.connector.EclipseProjectLoader;
import org.aikodi.chameleon.eclipse.util.Files;
import org.aikodi.chameleon.eclipse.util.Projects;
import org.aikodi.chameleon.eclipse.util.Workspaces;
import org.aikodi.chameleon.plugin.LanguagePlugin;
import org.aikodi.chameleon.plugin.LanguagePluginImpl;
import org.aikodi.chameleon.workspace.CompositeDocumentScanner;
import org.aikodi.chameleon.workspace.DirectoryScanner;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.DocumentScannerContainer;
import org.aikodi.chameleon.workspace.DocumentScannerImpl;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectConfigurator;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.predicate.Predicate;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.jnome.input.PredefinedElementsFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JarScanner;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

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
			Java7 java = (Java7) language();
			JavaView view = new JavaView(new LazyRootNamespace(), java);
			
			// 4. Create a Chameleon project
			Project chameleonProject = new Project(projectName, root, view);
			
			// 5. Set up the source and binary loaders.
			// FIXME This won't work flawlessly because we cannot yet properly simulate
			// a classpath.
			IJavaProject nature = (IJavaProject) jdtProject.getNature(JavaCore.NATURE_ID);
			IClasspathEntry[] rawClasspath = nature.getRawClasspath();
			addLoaders(view, jdtProject, rawClasspath,false,false);
			DocumentScanner loader = new DocumentScannerImpl() {
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
	
	protected void addLoaders(DocumentScannerContainer container, IProject jdtProject, IClasspathEntry[] entries, boolean inContainer, boolean inOtherProject) throws CoreException {
		
		IJavaProject nature = (IJavaProject) jdtProject.getNature(JavaCore.NATURE_ID);
		
		Java7 java = (Java7) language();
		Predicate<? super String, Nothing> sourceFileFilter = java.plugin(ProjectConfigurator.class).sourceFileFilter();
		Predicate<? super String, Nothing> binaryFileFilter = java.plugin(ProjectConfigurator.class).binaryFileFilter();
		for(IClasspathEntry entry: entries) {
			IPath path = entry.getPath();
			switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_SOURCE:
				IPath projectRelativePath = Workspaces.root().findMember(path).getProjectRelativePath();
				String sourceRoot;
				if(inOtherProject) {
					sourceRoot = Files.workspaceFileToAbsoluteFile(path).toString();
				} else {
					sourceRoot = projectRelativePath.toString();
				}
				DirectoryScanner loader = new DirectoryScanner(sourceRoot, sourceFileFilter, new LazyJavaFileInputSourceFactory());
				try {
					if(inOtherProject) {
						CompositeDocumentScanner composite = (CompositeDocumentScanner) container;
						if(composite.view().canAddBinary(loader)) {
							composite.addScanner(loader);
						} else {
							System.out.println("Not adding other project source loader twice.");
						}
					} else {
						View view = (View) container;
						if(view.canAddSource(loader)) {
							view.addSource(loader);
						} else {
							System.out.println("Not adding source loader twice.");
						}
					}
				} catch(ProjectException exc) {
					exc.printStackTrace();
				}
				break;
			case IClasspathEntry.CPE_CONTAINER:
				IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(path, nature);
				CompositeDocumentScanner composite = new CompositeDocumentScanner(classpathContainer.getDescription());
				addToContainer(container, composite);
				addLoaders(composite, jdtProject, classpathContainer.getClasspathEntries(),true,false);
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
					JarScanner jarLoader = new JarScanner(new JarFile(libfile), binaryFileFilter);
						addToContainer(container, jarLoader);
				} catch(IOException exc) {
					exc.printStackTrace();
				}
				break;
			case IClasspathEntry.CPE_PROJECT:
				IProject dependency = Projects.project(path);
				IJavaProject dependencyJDTProject = (IJavaProject) dependency.getNature(JavaCore.NATURE_ID);
				CompositeDocumentScanner cc = new CompositeDocumentScanner(dependency.getName());
				addToContainer(container, cc);
				addLoaders(cc, dependency, dependencyJDTProject.getRawClasspath(),false,true);
				break;
			default:
				break;
			}
		}
	}

	private void addToContainer(DocumentScannerContainer container, DocumentScanner loader) {
		try {
			if(container instanceof CompositeDocumentScanner) {
				CompositeDocumentScanner c = (CompositeDocumentScanner) container;
				if(c.view().canAddBinary(loader)) {
					c.addScanner(loader);
				}
			} else {
				View view = (View) container;
				if(view.canAddBinary(loader)) {
					view.addBinary(loader);
				}
			}
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Override
	public LanguagePluginImpl clone() {
		return new JDTProjectLoader();
	}

}
