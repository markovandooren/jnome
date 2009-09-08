package jnome.tool;

import java.util.Set;

import jnome.input.JavaModelFactory;

import org.apache.log4j.BasicConfigurator;

import chameleon.core.declaration.Declaration;
import chameleon.core.type.Type;
import chameleon.input.ModelFactory;
import chameleon.support.tool.ArgumentParser;
import chameleon.support.tool.Arguments;

public class DependencyAnalyzer {

  /**
   * args[0] = path for the directory to write output
   * args[1] = path to read input files
   * ...1 or more input paths possible...
   * args[i] = fqn of package to read, let this start with "@" to read the package recursively
   *...1 or more packageFqns possible...
   * args[n] = fqn of package to read, let this start with "#" to NOT read the package recursively.
   *...1 or more packageFqns possible...
   *
   * Example 
   * java Copy c:\output\ c:\input1\ c:\input2\ @javax.swing @java.lang #java #java.security 
   */
  public DependencyAnalyzer(String[] args, ModelFactory factory) throws Exception {
    if(args.length < 2) {
      System.out.println("Usage: java .... DependencyAnalyzer outputDir inputDir* @recursivePackageFQN* #packageFQN* $typeFQN*");
    }
    BasicConfigurator.configure();
    Arguments arguments = new ArgumentParser(factory,false).parse(args,".java");
	  chameleon.tool.analysis.DependencyAnalyzer analyzer = new chameleon.tool.analysis.DependencyAnalyzer();
	  Set<Declaration> deps = analyzer.dependenciesOfAll(arguments.getTypes());
	  Set<Type> types = analyzer.nearestAncestors(deps, Type.class);
	  for(Type type: types) {
	  	System.out.println(type.getFullyQualifiedName());
	  }
	}
  
  public static void main(String[] args) throws Exception {
  	new DependencyAnalyzer(args, new JavaModelFactory());
  }

}
