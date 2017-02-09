package org.aikodi.java.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.document.Document;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.input.ParseException;
import org.aikodi.chameleon.support.input.ChameleonANTLR3Parser;
import org.aikodi.chameleon.support.input.ModelFactoryUsingANTLR3;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.input.parser.JavaLexer;
import org.aikodi.java.input.parser.JavaParser;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * @author Marko van Dooren
 */

public class JavaModelFactory extends ModelFactoryUsingANTLR3 {

  protected JavaModelFactory(boolean bogus) {
    super();
  }

  /**
	 */
  public JavaModelFactory() {
    super();
  }

  @Override
  protected ChameleonANTLR3Parser<? extends Java7> getParser(InputStream inputStream, View view) throws IOException {
    ANTLRInputStream input = new ANTLRInputStream(inputStream);
    JavaLexer lexer = new JavaLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    JavaParser parser = new JavaParser(tokens);
    parser.setView(view);
    return parser;
  }

  @Override
  public <P extends Element> Element parse(Element element, String text) throws ParseException {
    try {
      InputStream inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
      Element result = null;
      if (element instanceof Declaration) {
        result = ((JavaParser) getParser(inputStream, element.view())).memberDecl().element;
      }
      return result;
    } catch (RecognitionException exc) {
      throw new ParseException(element.nearestAncestor(Document.class));
    } catch (IOException exc) {
      throw new ChameleonProgrammerException("Parsing of a string caused an IOException", exc);
    }
  }

  @Override
  public ModelFactoryUsingANTLR3 clone() {
    JavaModelFactory javaModelFactory = new JavaModelFactory();
    javaModelFactory.setDebug(debug());
    return javaModelFactory;
  }
}
