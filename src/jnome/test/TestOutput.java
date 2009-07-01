package jnome.test;

import jnome.core.type.JavaTypeReference;
import jnome.output.JavaCodeWriter;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;

/**
 * @author marko
 */
public class TestOutput extends MetaModelTest {

  /**
   * @param arg0
   */
  public TestOutput(String arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  public void addTestFiles() {
//    include("/home/marko/cvs/JnomeTNG/src/","**/*.java");
//    include("/home/marko/cvs/unit/src/", "**/*.java");
//    include("/home/marko/build/junit3.8.1/src", "**/*.java");
//    include("/home/marko/tmp/src/", "jregex/**/*.java");
//    include("/home/marko/build/antlr-2.7.2/antlr", "**/*.java");
  	
  	include("C:\\Chameleon\\src");
  }
  
  public void testOut() throws LookupException {
    TypeReference typeRef = new JavaTypeReference("chameleon.core.type.Type");
    typeRef.setUniParent(_mm);
    Type type = typeRef.getType();
    System.out.println(new JavaCodeWriter(2).toCode(type.parent()));
  }

}
