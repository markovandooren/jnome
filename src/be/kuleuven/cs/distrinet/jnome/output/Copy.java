//package be.kuleuven.cs.distrinet.jnome.output;
//
//import java.io.File;
//import java.io.IOException;
//
//import be.kuleuven.cs.distrinet.jnome.core.language.Java;
//import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
//import be.kuleuven.cs.distrinet.jnome.input.JavaFileInputSourceFactory;
//import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
//import be.kuleuven.cs.distrinet.chameleon.core.namespace.RegularNamespaceFactory;
//import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
//import be.kuleuven.cs.distrinet.chameleon.input.ParseException;
//import be.kuleuven.cs.distrinet.chameleon.support.tool.Arguments;
//import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
//import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
//
///**
// * @author Tim Laeremans
// */
//public class Copy {
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
//   * @throws LookupException 
//   * @throws ProjectException 
//   * @throws IOException 
//   * @throws ParseException 
//   */
//  public static void main(String[] args) throws LookupException, ProjectException, IOException, ParseException {
//    if(args.length < 2) {
//      System.out.println("Usage: java .... Copy outputDir inputDir* @recursivePackageFQN* #packageFQN*");
//    }
//    String ext = ".java";
//		Java language = new JavaLanguageFactory().create();
//		JavaFileInputSourceFactory factory = new JavaFileInputSourceFactory(language.defaultNamespace());
//		Project project = new Project("copy test",new RootNamespace(new RegularNamespaceFactory()),language, new File("."));
//    Arguments arguments = new ArgumentParser(project).parse(args,ext, factory);
//    
//    JavaCodeWriter.writeCode(arguments);
//  }
//}
