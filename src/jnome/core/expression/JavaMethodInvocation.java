package jnome.core.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jnome.core.language.Java;
import jnome.core.method.JavaVarargsOrder;
import jnome.core.type.ArrayType;
import jnome.core.type.JavaTypeReference;
import jnome.core.variable.MultiFormalParameter;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.Signature;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.MethodHeader;
import chameleon.core.reference.CrossReference;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.support.member.MoreSpecificTypesOrder;
import chameleon.support.member.simplename.SimpleNameMethodSignature;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;

public class JavaMethodInvocation extends RegularMethodInvocation<JavaMethodInvocation> {

	public JavaMethodInvocation(String name, InvocationTarget target) {
		super(name, target);
	}


	
  public class JavaMethodSelector extends DeclarationSelector<NormalMethod> {

    public boolean selectedRegardlessOfName(NormalMethod declaration) throws LookupException {
  		boolean result = declaration.is(language(ObjectOrientedLanguage.class).CONSTRUCTOR) != Ternary.TRUE;
  		if(result) {
  			Signature signature = declaration.signature();
  			if(signature instanceof SimpleNameMethodSignature) {
  				SimpleNameMethodSignature sig = (SimpleNameMethodSignature)signature;
  				List<Type> actuals = getActualParameterTypes();
  				List<FormalParameter> formals = declaration.formalParameters();
  				List<Type> formalTypes = sig.parameterTypes();
  				
          int nbActuals = actuals.size();
          int nbFormals = formals.size();
          if(nbActuals == nbFormals){
          	// POTENTIALLY
						result = MoreSpecificTypesOrder.create().contains(actuals,formalTypes);
          } else if
          // varargs rubbish
          	 (
          			 (formals.get(nbFormals - 1) instanceof MultiFormalParameter)
          			 && 
          			 (nbActuals >= nbFormals - 1)
          	 )
          	 {
          	// POTENTIALLY
						result = JavaVarargsOrder.create().contains(actuals,formalTypes);
          } else {
          	result = false;
          }
          if(result) {
          	List<ActualTypeArgument> actualTypeArguments = typeArguments();
          	int actualTypeArgumentsSize = actualTypeArguments.size();
						if(actualTypeArgumentsSize > 0) {
          		List<TypeParameter> formalTypeParameters = declaration.typeParameters();
          		result = actualTypeArgumentsSize == formalTypeParameters.size();
          		if(result) {
          			for(int i=0; result && i < actualTypeArgumentsSize; i++) {
          				result = formalTypeParameters.get(i).canBeAssigned(actualTypeArguments.get(i));
          			}
          		}
          	}
          }
  			}
  		}
  		return result;
    }
    
  	@Override
    public boolean selectedBasedOnName(Signature signature) throws LookupException {
  		boolean result = false;
  		if(signature instanceof SimpleNameMethodSignature) {
  			SimpleNameMethodSignature sig = (SimpleNameMethodSignature)signature;
  			result = sig.name().equals(name());
  		}
  		return result;
    }

    @Override
    public WeakPartialOrder<NormalMethod> order() {
      return new WeakPartialOrder<NormalMethod>() {
        @Override
        public boolean contains(NormalMethod first, NormalMethod second)
            throws LookupException {
          return MoreSpecificTypesOrder.create().contains(((MethodHeader) first.header()).getParameterTypes(), ((MethodHeader) second.header()).getParameterTypes());
        }
      };
    }
		@Override
		public Class<NormalMethod> selectedClass() {
			return NormalMethod.class;
		}

		@Override
		public String selectionName() {
			return name();
		}
  }
  
  public static class ConstraintSet {
  	
  	private OrderedMultiAssociation<ConstraintSet, Constraint> _constraints = new OrderedMultiAssociation<ConstraintSet, Constraint>(this);
  	
  	public List<Constraint> constraints() {
  		return _constraints.getOtherEnds();
  	}
  	
  	public void add(Constraint constraint) {
  		if(constraint != null) {
  			_constraints.add(constraint.parentLink());
  		}
  	}
  	
  	public void remove(Constraint constraint) {
  		if(constraint != null) {
  			_constraints.remove(constraint.parentLink());
  		}
  	}
  	
  	
  	public void replace(Constraint oldConstraint, Constraint newConstraint) {
  		if(oldConstraint != null && newConstraint != null) {
  			_constraints.replace(oldConstraint.parentLink(), newConstraint.parentLink());
  		}
  	}
  	
  	public List<TypeParameter> typeParameters() {
  		return _typeParameters;
  	}
  	
  	private List<TypeParameter> _typeParameters;
  }

  private static class Constraint {
  	
  	private SingleAssociation<Constraint, ConstraintSet> _parentLink = new SingleAssociation<Constraint, ConstraintSet>(this);
  	
  	public SingleAssociation<Constraint, ConstraintSet> parentLink() {
  		return _parentLink;
  	}
  	
  	public ConstraintSet parent() {
  		return _parentLink.getOtherEnd();
  	}
  	// resolve()
  }
  
  private abstract static class FirstPhaseConstraint extends Constraint {
  	
  	public FirstPhaseConstraint(Type type, JavaTypeReference tref) {
  	  _type = type;
  	  _typeReference = tref;
  	}
  	
  	private Type _type;
  	
