package org.jnome.test;

import org.jnome.output.JavaCodeWriter;

import chameleon.core.MetamodelException;
import chameleon.core.element.Element;
import chameleon.core.type.Type;

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
  
  public void testOut() throws MetamodelException {
    Type type = _mm.findType("chameleon.core.type.Type");
    System.out.println(new JavaCodeWriter(2).toCode((Element)type.getParent()));
  }

}
