package jnome.core.expression.invocation;

import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.DeclarationCollector;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReferenceTarget;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.workspace.View;

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
//	    return nearestAncestor(Type.class).getDirectSuperTypes().get(0).lexicalLookupStrategy().lookUp(selector());
  	return getElement();
  }
  
  protected SuperConstructorDelegation cloneInvocation(CrossReferenceTarget target) {
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
