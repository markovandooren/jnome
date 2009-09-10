package jnome.test;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import jnome.output.JavaCodeWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.test.ModelProvider;

/**
 * @author Marko van Dooren
 */
public abstract class ExpressionTest extends MetaModelTest {
  
	/**
	 * Create a new expression tester
	 * @param provider
	 */
 /*@
   @ public behavior
   @
   @ post provider() == provider;
   @ post baseRecursive();
   @ post customRecursive();
   @*/
	public ExpressionTest(ModelProvider provider) {
		super(provider);
	}
	
public abstract List<Type> getTestTypes() throws LookupException;

  
  private static Logger _expressionLogger = Logger.getLogger("chameleon.test.expression");
  
  public static Logger getExpressionLogger() {
  	return _expressionLogger;
  }

  public void setLogLevels() {
		Logger.getLogger("chameleon.test.expression").setLevel(Level.FATAL);
  }

  @Test
  public void testExpressionTypes() throws Exception {
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
