package org.aikodi.java.core.expression.invocation;

import java.util.List;

import org.aikodi.chameleon.core.lookup.DeclarationCollector;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguageImpl;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.workspace.View;

/**
 * @author Marko van Dooren
 */
public class SuperConstructorDelegation extends ConstructorDelegation {

  public SuperConstructorDelegation(){
    super(null);
  }

  protected Type actualType() throws LookupException {
    View view = view();
		return view.language(ObjectOrientedLanguage.class).voidType(view.namespace());
  }
  
  // @FIXME: does not work with multiple inheritance. Call is ambiguous.
  public NormalMethod getMethod() throws LookupException {
//	    return nearestAncestor(Type.class).getDirectSuperTypes().get(0).lookupContext().lookUp(selector());
  	return getElement();
  }
  
  protected SuperConstructorDelegation cloneSelf() {
    return new SuperConstructorDelegation();
  }

  @Override
  public NormalMethod getElement() throws LookupException {
  	Type parent = lexical().nearestAncestor(Type.class);
  	if(parent == null) {
  		throw new ChameleonProgrammerException("The super constructor delegation is not inside a type.");
  	}
  	List<Type> types = parent.getProperDirectSuperTypes();
		DeclarationCollector<NormalMethod> collector = new DeclarationCollector<NormalMethod>(selector());
  	for(Type type: types) {
  		type.targetContext().lookUp(collector);
  		if(!collector.willProceed()) {
  			return collector.result();
  		}
  	}
  	throw new LookupException("No super constructor was found.");
  }

	@Override
	public DeclarationSelector<NormalMethod> createSelector() {
		return new SuperConstructorDelegatorSelector(this);
	}

}
