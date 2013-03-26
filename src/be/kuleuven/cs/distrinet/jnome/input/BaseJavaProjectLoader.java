package be.kuleuven.cs.distrinet.jnome.input;

import java.io.IOException;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.NullType;
import be.kuleuven.cs.distrinet.jnome.workspace.JarLoader;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectInputSource;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoaderImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;

public class BaseJavaProjectLoader extends JarLoader {

	public BaseJavaProjectLoader(String path, Java java) {
		super(path, java.plugin(ProjectConfigurator.class).binaryFileFilter(),true);
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
