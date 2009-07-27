package jnome.core.expression;

import java.util.ArrayList;
import java.util.List;

import jnome.core.type.JavaTypeReference;

import org.rejuse.association.Reference;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.PrimitiveTotalPredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.Invocation;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.ClassBody;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeElement;
import chameleon.core.type.TypeReference;
import chameleon.core.type.inheritance.SubtypeRelation;
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
	private Reference<ConstructorInvocation,JavaTypeReference> _typeReference = new Reference<ConstructorInvocation,JavaTypeReference>(this);


  public JavaTypeReference getTypeReference() {
    return (JavaTypeReference)_typeReference.getOtherEnd();
  }

    public void setTypeReference(JavaTypeReference type) {
        Reference<? extends TypeReference, ? super ConstructorInvocation> tref = type.parentLink();
        Reference<? extends JavaTypeReference, ? super ConstructorInvocation> ref = (Reference<? extends JavaTypeReference, ? super ConstructorInvocation>)tref;
        _typeReference.connectTo(ref);
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

	private Reference<ConstructorInvocation,ClassBody> _body = new Reference<ConstructorInvocation,ClassBody>(this);
	
	public ClassBody body() {
		return _body.getOtherEnd();
	}
	
	public void setBody(ClassBody body) {
		if(body == null) {
			_body.connectTo(null);
		} else {
			_body.connectTo(body.parentLink());
		}
	}

  private Type createAnonymousType() throws LookupException {
  	final Type anon = new RegularType(new SimpleNameSignature("TODO"));
  	TypeReference tref = getTypeReference();
 	  Type writtenType = tref.getType();
	  List<NormalMethod> superMembers = writtenType.directlyDeclaredMembers(NormalMethod.class);
	  new PrimitiveTotalPredicate<NormalMethod>() {
		  @Override
		  public boolean eval(NormalMethod object) {
			  return object.is(language().CONSTRUCTOR) == Ternary.TRUE;
		  }
	  }.filter(superMembers);
	  for(NormalMethod method: superMembers) {
	  	anon.add(method.clone());
	  }
	  for(TypeElement element : body().elements()) {
	  	anon.add(element.clone());
	  }
	  anon.addInheritanceRelation(new SubtypeRelation(tref.clone()));
	  // Attach the created type to this element.
		anon.setUniParent(this);
		return anon;
	}

  protected Type actualType() throws LookupException {
    if (body() == null) {
      // Switching to target or not happens in getContext(Element) invoked by the type reference.
      return getTypeReference().getType();
    }
    else {
      return getAnonymousInnerType();
    }
  }
  
//	private Reference<ConstructorInvocation,Type> _anonymousType= new Reference<ConstructorInvocation,Type>(this);

  
  public Type getAnonymousInnerType() throws LookupException {
  	if(body() == null) {
  		return null;
  	} else {
  	  return createAnonymousType();
  	}
  }

//  public RegularMethod getConstructor() throws LookupException {
//    Type type = (Type)getTypeReference().getType(); // Not the actual type
//    return type.getConstructor(getActualParameterTypes());
//  }

//  public RegularMethod getMethod() throws LookupException {
//    return getConstructor();
//  }

  public boolean superOf(InvocationTarget target) throws LookupException {
    if(!(target instanceof ConstructorInvocation)) {
      return false;
    }
    ConstructorInvocation acc =(ConstructorInvocation)target;
    if(! getMethod().equals(acc.getMethod())) {
      return false;
    }
    if((body() != null) || (acc.body() != null)) {
      return false;
    }
    List varInits = getActualParameters();
    List otherVarInits = acc.getActualParameters();
    for(int i=0; i< varInits.size(); i++) {
      if(! ((InvocationTarget)varInits.get(i)).compatibleWith((InvocationTarget)otherVarInits.get(i))) {
        return false;
      }
    }
    if((getTargetExpression() == null) && (acc.getTargetExpression() == null)) {
      return true;
    } else if((getTargetExpression() != null) && (acc.getTargetExpression() != null)) {
      return getTargetExpression().compatibleWith(acc.getTargetExpression());
    } else {
      return false;
    }
  }

  protected ConstructorInvocation cloneInvocation(InvocationTarget target) {
    ConstructorInvocation result = new ConstructorInvocation((JavaTypeReference)getTypeReference().clone(), (Expression)target);
    if(body() != null) {
      result.setBody(body().clone());
    }
    return result;
  }

  public void prefix(InvocationTarget target) throws LookupException {
    if(getTarget() != null) {
      getTarget().prefixRecursive(target);
    }
  }

 /*@
   @ also public behavior
   @
   @ post getAnonymousInnerType() != null ==> \result.contains(getAnonymousInnerType());
   @*/
  public List<Element> children() {
    List<Element> result = super.children();
    Util.addNonNull(body(), result);
    return result;
  }
  
  public NormalMethod getMethod() throws LookupException {
  	InvocationTarget target = getTarget();
  	NormalMethod result;
  	if(getAnonymousInnerType() != null) {
  		// @STRANGE!!! Inline this, and it no longer compiles.
  		Type anon = getAnonymousInnerType();
  		LookupStrategy tctx = anon.targetContext();
  		result = tctx.lookUp(selector());
  	} else if(target == null) {
      result = lexicalLookupStrategy().lookUp(selector());
  	} else {
  		result = getTarget().targetContext().lookUp(selector());
  	}
		if (result == null) {
			throw new LookupException("Lookup in constructor invocation returned null",this);
		}
    return result;
  }

  public class ConstructorSelector extends DeclarationSelector<NormalMethod> {
    
    public NormalMethod filter(Declaration declaration) throws LookupException {
    	NormalMethod result = null;
			NormalMethod decl = (NormalMethod) declaration;
			List<Type> actuals = getActualParameterTypes();
			List<Type> formals = decl.header().getParameterTypes();
			if (new MoreSpecificTypesOrder().contains(actuals, formals) && (decl.is(language().CONSTRUCTOR)==Ternary.TRUE)) {
					result = decl;
			}
      return result;
    }

    @Override
    public WeakPartialOrder<NormalMethod> order() {
      return new WeakPartialOrder<NormalMethod>() {
        @Override
        public boolean contains(NormalMethod first, NormalMethod second)
            throws LookupException {
          return new MoreSpecificTypesOrder().contains(first.header().getParameterTypes(), second.header().getParameterTypes());
        }
      };
    }

		@Override
		public Class<NormalMethod> selectedClass() {
			return NormalMethod.class;
		}
  }
  
	@Override
	public DeclarationSelector<NormalMethod> selector() {
		return new ConstructorSelector();
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

//	// COPIED FROM chameleon.core.type.Type
//	@SuppressWarnings("unchecked")
//	public <T extends Declaration> Set<T> declarations(DeclarationSelector<T> selector) throws LookupException {
//    Set<Declaration> tmp = declarations();
//    Set<T> result = selector.selection(tmp);
//    return result;
//	}


}
