package org.aikodi.java.core.variable;

import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.variable.VariableDeclaration;
import org.aikodi.chameleon.oo.variable.VariableDeclarator;
import org.aikodi.java.core.type.ArrayTypeReference;
import org.aikodi.java.core.type.JavaTypeReference;

/**
 * Because the Java allows array brackets after the name of a variable, we
 * need a separate subclass.
 * 
 * @author Marko van Dooren
 */
public class JavaVariableDeclaration extends VariableDeclaration {
	
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

 public TypeReference typeReference() {
	 int arrayDimension = arrayDimension();
	 VariableDeclarator variableDeclarator = lexical().nearestAncestor(VariableDeclarator.class);
	JavaTypeReference result = (JavaTypeReference) variableDeclarator.typeReference();
	 if(arrayDimension > 0) {
	   result = new ArrayTypeReference(clone(result),arrayDimension);
	   result.setUniParent(variableDeclarator);
	 }
	 return result;
 }

}
