package org.aikodi.java.core.type;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Signature;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;

public abstract class AbstractAnonymousInnerClass extends AnonymousType {
	
	public AbstractAnonymousInnerClass(String name) {
		super(name);
	}

	public AbstractAnonymousInnerClass() {
		this("");
	}
	
	@Override
	public List<InheritanceRelation> inheritanceRelations() {
		List<InheritanceRelation> result = new ArrayList<InheritanceRelation>();
		SubtypeRelation subtypeRelation = new SubtypeRelation(clone(typeReference()));
		subtypeRelation.setUniParent(this);
		result.add(subtypeRelation);
		return result;
	}

	public LookupContext lookupContext(Element element) throws LookupException {
  	if(element instanceof SubtypeRelation) {
  		Element parent = parent();
  		if(parent != null) {
  			return lexicalParametersLookupStrategy();
  		} else {
  			throw new LookupException("Parent of type is null when looking for the parent context of a type.");
  		}
  	} else {
  	  return super.lookupContext(element);
  	}
  }

	@Override
	public void setSignature(Signature signature) {
		throw new ChameleonProgrammerException();
	}
	

}
