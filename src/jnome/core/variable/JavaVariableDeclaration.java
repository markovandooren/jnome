package jnome.core.variable;

import jnome.core.type.JavaTypeReference;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.expression.Expression;
import chameleon.core.variable.Variable;
import chameleon.support.variable.VariableDeclaration;

/**
 * Because the idiots who created Java had to allow array brackets after the name of a variable, we
 * need a separate subclass.
 * 
 * @author Marko
 */
public class JavaVariableDeclaration<V extends Variable> extends VariableDeclaration<V> {
	
  public JavaVariableDeclaration(SimpleNameSignature sig, Expression expr) {
		super(sig,expr);
	}

	public JavaVariableDeclaration(String name, Expression expr) {
		super(name,expr);
	}

	public JavaVariableDeclaration(String name) {
		super(name);
	}

/*@
  @ public behavior
  @
  @ post \result >= 0;
  @*/
 public int arrayDimension() {
 	return _arrayDimension;
 }
 
/*@
  @ public behavior
  @
  @ pre dimension >= 0;
  @
  @ post arrayDimension() == dimension;
 */
 public void setArrayDimension(int dimension) {
 	if(dimension >= 0) {
 	  _arrayDimension = dimension;
 	} else {
 		throw new ChameleonProgrammerException("Array dimension is negative");
 	}
 }
 
 private int _arrayDimension = 0;

 /**
  * This method adds the array dimension. 
  */
 protected void transform(V variable) {
	 JavaTypeReference ref = (JavaTypeReference)variable.getTypeReference();
	 ref.addArrayDimension(arrayDimension());
 }


}
