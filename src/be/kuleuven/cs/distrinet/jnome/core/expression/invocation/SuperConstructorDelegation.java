package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationCollector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;

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

  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
  	Type parent = nearestAncestor(Type.class);
  	if(parent == null) {
  		throw new ChameleonProgrammerException("The super constructor delegation is not inside a type.");
  	}
  	List<Type> types = parent.getDirectSuperTypes();
		DeclarationCollector<X> collector = new DeclarationCollector<X>(selector);
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
