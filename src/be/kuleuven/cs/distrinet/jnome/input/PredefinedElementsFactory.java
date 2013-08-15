package be.kuleuven.cs.distrinet.jnome.input;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.Operator;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectInputSource;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.NullType;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

public class PredefinedElementsFactory {

	public PredefinedElementsFactory(JavaView view, DocumentLoader loader) {
		_view = view;
		_loader = loader;
	}
	
	private DocumentLoader _loader;
	
	public DocumentLoader loader() {
		return _loader;
	}
	
	private JavaView _view;
	
	public void initializePredefinedElements() {
		_factory = new PrimitiveTypeFactory(_view);
		addPrimitives("",loader());
	  addInfixOperators();
	  addNullType(loader());
	}

	protected void addNullType(DocumentLoader loader) {
    try {
			new DirectInputSource(new NullType(java()),"",_view,loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
	}
	
	private PrimitiveTypeFactory _factory;


	protected void addPrimitives(String root, DocumentLoader loader) {
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
			JavaView view = _view;
			Type obj = findType(view, "java.lang.Object");
			view.setTopLevelType(obj);
			if (obj != null) {
				addInfixOperator(obj, "boolean", equality(), "Object");
				addInfixOperator(obj, "boolean", "!=", "Object");
				addPlusString(obj);
			}
			Type string = findType(view, "java.lang.String");
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
			copyOperators(findType(view, "int"), findType(view, "java.lang.Integer"));
			copyOperators(findType(view, "long"), findType(view, "java.lang.Long"));
			copyOperators(findType(view, "float"), findType(view, "java.lang.Float"));
			copyOperators(findType(view, "double"), findType(view, "java.lang.Double"));
			copyOperators(findType(view, "boolean"), findType(view, "java.lang.Boolean"));
			copyOperators(findType(view, "short"), findType(view, "java.lang.Boolean"));
			copyOperators(findType(view, "byte"), findType(view, "java.lang.Byte"));
			copyOperators(findType(view, "char"), findType(view, "java.lang.Character"));

		}
		catch (LookupException e) {
			// This should only happen if the Java system library was not parsed.
			e.printStackTrace();
			throw new ChameleonProgrammerException(e);
		}
	}

	private void copyOperators(Type from, Type to) throws LookupException {
		for(Declaration m: from.locallyDeclaredDeclarations()) {
			if(m instanceof Operator) {
				//					String name = m.name();
				//					if((! name.equals("==")) && (! name.equals("!="))) {
				to.add(Util.clone((Operator)m));
				//					}
			}
		}
	}

    protected void addPlusString(Type type) {
    	_factory.addPlusString(type);
    }
    
    protected Java java() {
    	return (Java) _view.language();
    }
}
