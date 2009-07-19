package jnome.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;

/**
 * @author Marko van Dooren
 */
public class TestRejuse extends ExpressionTest {

  public TestRejuse() throws Exception {
  }
  
  public void addTestFiles() {
		include("testsource"+getSeparator()+"gen"+getSeparator());
		include("testsource"+getSeparator()+"jregex"+getSeparator());
		include("testsource"+getSeparator()+"jutil"+getSeparator()+"src"+getSeparator());
	  include("testsource"+getSeparator()+"junit3.8.1"+getSeparator()+"src"+getSeparator());
  	
  }
  
  public List<Type> getTestTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();
//    JavaTypeReference ref = new JavaTypeReference("org.jutil.math.matrix.Matrix");
//    ref.setUniParent(_mm);
//    result.add(ref.getType());
	
    result = _mm.getSubNamespace("org").getSubNamespace("jutil").getAllTypes();
    return result;
  }

	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
	
//  public static void main(String[] args) throws Exception, Throwable {
//    new TestSuite(TestRejuse.class).run(new TestResult());
//  }
}
