package org.aikodi.java.input;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.operator.Operator;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.DeclarationLoader;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.type.NullType;
import org.aikodi.java.workspace.JavaView;

/**
 * A class for initializing the predefined elements of Java 7.
 * This includes the primitive types and the operators.
 * 
 * @author Marko van Dooren
 */
public class PredefinedElementsFactory {

	public PredefinedElementsFactory(JavaView view, DocumentScanner scanner) {
		_view = view;
		_scanner = scanner;
	}
	
	private DocumentScanner _scanner;
	
	public DocumentScanner scanner() {
		return _scanner;
	}
	
	private JavaView _view;
	
	public void initializePredefinedElements() {
		_factory = new PrimitiveTypeFactory(_view);
		addPrimitives("",scanner());
	  addInfixOperators();
	  addNullType(scanner());
	}

	protected void addNullType(DocumentScanner loader) {
    try {
			new DeclarationLoader(new NullType(java()),"",_view,loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
	}
	
	private PrimitiveTypeFactory _factory;


	protected void addPrimitives(String root, DocumentScanner loader) {
		_factory.addPrimitives(root,loader);
  }

	private Type findType(View view, String fqn) throws LookupException {
		Java7 lang = (Java7) view.language();
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
    
    protected Java7 java() {
    	return (Java7) _view.language();
    }
}
