package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationCollector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclaratorSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.SelectionResult;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ClassBody;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.RegularMethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousInnerClass;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;

/**
 * @author Marko van Dooren
 *
 */
public class ConstructorInvocation extends RegularMethodInvocation implements DeclarationContainer {

  /**
   * @param target
   */
  public ConstructorInvocation(BasicJavaTypeReference type, CrossReferenceTarget target) {
    super(type.name(),target);
    setTypeReference(type);
  }

  public Expression getTargetExpression() {
    return (Expression)getTarget();
  }
  
  public boolean isDiamondInvocation() {
  	return _isDiamond;
  }
  
  public void setDiamond(boolean value) {
  	_isDiamond = value;
  }
  
  private boolean _isDiamond;

	/**
	 * TYPE REFERENCE
	 */
	private Single<BasicJavaTypeReference> _typeReference = new Single<BasicJavaTypeReference>(this);


  public BasicJavaTypeReference getTypeReference() {
    return _typeReference.getOtherEnd();
  }

    public void setTypeReference(BasicJavaTypeReference type) {
    	set(_typeReference, type);
    }

  /******************
   * ANONYMOUS TYPE *
   ******************/

//    public void setAnonymousType(Type type) {
//      if(type != null) {
//        _anon.connectTo(type.parentLink());
//      } else {
//        _anon.connectTo(null);
//      }
//    }
//
//    public Type getAnonymousInnerType() {
//    return _anon.getOtherEnd();
//  }

    
    
	private Single<Type> _anonymousType = new Single<Type>(this);
	
	public void setBody(ClassBody body) {
		if(body == null) {
			_anonymousType.connectTo(null);
		} else {
			set(_anonymousType,createAnonymousType(body));
		}
	}

  private Type createAnonymousType(ClassBody body) {
  	RegularType anon = new AnonymousInnerClass(this);
	  anon.setBody(body);
		return anon;
	}

  protected Type actualType() throws LookupException {
    Type result = getAnonymousInnerType();
		if (result == null) {
      result = getTypeReference().getType();
    }
		return result;
  }
  
  protected Type auxType() throws LookupException {
    Type result = getAnonymousInnerType();
		if (result == null) {
      result = getTypeReference().getType();
      if(isDiamondInvocation()) {
      	result = (Type) result.origin();
      }
    }
		return result;
  }
  
  protected Type referencedType() throws LookupException {
  	Type result = getTypeReference().getType();
  	if(isDiamondInvocation()) {
  		result = (Type) result.origin();
  	}
  	return result;
  }
  
  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
  	//FIXME this is wrong. The constructor selector should not redirect to the origin if it is a diamond
  	//      the constructor invocation should do it.
//		DeclarationCollector<X> collector = new DeclarationCollector<X>(selector);
//  	actualType().targetContext().lookUp(collector);
//  	return collector.result();

  
		DeclarationCollector<X> collector = new DeclarationCollector<X>(selector);
  	auxType().targetContext().lookUp(collector);
  	return collector.result();
}
  
  @Override
  public Declaration getDeclarator() throws LookupException {
		DeclarationCollector<Declaration> collector = new DeclarationCollector<Declaration>(new DeclaratorSelector(selector()));
  	auxType().targetContext().lookUp(collector);
  	return collector.result();
  }

  
  public Type getAnonymousInnerType() {
  	return _anonymousType.getOtherEnd();
  }
  
  private void setAnonymousType(Type anonymous) {
  	set(_anonymousType,anonymous);
  }

  protected ConstructorInvocation cloneSelf() {
    return new ConstructorInvocation(clone(getTypeReference()), null);
  }

//  public void prefix(InvocationTarget target) throws LookupException {
//    if(getTarget() != null) {
//      getTarget().prefixRecursive(target);
//    }
//  }

  @Override
  public LookupContext lookupContext(Element element) throws LookupException {
    if ((element == getTypeReference()) && (getTargetExpression() != null)) {
      return getTargetExpression().targetContext();
    } else {
      return super.lookupContext(element);
    }
  }

  @Override
	public DeclarationSelector<NormalMethod> createSelector() throws LookupException {
		return new ConstructorSelector(this, getTypeReference().getElement().signature().name());
	}

	public List<? extends Declaration> locallyDeclaredDeclarations() throws LookupException {
		return declarations();
	}

	public List<? extends Type> declarations() throws LookupException {
		List<Type> result = new ArrayList<Type>();
		if(getAnonymousInnerType() != null) {
			result.add(getAnonymousInnerType());
		}
		return result;
	}

	public <D extends Declaration> List<? extends SelectionResult> declarations(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}

	@Override
	public LookupContext localContext() throws LookupException {
		return language().lookupFactory().createLocalLookupStrategy(this);
	}

}