  	public Type type() {
  		return _type;
  	}
  	
  	private JavaTypeReference _typeReference;
  	
  	public JavaTypeReference typeReference() {
  		return _typeReference;
  	}
  	
  	public List<SecondPhaseConstraint> process() throws LookupException {
  		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
  		if(! type().equals(type().language(ObjectOrientedLanguage.class).getNullType())) {
  			result.addAll(processSpecifics());
  		}
  		return result;
  	}
  	
  	public abstract List<SecondPhaseConstraint> processSpecifics() throws LookupException;
  	
  	public boolean involvesTypeParameter(TypeReference tref) throws LookupException {
  		return ! involvedTypeParameters(tref).isEmpty();
  	}
  	
  	public List<TypeParameter> involvedTypeParameters(TypeReference tref) throws LookupException {
  		List<CrossReference> list = tref.descendants(CrossReference.class, new UnsafePredicate<CrossReference, LookupException>() {

				@Override
				public boolean eval(CrossReference object) throws LookupException {
					return parent().typeParameters().contains(object.getDeclarator());
				}
			});
  		List<TypeParameter> parameters = new ArrayList<TypeParameter>();
  		for(CrossReference cref: list) {
  			parameters.add((TypeParameter) cref.getElement());
  		}
  		return parameters;
  	}

  	public Java language() {
  		return type().language(Java.class);
  	}
  }
  /**
   * A << F
   * 
   * Type << JavaTypeReference
   * 
   * @author Marko van Dooren
   */
  private static class SSConstraint extends FirstPhaseConstraint {

		public SSConstraint(Type type, JavaTypeReference tref) {
			super(type,tref);
		}

		@Override
		public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
			List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
			Declaration declarator = typeReference().getDeclarator();
			Type type = type();
			if(type().is(language().PRIMITIVE_TYPE) == Ternary.TRUE) {
				// If A is a primitive type, then A is converted to a reference type U via
				// boxing conversion and this algorithm is applied recursively to the constraint
				// U << F
				SSConstraint recursive = new SSConstraint(language().box(type()), typeReference());
				result.addAll(recursive.process());
			} else if(parent().typeParameters().contains(declarator)) {
				// Otherwise, if F=Tj, then the constraint Tj :> A is implied.
					result.add(new SupertypeConstraint((TypeParameter) declarator, type()));
			} else if(typeReference().arrayDimension() > 0) {
				// If F=U[], where the type U involves Tj, then if A is an array type V[], or
				// a type variable with an upper bound that is an array type V[], where V is a
				// reference type, this algorithm is applied recursively to the constraint V<<U

				// The "involves Tj" condition for U is the same as "involves Tj" for F.
				if(type instanceof ArrayType && involvesTypeParameter(typeReference())) {
					Type componentType = ((ArrayType)type).componentType();
					if(componentType.is(language().REFERENCE_TYPE) == Ternary.TRUE) {
						JavaTypeReference componentTypeReference = typeReference().clone();
						componentTypeReference.setUniParent(typeReference());
						componentTypeReference.decreaseArrayDimension(1);
						SSConstraint recursive = new SSConstraint(componentType, componentTypeReference);
						result.addAll(recursive.process());
						// FIXME: can't we unwrap the entire array dimension at once? This seems rather inefficient.
					}
				}
			} else {
				List<ActualTypeArgument> actuals = typeReference().typeArguments();
				for(ActualTypeArgument argument: actuals) {
					if(argument instanceof BasicTypeArgument) {
						BasicTypeArgument basic = (BasicTypeArgument)argument;
						if(involvesTypeParameter(basic.typeReference())) {
							Set<Type> supers = type().getAllSuperTypes();
						}
					} else if(argument instanceof ExtendsWildCard) {
						
					} else if(argument instanceof SuperWildCard) {
						
					}
				}
			}
 			return result;
		}
  	
  }

  private static class GGConstraint extends FirstPhaseConstraint {

		public GGConstraint(Type type, JavaTypeReference tref) {
			super(type,tref);
		}

		@Override
		public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
			return null;
		}
  	
  }

  private static class EQConstraint extends FirstPhaseConstraint {

		public EQConstraint(Type type, JavaTypeReference tref) {
			super(type,tref);
		}

		@Override
		public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
			return null;
		}
  	
  }
 
  private abstract static class SecondPhaseConstraint extends Constraint {
  	
  	public SecondPhaseConstraint(TypeParameter param, Type type) {
  	  _type = type;	
  	  _typeParameter = param;
  	}
  	
  	private Type _type;
  	
  	public Type type() {
  		return _type;
  	}
  	
  	private TypeParameter _typeParameter;
  	
  	public TypeParameter typeParameter() {
  		return _typeParameter;
  	}
  	
  }

  private static class EqualTypeConstraint extends SecondPhaseConstraint {

		public EqualTypeConstraint(TypeParameter param, Type type) {
			super(param,type);
		}
  	
  }
  
  private static class SubtypeConstraint extends SecondPhaseConstraint {

		public SubtypeConstraint(TypeParameter param, Type type) {
			super(param,type);
		}
  	
  }
  
  private static class SupertypeConstraint extends SecondPhaseConstraint {

		public SupertypeConstraint(TypeParameter param, Type type) {
			super(param,type);
		}
  	
  }
}