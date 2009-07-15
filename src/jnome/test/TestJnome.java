package jnome.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jnome.core.type.JavaTypeReference;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;

/**
 * @author marko
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestJnome extends ExpressionTest {

	/**
	 * @param arg
	 * @throws TokenStreamException
	 * @throws RecognitionException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public TestJnome(String arg)
		throws Exception {
		super(arg);
	}

	public void addTestFiles() {
			include("testsource"+getSeparator()+"gen"+getSeparator());
			include("testsource"+getSeparator()+"jregex"+getSeparator());
			include("testsource"+getSeparator()+"antlr-2.7.2"+getSeparator()+"antlr"+getSeparator());
			include("testsource"+getSeparator()+"jnome"+getSeparator()+"src"+getSeparator());
			include("testsource"+getSeparator()+"jutil"+getSeparator()+"src"+getSeparator());
		  include("testsource"+getSeparator()+"junit3.8.1"+getSeparator()+"src"+getSeparator());
			include("testsource"+getSeparator()+"jakarta-log4j-1.2.8"+getSeparator()+"src"+getSeparator()+"java"+getSeparator());
	}

	/**
	 *
	 */

	public List<Type> getTestTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.input.antlr.javadoc.DocumentationBlockMethodAcquirer");
//			  JavaTypeReference ref = new JavaTypeReference("org.jnome.mm.java.types.ResolvedReturnTypeImpl");
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.mm.Resolver");
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.mm.java.methods.MethodWithReturnType");
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.input.antlr.javadoc.parser.JavaDocParser");
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.output.xml.java.packages.ResolvedPackageWriter");
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.input.antlr.javadoc.DocumentationBlockAcquirer");
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.input.antlr.javadoc.parser.JavaDocLexer");
//		JavaTypeReference ref = new JavaTypeReference("org.jnome.input.antlr.java.methods.RegularConstructorAcquirer");
//		ref.setUniParent(_mm);
//		result.add(ref.getType());
//		ref = new JavaTypeReference("org.jnome.input.antlr.java.parser.JnomeJavaParser");
//		ref.setUniParent(_mm);
//		result.add(ref.getType());
		result = _mm.getSubNamespace("org").getSubNamespace("jnome").getAllTypes();
		return result;
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
