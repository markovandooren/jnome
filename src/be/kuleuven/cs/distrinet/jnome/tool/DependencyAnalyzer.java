//package be.kuleuven.cs.distrinet.jnome.tool;
//
//import java.io.File;
//import java.util.Set;
//
//import be.kuleuven.cs.distrinet.jnome.core.language.Java;
//import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
//import be.kuleuven.cs.distrinet.jnome.input.JavaFileInputSourceFactory;
//import be.kuleuven.cs.distrinet.jnome.input.JavaModelFactory;
//
//import org.apache.log4j.BasicConfigurator;
//
//import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
//import be.kuleuven.cs.distrinet.chameleon.core.namespace.RegularNamespaceFactory;
//import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
//import be.kuleuven.cs.distrinet.chameleon.input.ModelFactory;
//import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
//import be.kuleuven.cs.distrinet.chameleon.support.tool.ArgumentParser;
//import be.kuleuven.cs.distrinet.chameleon.support.tool.Arguments;
//import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
//
//public class DependencyAnalyzer {
//
//  /**
//   * args[0] = path for the directory to write output
//   * args[1] = path to read input files
//   * ...1 or more input paths possible...
//   * args[i] = fqn of package to read, let this start with "@" to read the package recursively
//   *...1 or more packageFqns possible...
//   * args[n] = fqn of package to read, let this start with "#" to NOT read the package recursively.
//   *...1 or more packageFqns possible...
//   *
//   * Example 
//   * java Copy c:\output\ c:\input1\ c:\input2\ @javax.swing @java.lang #java #java.security 
//   */
//  public DependencyAnalyzer(String[] args, ModelFactory factory) throws Exception {
//    if(args.length < 2) {
//      System.out.println("Usage: java .... DependencyAnalyzer outputDir inputDir* @recursivePackageFQN* #packageFQN* $typeFQN*");
//    }
//    BasicConfigurator.configure();
//		Java language = new JavaLanguageFactory().create();
//		String extension = ".java";
//		Project project = new Project("copy test",new RootNamespace(new RegularNamespaceFactory()),language, new File("."));
//		JavaFileInputSourceFactory inputSourceFactory = new JavaFileInputSourceFactory(language.defaultNamespace());
//		Arguments arguments = new ArgumentParser(project,false).parse(args,extension,inputSourceFactory);
//	  chameleon.tool.analysis.DependencyAnalyzer analyzer = new chameleon.tool.analysis.DependencyAnalyzer();
//	  Set<Declaration> deps = analyzer.dependenciesOfAll(arguments.getTypes());
//	  Set<Type> types = analyzer.nearestAncestors(deps, Type.class);
//	  for(Type type: types) {
//	  	System.out.println(type.getFullyQualifiedName());
//	  }
//	}
//  
//  public static void main(String[] args) throws Exception {
//  	new DependencyAnalyzer(args, new JavaModelFactory());
//  }
//
//}
