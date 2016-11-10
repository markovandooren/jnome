package be.kuleuven.cs.distrinet.jnome.eclipse;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.eclipse.presentation.treeview.DeclarationCategorizer;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.RegularMethod;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.operator.Operator;

public class JavaDeclarationCategorizer implements DeclarationCategorizer {

	public int category(Declaration declaration)  {
		Element element = declaration;
		int result;
		if(element instanceof Declaration){
			if(element instanceof Method){
				if(element instanceof RegularMethod) {
					result = 3;
				}
				if(element instanceof Operator) {
					result = 4;
				}
				else {
					result = 10;
				}
			} else if(element instanceof Type) {
				result = 5;
			} else if(element instanceof Variable) {
				result = 1;
			}	else {
				result = 20;
			}
		} else {
			result = 30;
		}
		return result;
	}

}
