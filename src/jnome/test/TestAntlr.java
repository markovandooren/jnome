package jnome.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import org.antlr.runtime.RecognitionException;

import chameleon.core.context.LookupException;

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

	public Set getTestTypes() throws LookupException {
		return _mm.getSubNamespace("antlr").getAllTypes();
	}

}
