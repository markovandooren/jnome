package org.jnome.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import chameleon.analysis.ExceptionFlow;
import chameleon.analysis.ExceptionFlowAnalyzer;
import chameleon.analysis.MethodSTEFAnalysis;
import chameleon.core.MetamodelException;
import chameleon.core.method.Method;
import chameleon.core.namespace.Namespace;
import chameleon.core.type.Type;

/**
 * @author marko
 */
public class TestExceptionFlow extends MetaModelTest {

  /**
   * @param arg0
   */
  public TestExceptionFlow(String arg0) {
    super(arg0);
  }

  public void addTestFiles() {
    //include("/home/marko/java/eclipse/3.0/eclipse/workspace/ExceptionTestCase", "**/*.java");
  }
  
  public void test() throws MetamodelException {
    processType("test.A");
    processType("test.B");
  }
  
  public List getMethods(Namespace pack) {
    Collection c = pack.getTypes();
    List result = new ArrayList();
    Iterator iter = c.iterator();
    while(iter.hasNext()) {
      List desc = ((Type)iter.next()).getDescendants(Method.class);
      result.addAll(desc);
    }
    return result; 
  }

  private void processType(String name) throws MetamodelException {
    Type typeA = _mm.findType(name);
    List desc = typeA.getDescendants(Method.class);
    ExceptionFlowAnalyzer analyzer = new ExceptionFlowAnalyzer(getMethods(_mm.getSubNamespace("test")));
    Collection analyses = analyzer.analyzeMethods(desc);
    Iterator iter = analyses.iterator();
    System.out.println("Analyzed "+analyses.size() +" methods in type "+name);
    while(iter.hasNext()) {
      MethodSTEFAnalysis analysis = (MethodSTEFAnalysis)iter.next();
      Iterator inner = analysis.getHeaderAnalyses().iterator();
      while(inner.hasNext()) {
        ExceptionFlow flow = (ExceptionFlow)inner.next();
        if(flow.getGraph().getNbNodes() > 1) {
          System.out.println("Exception "+flow.getType().getFullyQualifiedName()+" in method header of "+ flow.getMethod().getName()+" is propagated in "+(flow.getGraph().getNbNodes()-1) +" methods");
        }
      }
      inner = analysis.getThrowAnalyses().iterator();
      while(inner.hasNext()) {
        ExceptionFlow flow = (ExceptionFlow)inner.next();
        if(flow.getGraph().getNbNodes() > 1) {
          System.out.println("Exception "+flow.getType().getFullyQualifiedName()+" thrown directly in method "+ flow.getMethod().getName()+" is propagated in "+(flow.getGraph().getNbNodes()-1) +" methods");
        }
      }
    }
  }

}
