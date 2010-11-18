package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.type.AnonymousInnerClass;
import jnome.core.type.BasicJavaTypeReference;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.oo.type.ClassBody;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 *
 */
public class ConstructorInvocation extends RegularMethodInvocation<ConstructorInvocation> implements DeclarationContainer<ConstructorInvocation, Element> {

  /**
   * @param target
   */
  public ConstructorInvocation(BasicJavaTypeReference type, InvocationTarget target) {
    super(type.name(),target);
    setTypeReference(type);
  }

  public Expression getTargetExpression() {
    return (Expression)getTarget();
  }

	/**
	 * TYPE REFERENCE
	 */
	private SingleAssociation<ConstructorInvocation,BasicJavaTypeReference> _typeReference = new SingleAssociation<ConstructorInvocation,BasicJavaTypeReference>(this);


  public BasicJavaTypeReference getTypeReference() {
    return _typeReference.getOtherEnd();
  }

    public void setTypeReference(BasicJavaTypeReference type) {
    	setAsParent(_typeReference, type);
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

    
    
	private SingleAssociation<ConstructorInvocation,Type> _body = new SingleAssociation<ConstructorInvocation,Type>(this);
	
	public void setBody(ClassBody body) {
		if(body == null) {
			_body.connectTo(null);
		} else {
			_body.connectTo(createAnonymousType(body).parentLink());
		}
	}

  private Type createAnonymousType(ClassBody body) {
  	RegularType anon = new AnonymousInnerClass(this);
	  anon.setBody(body);
		return anon;
	}

  protected Type actualType() throws LookupException {
    Type anonymousInnerType = getAnonymousInnerType();
		if (anonymousInnerType == null) {
      // Switching to target or not happens in getContext(Element) invoked by the type reference.
      return getTypeReference().getType();
    }
    else {
      return anonymousInnerType;
    }
  }
  
  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
  	X result = actualType().targetContext().lookUp(selector);
  	if(result == null) {
  		actualType().targetContext().lookUp(selector);
			throw new LookupException("Constructor returned by invocation is null", this);
  	} else {
		  return result;
  	}
  }

  
  public Type getAnonymousInnerType() {
  	return _body.getOtherEnd();
  }
  
  private void setAnonymousType(Type anonymous) {
  	if(anonymous == null) {
  		_body.connectTo(null);
  	} else {
  		_body.connectTo(anonymous.parentLink());
  	}
  }

  protected ConstructorInvocation cloneInvocation(InvocationTarget target) {
    ConstructorInvocation result = new ConstructorInvocation((BasicJavaTypeReference)getTypeReference().clone(), (Expression)target);
    Type anonymousInnerType = getAnonymousInnerType();
		if(anonymousInnerType != null) {
      result.setAnonymousType(anonymousInnerType.clone());
    }
    return result;
  }

//  public void prefix(InvocationTarget target) throws LookupException {
//    if(getTarget() != null) {
//      getTarget().prefixRecursive(target);
//    }
//  }

 /*@
   @ also public behavior
   @
   @ post getAnonymousInnerType() != null ==> \result.contains(getAnonymousInnerType());
   @*/
  public List<Element> children() {
    List<Element> result = super.children();
    Util.addNonNull(getAnonymousInnerType(), result);
    Util.addNonNull(getTypeReference(), result);
    return result;
  }
  
  @Override
  public LookupStrategy lexicalLookupStrategy(Element element) throws LookupException {
    if ((element == getTypeReference()) && (getTargetExpression() != null)) {
      return getTargetExpression().targetContext();
    } else {
      return super.lexicalLookupStrategy(element);
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

	public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}

//	@Override
//	public void setName(String name) {
//		getTypeReference().setName(name);
//	}

}
