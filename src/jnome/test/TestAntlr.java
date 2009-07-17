package jnome.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jnome.core.type.JavaTypeReference;

import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;

/**
 * @author marko
 */
public class TestAntlr extends ExpressionTest {

	/**
	 * @param arg
	 * @throws TokenStreamException
	 * @throws RecognitionException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public TestAntlr(String arg)
		throws 
			Exception {
		super(arg);
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
		
		
		result = _mm.getSubNamespace("antlr").getAllTypes();
		return result;
	}
	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

}
