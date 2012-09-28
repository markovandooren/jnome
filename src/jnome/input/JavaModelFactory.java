package jnome.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Collections;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.namespacedeclaration.JavaNamespaceDeclaration;
import jnome.core.type.NullType;
import jnome.core.type.RegularJavaType;
import jnome.input.parser.JavaLexer;
import jnome.input.parser.JavaParser;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.document.Document;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ParseException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.member.Member;
import chameleon.oo.method.SimpleNameMethodHeader;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.oo.variable.FormalParameter;
import chameleon.support.input.ChameleonParser;
import chameleon.support.input.ModelFactoryUsingANTLR;
import chameleon.support.member.simplename.operator.infix.InfixOperator;
import chameleon.support.member.simplename.operator.postfix.PostfixOperator;
import chameleon.support.member.simplename.operator.prefix.PrefixOperator;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.ValueType;
import chameleon.workspace.ProjectException;
import chameleon.workspace.SyntheticInputSource;
import chameleon.workspace.SyntheticProjectLoader;

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
		
	}
	
	/**
	 */
	public JavaModelFactory() {
	}
	
  @Override
  protected ChameleonParser getParser(InputStream inputStream) throws IOException {
      ANTLRInputStream input = new ANTLRInputStream(inputStream);
      JavaLexer lexer = new JavaLexer(input);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      JavaParser parser = new JavaParser(tokens);
      parser.setLanguage((ObjectOrientedLanguage) language());
      return parser;
  }

  @Override
	protected <P extends Element> Element parse(Element element, String text) throws ParseException {
		try {
		  InputStream inputStream = new StringBufferInputStream(text);
		  Element result = null;
		  if(element instanceof Member) {
	  		result = ((JavaParser)getParser(inputStream)).memberDecl().element;
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
