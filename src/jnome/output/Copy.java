package jnome.output;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.JavaFileInputSourceFactory;
import jnome.input.JavaModelFactory;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.RootNamespace;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.support.tool.ArgumentParser;
import chameleon.support.tool.Arguments;
import chameleon.workspace.DirectoryProjectBuilder;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

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
   * @throws ProjectException 
   * @throws IOException 
   * @throws ParseException 
   */
  public static void main(String[] args) throws LookupException, ProjectException, IOException, ParseException {
    if(args.length < 2) {
      System.out.println("Usage: java .... Copy outputDir inputDir* @recursivePackageFQN* #packageFQN*");
    }
    String ext = ".java";
		Java language = new JavaLanguageFactory().create();
		DirectoryProjectBuilder builder = new DirectoryProjectBuilder(new Project("copy test",new RootNamespace(),language),ext,null, new JavaFileInputSourceFactory(language.plugin(ModelFactory.class)));
    Arguments arguments = new ArgumentParser(builder).parse(args,ext);
    
    JavaCodeWriter.writeCode(arguments);
  }
}
