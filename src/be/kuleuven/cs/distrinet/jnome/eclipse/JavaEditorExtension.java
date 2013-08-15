package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
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

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.Modifier;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyRootNamespace;
import be.kuleuven.cs.distrinet.chameleon.eclipse.connector.EclipseEditorExtension;
import be.kuleuven.cs.distrinet.chameleon.eclipse.presentation.treeview.CompositeIconProvider;
import be.kuleuven.cs.distrinet.chameleon.eclipse.presentation.treeview.DefaultIconProvider;
import be.kuleuven.cs.distrinet.chameleon.eclipse.presentation.treeview.IconProvider;
import be.kuleuven.cs.distrinet.chameleon.eclipse.util.Files;
import be.kuleuven.cs.distrinet.chameleon.eclipse.util.Projects;
import be.kuleuven.cs.distrinet.chameleon.eclipse.util.Workspaces;
import be.kuleuven.cs.distrinet.chameleon.exception.ModelException;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.plugin.output.Syntax;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Abstract;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Constructor;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Final;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Interface;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Native;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Private;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Protected;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Static;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectoryLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoaderImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Default;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.jnome.input.PredefinedElementsFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JarLoader;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

/**
 * @author Marko van Dooren
 * @author Koen Vanderkimpen
 */
public class JavaEditorExtension extends EclipseEditorExtension {

	public JavaEditorExtension() {
		ACCESS_ICON_DECORATOR = new AccessIconDecorator();
		CLASS_ICON_PROVIDER = new ClassIconProvider(ACCESS_ICON_DECORATOR);
		FIELD_ICON_PROVIDER = new DefaultIconProvider("field", MemberVariable.class,ACCESS_ICON_DECORATOR);
		MEMBER_ICON_PROVIDER = new DefaultIconProvider("member", Member.class,ACCESS_ICON_DECORATOR);
	}

	public String pluginID() {
		return Bootstrapper.PLUGIN_ID;
	}

	public JavaEditorExtension clone() {
		return new JavaEditorExtension();
	}

	public String getLabel(Element element) {
		try {
			String result;
			if (element instanceof Method) {
				Method method = (Method)element;
				result = method.name();
				List<FormalParameter> params = method.formalParameters();
				result += "(";
				if (params.size()>0) {
					for (int i = 0;i<params.size();i++) {
						FormalParameter p = params.get(i);
						try {
							result += element.language().plugin(Syntax.class).toCode(p.getTypeReference());
						} catch (ModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (i<params.size()-1) {
							result += ",";
						}
					}
				}
				result += ")";
			} else result = super.getLabel(element);
			return result;
		} catch(Exception exc) {
			return "";
		}
	}

	@Override
	public List<Modifier> getFilterModifiers() {
		List<Modifier> result = new ArrayList<Modifier>();
		result.add(new Private());
		result.add(new Protected());
		result.add(new Public());
		result.add(new Default());
		result.add(new Static());
		result.add(new Final());
		result.add(new Abstract());
		result.add(new Constructor());
		result.add(new Interface());
		result.add(new Native());
		return result;
	}

	@Override
	public JavaDeclarationCategorizer declarationCategorizer()  {
		return new JavaDeclarationCategorizer();
	}

	@Override
	public JavaOutlineSelector createOutlineSelector() {
		return new JavaOutlineSelector();
	}

	public CompositeIconProvider createIconProvider() {
		return new CompositeIconProvider(
				CLASS_ICON_PROVIDER,
				FIELD_ICON_PROVIDER,
				MEMBER_ICON_PROVIDER
				);
	}

	private void register(String fileName, String iconName) throws MalformedURLException {
		register(fileName, iconName, Bootstrapper.PLUGIN_ID);
	}

	@Override
	protected void initializeRegistry() {
		super.initializeRegistry();
		try {
			register("class_obj.gif","publicclass");
			register("class_default_obj.gif","defaultclass");
			register("innerclass_private_obj.gif","privateclass");
			register("innerclass_protected_obj.gif","protectedclass");
			register("int_obj.gif","publicinterface");
			register("int_default_obj.gif","defaultinterface");
			register("innerinterface_private_obj.gif","privateinterface");
			register("innerinterface_protected_obj.gif","protectedinterface");
			register("field_private_obj.gif","privatefield");
			register("field_protected_obj.gif","protectedfield");
			register("field_default_obj.gif","defaultfield");
			register("field_public_obj.gif","publicfield");
			register("methpri_obj.gif","privatemember");
			register("methpro_obj.gif","protectedmember");
			register("methdef_obj.gif","defaultmember");
			register("methpub_obj.gif","publicmember");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final AccessIconDecorator ACCESS_ICON_DECORATOR;

	public final IconProvider CLASS_ICON_PROVIDER;
	public final IconProvider FIELD_ICON_PROVIDER;
	public final IconProvider MEMBER_ICON_PROVIDER;

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
			DocumentLoader loader = new DocumentLoaderImpl();
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
				
				String sourceRoot = Files.workspaceFileToAbsoluteFile(path).toString();
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

}
