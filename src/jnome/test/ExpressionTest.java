package jnome.test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import jnome.output.JavaCodeWriter;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import static org.junit.Assert.*; 

/**
 * @author marko
 */

public abstract class ExpressionTest extends MetaModelTest {
  
	public ExpressionTest() {
		
	}
	
  public ExpressionTest(String arg) {
    super(arg); 
  }

//  public void add(FileSet set, String name) {
//    String filename =  "**" + File.separator + name.replace('.', File.separatorChar)+".java";
//    set.include(new PatternPredicate(_srcDir, new FileNamePattern(filename)));
//  }

public abstract List<Type> getTestTypes() throws LookupException;

  
  private static Logger _expressionLogger = Logger.getLogger("chameleon.test.expression");
  
  public static Logger getExpressionLogger() {
  	return _expressionLogger;
  }

  public void setLogLevels() {
		Logger.getLogger("chameleon.test.expression").setLevel(Level.FATAL);
  }

  public void myTestDescendants() {
    List descendants = _mm.descendants();
    Iterator iter = descendants.iterator();
    while(iter.hasNext()) {
      Object o = iter.next();
      if(! (o instanceof Element)) {
        System.out.println("Bug in getDescendants");
      }
      assertTrue(o instanceof Element);
    }
  }
  
  @Test
  public void testExpressionTypes() throws Exception {
    //myTestDescendants(); // Stupid Junit creates a new test object for every test (which includes parsing).
    List<Type> types = getTestTypes();
    getLogger().info("Starting to test "+types.size() + " types.");
    Iterator<Type> iter = types.iterator();
    long startTime = System.currentTimeMillis();
    int count = 1;
    while (iter.hasNext()) {
      Type type = iter.next();
      getLogger().info(count+" Testing "+type.getFullyQualifiedName());
      processType(type);
      count++;
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Testing took "+(endTime-startTime)+" milliseconds.");
  }

  private int _count = 0;
  
  public void processType(Type type) throws Exception {
    _count++;
    Expression expr = null;
    Object o = null;
    try {
      List<Expression> exprs = type.descendants(Expression.class);
//      new PrimitiveTotalPredicate<Expression>() {
//
//				@Override
//				public boolean eval(Expression expr) {
//					return ! (expr.parent() instanceof Expression);
//				}
//			}.filter(exprs);
      for(Expression expression : exprs) {
        getExpressionLogger().info(_count + " Testing: "+toCode(expression));
        Type expressionType = expression.getType();
				assertTrue(expressionType != null);
        getExpressionLogger().info(_count + "        : "+expressionType.getFullyQualifiedName());
      }
    }
    catch (Exception e) {
      throw e; 
    }
    
  }
  
// Pull up this test, and make the toCode abstract. 
//  public abstract String toCode(Expression expr) throws LookupException;

	public String toCode(Expression expr) throws LookupException {
		return _writer.toCode(expr);
	}
	
	private JavaCodeWriter _writer = new JavaCodeWriter();

}
