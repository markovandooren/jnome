package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.type.AnonymousInnerClass;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;

import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.Invocation;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.TwoPhaseDeclarationSelector;
import chameleon.core.method.MethodSignature;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.ClassBody;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.support.member.MoreSpecificTypesOrder;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 *
 */
public class ConstructorInvocation extends Invocation<ConstructorInvocation, NormalMethod> implements DeclarationContainer<ConstructorInvocation, Element> {

  /**
   * @param target
   */
  public ConstructorInvocation(JavaTypeReference type, InvocationTarget target) {
    super(target);
    setTypeReference(type);
  }

  public Expression getTargetExpression() {
    return (Expression)getTarget();
  }

	/**
	 * TYPE REFERENCE
	 */
	private SingleAssociation<ConstructorInvocation,JavaTypeReference> _typeReference = new SingleAssociation<ConstructorInvocation,JavaTypeReference>(this);


  public JavaTypeReference getTypeReference() {
    return _typeReference.getOtherEnd();
  }

    public void setTypeReference(JavaTypeReference type) {
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
	
//	public ClassBody body() {
//		return _body.getOtherEnd();
//	}
	
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
  	return actualType().targetContext().lookUp(selector);
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

//  public NormalMethod getMethod() throws LookupException {
//  	InvocationTarget target = getTarget();
//  	NormalMethod result;
//  	if(getAnonymousInnerType() != null) {
//  		LookupStrategy tctx = getAnonymousInnerType().targetContext();
//  		result = tctx.lookUp(selector());
//  	} else if(target == null) {
//      result = lexicalLookupStrategy().lookUp(selector());
//  	} else {
//  		result = getTarget().targetContext().lookUp(selector());
//  	}
//		if (result == null) {
//			throw new LookupException("Lookup in constructor invocation returned null",this);
//		}
//    return result;
//  }

  public class ConstructorSelector extends TwoPhaseDeclarationSelector<NormalMethod> {
    
  	public ConstructorSelector(String name) {
  		__name = name;
  	}
  	
  	String __name;
  	
    public boolean selectedRegardlessOfName(NormalMethod declaration) throws LookupException {
    	return declaration.is(language(ObjectOrientedLanguage.class).CONSTRUCTOR)==Ternary.TRUE;
    }
    
		@Override
		public boolean selectedBasedOnName(Signature signature) throws LookupException {
    	boolean result = false;
			if(signature instanceof MethodSignature) {
				MethodSignature<?,?> sig = (MethodSignature<?,?>)signature;
//			  if(sig.nearestAncestor(Type.class).signature().sameAs(getTypeReference().signature())) {
			  if(sig.name().equals(__name)) {
			  	List<Type> actuals = getActualParameterTypes();
			  	List<Type> formals = ((MethodSignature)signature).parameterTypes();
			  	if (MoreSpecificTypesOrder.create().contains(actuals, formals)) {
			  		result = true;
			  	}
			  }
			}
      return result;
		}

    @Override
    public WeakPartialOrder<NormalMethod> order() {
      return new WeakPartialOrder<NormalMethod>() {
        @Override
        public boolean contains(NormalMethod first, NormalMethod second)
            throws LookupException {
          return MoreSpecificTypesOrder.create().contains(first.header().formalParameterTypes(), second.header().formalParameterTypes());
        }
      };
    }

		@Override
		public Class<NormalMethod> selectedClass() {
			return NormalMethod.class;
		}

		@Override
		public String selectionName(DeclarationContainer<?,?> container) {
			return __name;
		}



  }
  
	@Override
	public DeclarationSelector<NormalMethod> createSelector() throws LookupException {
		return new ConstructorSelector(getTypeReference().getElement().signature().name());
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

}
