package jnome.input;

import java.io.File;
import java.io.IOException;

import jnome.core.language.Java;
import jnome.core.type.NullType;
import jnome.workspace.JarLoader;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.Type;
import chameleon.workspace.DocumentLoaderImpl;
import chameleon.workspace.InputException;
import chameleon.workspace.DirectInputSource;
import chameleon.workspace.View;

public class BaseJavaProjectLoader extends JarLoader {

	public BaseJavaProjectLoader(String path) {
		super(path);
	}
	
	@Override
	protected void createInputSources() throws IOException, LookupException, InputException {
		// First create input sources for the base classes in rt.jar
		super.createInputSources();
		// The add predefined elements.
		initializePredefinedElements();
	}
	
	public void initializePredefinedElements() {
		RootNamespace root = view().namespace();
		_factory = new PrimitiveTypeFactory(view());
		addPrimitives("",this);
	  addInfixOperators();
	  addNullType(this);
	}

	protected void addNullType(DocumentLoaderImpl loader) {
    try {
			new DirectInputSource(new NullType(java()),"",view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
	}

	
	private PrimitiveTypeFactory _factory;


	protected void addPrimitives(String root, DocumentLoaderImpl loader) {
		_factory.addPrimitives(root,loader);
  }

	private Type findType(View view, String fqn) throws LookupException {
		Java lang = (Java) view.language();
	 	return lang.findType(fqn,view.namespace());
	}
	  
	protected String equality() {
		return _factory.equality();
	}
	  
	protected void addInfixOperator(Type type, String returnType, String symbol, String argType) {
		_factory.addInfixOperator(type, returnType, symbol, argType);
	}
	  protected void addInfixOperators() {
        try {
            Type obj = findType(view(), "java.lang.Object");
            if (obj != null) {
                addInfixOperator(obj, "boolean", equality(), "Object");
                addInfixOperator(obj, "boolean", "!=", "Object");
                addPlusString(obj);
            }
            Type string = findType(view(), "java.lang.String");
            if (string != null) {
                addInfixOperator(string, "String", "+", "Object");
                addInfixOperator(string, "String", "+=", "Object");
                addInfixOperator(string, "String", "+", "byte");
                addInfixOperator(string, "String", "+=", "byte");
                addInfixOperator(string, "String", "+", "short");
                addInfixOperator(string, "String", "+=", "short");
                addInfixOperator(string, "String", "+", "char");
                addInfixOperator(string, "String", "+=", "char");
                addInfixOperator(string, "String", "+", "int");
                addInfixOperator(string, "String", "+=", "int");
                addInfixOperator(string, "String", "+", "long");
                addInfixOperator(string, "String", "+=", "long");
                addInfixOperator(string, "String", "+", "float");
                addInfixOperator(string, "String", "+=", "float");
                addInfixOperator(string, "String", "+", "double");
                addInfixOperator(string, "String", "+=", "double");
                addInfixOperator(string, "String", "+", "boolean");
                addInfixOperator(string, "String", "+=", "boolean");
            }

        }
        catch (LookupException e) {
        	// This should only happen if the Java system library was not parsed.
        	e.printStackTrace();
            throw new ChameleonProgrammerException(e);
        }
    }


	  protected void removeElement(Element element) {
      element.parentLink().connectTo(null);
    }


    protected Java java() {
    	return (Java) language();
    }

    protected void addPlusString(Type type) {
    	_factory.addPlusString(type);
    }
}
