package jnome.core.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jnome.core.type.JavaTypeReference;

import org.rejuse.association.Reference;
import org.rejuse.association.Relation;
import org.rejuse.logic.ternary.Ternary;

import chameleon.core.MetamodelException;
import chameleon.core.accessibility.AccessibilityDomain;
import chameleon.core.context.Context;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationSelector;
import chameleon.core.expression.Expression;
import chameleon.core.expression.ExpressionContainer;
import chameleon.core.expression.Invocation;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.TypeContainer;
import chameleon.core.type.TypeReference;
import chameleon.support.member.MoreSpecificTypesOrder;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.property.accessibility.EmptyDomain;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 *
 */
public class ConstructorInvocation extends Invocation<ConstructorInvocation, NormalMethod> implements TypeContainer<ConstructorInvocation, ExpressionContainer> {

  /**
   * @param target
   */
  public ConstructorInvocation(JavaTypeReference type, Expression target) {
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

    /*****************************************************************************
   * ANONYMOUS TYPE *
   ****************************************************************************/

    public void setAnonymousType(Type type) {
      if(type != null) {
        _anon.connectTo(type.parentLink());
      } else {
        _anon.connectTo(null);
      }
    }

    public Type getAnonymousInnerType() {
    return _anon.getOtherEnd();
  }

	private Reference<ConstructorInvocation,Type> _anon = new Reference<ConstructorInvocation,Type>(this);

  public Relation getTypesLink() {
    return _anon;
  }

//  public NamespacePart getNamespacePart() {
//	  return ((TypeContainer)this.getParent()).getNamespacePart();
//  }

  public Type getType() throws MetamodelException {
    if (getAnonymousInnerType() == null) {
      // Switching to target or not happens in getContext(Element) invoked by the type reference.
      return getTypeReference().getType();
    }
    else {
      return getAnonymousInnerType();
    }
  }

//  public RegularMethod getConstructor() throws MetamodelException {
//    Type type = (Type)getTypeReference().getType(); // Not the actual type
//    return type.getConstructor(getActualParameterTypes());
//  }

//  public RegularMethod getMethod() throws MetamodelException {
//    return getConstructor();
//  }

  public boolean superOf(InvocationTarget target) throws MetamodelException {
    if(!(target instanceof ConstructorInvocation)) {
      return false;
    }
    ConstructorInvocation acc =(ConstructorInvocation)target;
    if(! getMethod().equals(acc.getMethod())) {
      return false;
    }
    if((getAnonymousInnerType() != null) || (acc.getAnonymousInnerType() != null)) {
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
    if(getAnonymousInnerType() != null) {
      result.setAnonymousType(getAnonymousInnerType().clone());
    }
    return result;
  }

  public void prefix(InvocationTarget target) throws MetamodelException {
    if(getTarget() != null) {
      getTarget().prefixRecursive(target);
    }
  }

  public AccessibilityDomain getTypeAccessibilityDomain() throws MetamodelException {
    return new EmptyDomain();
  }

 /*@
   @ also public behavior
   @
   @ post getAnonymousInnerType() != null ==> \result.contains(getAnonymousInnerType());
   @*/
  public List children() {
    List result = super.children();
    Util.addNonNull(getAnonymousInnerType(), result);
    return result;
  }
  
  public NormalMethod getMethod() throws MetamodelException {
  	InvocationTarget target = getTarget();
  	NormalMethod result;
  	if(getAnonymousInnerType() != null) {
  		// @STRANGE!!! Inline this, and it no longer compiles.
  		Type anon = getAnonymousInnerType();
  		Context tctx = anon.targetContext();
  		result = tctx.lookUp(selector());
  	} else if(target == null) {
      result = lexicalContext().lookUp(selector());
  	} else {
  		result = getTarget().targetContext().lookUp(selector());
  	}
		if (result == null) {
			throw new MetamodelException();
		}
    return result;
  }

  public class ConstructorSelector extends DeclarationSelector<NormalMethod> {
    
    public NormalMethod filter(Declaration declaration) throws MetamodelException {
    	NormalMethod result = null;
			if (selectedClass().isInstance(declaration)) {
				NormalMethod decl = (NormalMethod) declaration;
				List<Type> actuals = getActualParameterTypes();
				List<Type> formals = decl.signature().getParameterTypes();
				if (new MoreSpecificTypesOrder().contains(actuals, formals) && (decl.is(language().CONSTRUCTOR)==Ternary.TRUE)) {
					result = decl;
				}
			}
      return result;
    }

    @Override
    public WeakPartialOrder<NormalMethod> order() {
      return new WeakPartialOrder<NormalMethod>() {
        @Override
        public boolean contains(NormalMethod first, NormalMethod second)
            throws MetamodelException {
          return new MoreSpecificTypesOrder().contains(first.signature().getParameterTypes(), second.signature().getParameterTypes());
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

	public Set<Declaration> declarations() throws MetamodelException {
		Set<Declaration> result = new HashSet<Declaration>();
		result.add(getAnonymousInnerType());
		return result;
	}

	// COPIED FROM chameleon.core.type.Type
	public <T extends Declaration> Set<T> declarations(DeclarationSelector<T> selector) throws MetamodelException {
    Set<Declaration> tmp = declarations();
    Set<T> result = selector.selection(tmp);
    return result;
	}

//  //@STUDENTCREWUP ?
//public Type getTopLevelType() {
//	return null;
//}
//

}
