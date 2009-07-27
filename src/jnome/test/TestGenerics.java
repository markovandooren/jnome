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
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.SuperWildCard;

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
	
	@Test
	public void testSubtyping() throws LookupException {
		JavaTypeReference tref1 = new JavaTypeReference("test.List");
		tref1.setUniParent(_mm);
		tref1.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.String")));
		JavaTypeReference tref2 = new JavaTypeReference("test.List");
		tref2.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.String")));
		tref2.setUniParent(_mm);
		JavaTypeReference tref3 = new JavaTypeReference("test.SubList");
		tref3.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.String")));
		tref3.setUniParent(_mm);
		JavaTypeReference tref4 = new JavaTypeReference("test.List");
		tref4.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.Object")));
		tref4.setUniParent(_mm);
		JavaTypeReference tref5 = new JavaTypeReference("test.List");
		tref5.addArgument(new ExtendsWildCard(new JavaTypeReference("java.lang.CharSequence")));
		tref5.setUniParent(_mm);
		JavaTypeReference tref6 = new JavaTypeReference("test.List");
		tref6.addArgument(new ExtendsWildCard(new JavaTypeReference("java.lang.String")));
		tref6.setUniParent(_mm);
		JavaTypeReference tref7 = new JavaTypeReference("test.List");
		tref7.addArgument(new SuperWildCard(new JavaTypeReference("java.lang.String")));
		tref7.setUniParent(_mm);
		JavaTypeReference tref8 = new JavaTypeReference("test.List");
		tref8.addArgument(new SuperWildCard(new JavaTypeReference("java.lang.CharSequence")));
		tref8.setUniParent(_mm);

		Type type1 = tref1.getType();
		Type type2 = tref2.getType();
		Type type3 = tref3.getType();
		Type type4 = tref4.getType();
		Type type5 = tref5.getType();
		Type type6 = tref6.getType();
		Type type7 = tref7.getType();
		Type type8 = tref8.getType();
		assertTrue(type2.subTypeOf(type1));
		assertTrue(type3.subTypeOf(type1));
		assertFalse(type4.subTypeOf(type1));
		assertFalse(type1.subTypeOf(type4));
		assertTrue(type1.subTypeOf(type5));
		assertFalse(type4.subTypeOf(type5));
		assertFalse(type5.subTypeOf(type1));
		assertTrue(type6.subTypeOf(type5));
		assertTrue(type2.subTypeOf(type6));
		assertFalse(type5.subTypeOf(type6));
		assertTrue(type4.subTypeOf(type7));
		assertTrue(type4.subTypeOf(type8));
		assertTrue(type8.subTypeOf(type7));
		assertFalse(type5.subTypeOf(type8));
		assertFalse(type5.subTypeOf(type7));
		assertFalse(type6.subTypeOf(type8));
		assertFalse(type6.subTypeOf(type7));
	}

  public static void main(String[] args) throws Exception, Throwable {
    new TestSuite(TestJnome.class).run(new TestResult());
  }


	@Override
	public void setLogLevels() {
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getLogger("lookup.subtyping").setLevel(Level.DEBUG);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
}
