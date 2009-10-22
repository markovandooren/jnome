package jnome.output;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import jnome.input.JavaModelFactory;
import chameleon.core.lookup.LookupException;
import chameleon.input.ParseException;
import chameleon.support.tool.ArgumentParser;
import chameleon.support.tool.Arguments;

/**
 * @author Tim Laeremans
 */
public class Copy {

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
   * @throws LookupException 
   */
  public static void main(String[] args) throws ParseException, MalformedURLException, FileNotFoundException, IOException, LookupException {
    if(args.length < 2) {
      System.out.println("Usage: java .... Copy outputDir inputDir* @recursivePackageFQN* #packageFQN*");
    }
    
    Arguments arguments = new ArgumentParser(new JavaModelFactory()).parse(args,".java");
    
    JavaCodeWriter.writeCode(arguments);
  }
}
