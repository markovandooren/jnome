package be.kuleuven.cs.distrinet.jnome.core.variable;

import be.kuleuven.cs.distrinet.jnome.core.type.ArrayTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclaration;

/**
 * Because the idiots who created Java had to allow array brackets after the name of a variable, we
 * need a separate subclass.
 * 
 * @author Marko
 */
public class JavaVariableDeclaration extends VariableDeclaration {
	
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
 protected void transform(Variable variable) {
	 int arrayDimension = arrayDimension();
	 if(arrayDimension > 0) {
	   JavaTypeReference ref = (JavaTypeReference)variable.getTypeReference();
	   variable.setTypeReference(new ArrayTypeReference(ref,arrayDimension));
	 }
 }


}
