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
public class TestAntlr extends ExpressionTest {

	public TestAntlr() {
	}

	/**
	 *
	 */

	public void addTestFiles() {
		include("testsource"+getSeparator()+"gen"+getSeparator());
		include("testsource"+getSeparator()+"antlr-2.7.2"+getSeparator()+"antlr"+getSeparator());
	}

	/**
	 *
	 */

	public List<Type> getTestTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();
//		assertNotNull(_mm);
//	  JavaTypeReference ref = new JavaTypeReference("antlr.NoViableAltException");
//	  ref.setUniParent(_mm);
//	  result.add(ref.getType());
		
		
		result = _mm.getSubNamespace("antlr").allDeclarations(Type.class);
		return result;
	}
	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

}
