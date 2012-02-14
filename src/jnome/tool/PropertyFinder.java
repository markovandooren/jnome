package jnome.tool;

import java.util.List;

import jnome.input.JavaModelFactory;

import org.apache.log4j.BasicConfigurator;

import chameleon.core.language.Language;
import chameleon.input.ModelFactory;
import chameleon.oo.method.Method;
import chameleon.oo.type.Type;
import chameleon.plugin.output.Syntax;
import chameleon.support.tool.ArgumentParser;
import chameleon.support.tool.Arguments;

public class PropertyFinder {

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
	  public PropertyFinder(String[] args, ModelFactory factory) throws Exception {
	    if(args.length < 2) {
	      System.out.println("Usage: java .... PropertyFinder outputDir inputDir* @recursivePackageFQN* #packageFQN* $typeFQN*");
	    }
	    BasicConfigurator.configure();
	    Arguments arguments = new ArgumentParser(factory,false).parse(args,".java");
		  Language lang = factory.language();
		  List<Type> types = lang.defaultNamespace().descendants(Type.class);
		  findPairs(types,"set","get");
		  findPairs(types,"add","remove");
		}

		protected void findPairs(List<Type> types, String first, String second) {
		  int size = types.size();
			int count = 1;
		  int pairs = 0;
			for(Type type:types) {
//				System.out.println("Searching type "+count+" of "+size);
				count++;
		  	for(Method<?,?,?> method: type.directlyDeclaredElements(Method.class)) {
		  		String name = method.signature().name();
		  		if(name.startsWith(first)) {
		  			String X = name.substring(3);
				  	for(Method<?,?,?> getter: type.directlyDeclaredElements(Method.class)) {
				  		String otherName = getter.signature().name();
				  		if(otherName.equals(second+X)) {
//				  		  System.out.println(method.nearestAncestor(Type.class).getFullyQualifiedName()+" : "+X);
				  		  pairs++;
				  		}
				  	}
		  		}
		  	}
		  }
			System.out.println(first+" and "+second+" pairs found: "+pairs);
		}
	  
	  public static void main(String[] args) throws Exception {
	  	JavaModelFactory factory = new JavaModelFactory();
	  	
			new PropertyFinder(args, factory);
	  }

}
