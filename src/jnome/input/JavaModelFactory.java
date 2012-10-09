package jnome.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import jnome.input.parser.JavaLexer;
import jnome.input.parser.JavaParser;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import chameleon.core.document.Document;
import chameleon.core.element.Element;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ParseException;
import chameleon.oo.member.Member;
import chameleon.support.input.ChameleonParser;
import chameleon.support.input.ModelFactoryUsingANTLR;
import chameleon.workspace.View;

/**
 * @author Marko van Dooren
 */

public class JavaModelFactory extends ModelFactoryUsingANTLR {

//	/**
//	 * BE SURE TO CALL INIT() IF YOU USE THIS CONSTRUCTOR.
//	 * 
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	public JavaModelFactory() {
//		Java lang = new Java();
//		setLanguage(lang, ModelFactory.class);
//	}
	
	protected JavaModelFactory(boolean bogus) {
		super();
	}
	
	/**
	 */
	public JavaModelFactory() {
		super();
	}
	
  @Override
  protected ChameleonParser getParser(InputStream inputStream,View view) throws IOException {
      ANTLRInputStream input = new ANTLRInputStream(inputStream);
      JavaLexer lexer = new JavaLexer(input);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      JavaParser parser = new JavaParser(tokens);
      parser.setView(view);
      return parser;
  }

  @Override
	protected <P extends Element> Element parse(Element element, String text) throws ParseException {
		try {
		  InputStream inputStream = new StringBufferInputStream(text);
		  Element result = null;
		  if(element instanceof Member) {
	  		result = ((JavaParser)getParser(inputStream, element.view())).memberDecl().element;
			}
			return result;
		} catch(RecognitionException exc) {
			throw new ParseException(element.nearestAncestor(Document.class));
		} catch(IOException exc) {
			throw new ChameleonProgrammerException("Parsing of a string caused an IOException",exc);
		}
	}


  @Override
	public ModelFactoryUsingANTLR clone() {
		try {
			JavaModelFactory javaModelFactory = new JavaModelFactory();
			javaModelFactory.setDebug(debug());
			return javaModelFactory;
		} catch (Exception e) {
			throw new RuntimeException("Exception while cloning a JavaModelFactory", e);
		}
	}
}
