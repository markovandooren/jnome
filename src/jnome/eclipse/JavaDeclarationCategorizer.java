package jnome.eclipse;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.eclipse.presentation.treeview.DeclarationCategorizer;
import chameleon.oo.member.Member;
import chameleon.oo.method.Method;
import chameleon.oo.method.RegularMethod;
import chameleon.oo.type.Type;
import chameleon.oo.variable.MemberVariable;
import chameleon.support.member.simplename.operator.Operator;

public class JavaDeclarationCategorizer implements DeclarationCategorizer {

	public int category(Declaration declaration)  {
		Element element = declaration;
		int result;
		if(element instanceof Member){
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
			} else if(element instanceof MemberVariable) {
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
