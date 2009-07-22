package jnome.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import jnome.core.type.JavaTypeReference;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.GenericArgument;

/**
 * @author marko
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestGenerics extends ExpressionTest {

	public TestGenerics() throws Exception {
	}

	public void addTestFiles() {
			include("testsource"+getSeparator()+"gen"+getSeparator());
			include("testsource"+getSeparator()+"generics"+getSeparator());
	}

	/**
	 *
	 */

	public List<Type> getTestTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();
		result = _mm.getSubNamespace("test").getAllTypes();
		return result;
	}
	
	//@Test
	public void testSubtyping() throws LookupException {
		JavaTypeReference tref1 = new JavaTypeReference("test.List");
		tref1.setUniParent(_mm);
		tref1.addArgument(new GenericArgument(new JavaTypeReference("java.lang.String")));
		JavaTypeReference tref2 = new JavaTypeReference("test.List");
		tref2.addArgument(new GenericArgument(new JavaTypeReference("java.lang.String")));
		tref2.setUniParent(_mm);
		JavaTypeReference tref3 = new JavaTypeReference("test.SubList");
		tref3.addArgument(new GenericArgument(new JavaTypeReference("java.lang.String")));
		tref3.setUniParent(_mm);
		JavaTypeReference tref4 = new JavaTypeReference("test.List");
		tref4.addArgument(new GenericArgument(new JavaTypeReference("java.lang.Object")));
		tref4.setUniParent(_mm);
		Type type1 = tref1.getType();
		Type type2 = tref2.getType();
		Type type3 = tref3.getType();
		Type type4 = tref4.getType();
		assertTrue(type2.subTypeOf(type1));
		assertTrue(type3.subTypeOf(type1));
		assertFalse(type4.subTypeOf(type1));
	}

  public static void main(String[] args) throws Exception, Throwable {
    new TestSuite(TestJnome.class).run(new TestResult());
  }


	@Override
	public void setLogLevels() {
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
}
