package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Signature;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;

public abstract class AbstractAnonymousInnerClass extends AnonymousType {
	
	public AbstractAnonymousInnerClass() {
	}

	public AbstractAnonymousInnerClass(String name) {
		super(name);
	}

	@Override
	public List<InheritanceRelation> inheritanceRelations() {
		List<InheritanceRelation> result = new ArrayList<InheritanceRelation>();
		SubtypeRelation subtypeRelation = new SubtypeRelation(typeReference().clone());
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
	public void setName(String name) {
		throw new ChameleonProgrammerException();
	}
	
	@Override
	public void setSignature(Signature signature) {
		throw new ChameleonProgrammerException();
	}
	

}
