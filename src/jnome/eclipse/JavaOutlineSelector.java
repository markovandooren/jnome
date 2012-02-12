package jnome.eclipse;

import java.util.List;

import chameleon.core.element.Element;
import chameleon.eclipse.presentation.outline.ChameleonOutlineSelector;
import chameleon.exception.ModelException;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.variable.FormalParameter;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;

public class JavaOutlineSelector extends ChameleonOutlineSelector {

	@Override
	public boolean isAllowed(Element<?> element) throws ModelException {
		return (! (element instanceof FormalParameter)) && 
		       (! (element instanceof TypeParameter)) &&
		       super.isAllowed(element);
	}

	@Override
	public List<Element> outlineChildren(Element<?> element) throws ModelException {
		List<Element> result = super.outlineChildren(element);
		if(element instanceof MemberVariableDeclarator) {
			MemberVariableDeclarator decl = (MemberVariableDeclarator) element;
			result.addAll(decl.getIntroducedMembers());
		}
		return result;
	}
	
	
}
