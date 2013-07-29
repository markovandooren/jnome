package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.eclipse.view.outline.ChameleonOutlineSelector;
import be.kuleuven.cs.distrinet.chameleon.exception.ModelException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.variable.MemberVariableDeclarator;

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
			result.addAll(decl.getIntroducedMembers());
		}
		return result;
	}
	
	
}
