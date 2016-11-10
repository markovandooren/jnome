package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.eclipse.view.outline.ChameleonOutlineSelector;
import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.support.member.simplename.variable.MemberVariableDeclarator;

public class JavaOutlineSelector extends ChameleonOutlineSelector {

	@Override
	public boolean isAllowed(Element element) throws ModelException {
		return (! (element instanceof FormalParameter)) && 
		       (! (element instanceof TypeParameter)) &&
		       super.isAllowed(element);
	}

	@Override
	public List<Element> outlineChildren(Element element) throws ModelException {
		List<Element> result = super.outlineChildren(element);
		if(element instanceof MemberVariableDeclarator) {
			MemberVariableDeclarator decl = (MemberVariableDeclarator) element;
			result.addAll(decl.declaredDeclarations());
		}
		return result;
	}
	
	
}
