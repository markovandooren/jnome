package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;
import jnome.core.variable.MultiFormalParameter;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Signature;
import chameleon.core.expression.ActualArgument;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.MethodHeader;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
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
  				declaration.scope().contains(JavaMethodInvocation.this);
          int nbActuals = actuals.size();
          int nbFormals = formals.size();
          if(nbActuals == nbFormals){
          	// Phases 1 and 2 and 3
						result = phases1and2and3(declaration);
          } else if
          // varargs
          	 (
          			 (formals.get(nbFormals - 1) instanceof MultiFormalParameter)
          			 && 
          			 (nbActuals >= nbFormals - 1)
          	 )
          	 {
          	// only Phase 3
						result = variableApplicableBySubtyping(declaration);
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

		private boolean phases1and2and3(NormalMethod method) throws LookupException {
			//return MoreSpecificTypesOrder.create().contains(actuals,formalTypes);
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method);
			List<Type> formalParameterTypesInContext = formalParameterTypesInContext(method,actualTypeParameters);
			return matchingApplicableBySubtyping(method,formalParameterTypesInContext,actualTypeParameters) ||
			       matchingApplicableByConversion(method,formalParameterTypesInContext) ||
			       variableApplicableBySubtyping(method);
		}
		
		/**
		 * Determine the types of the formal parameter of the given method in the context of the enclosing method invocation.
		 * This involves substituting the formal type parameters in the type of the formal parameters
		 * with the actual type arguments.
		 * 
		 * If explicit type arguments are present, then these are used as the actual type arguments. Otherwise, the actual
		 * type arguments are inferred.
		 * @throws LookupException 
		 */
		private List<Type> formalParameterTypesInContext(NormalMethod<?,?,?> method,TypeAssignmentSet actualTypeParameters) throws LookupException {
			List<ActualTypeArgument> typeArguments = typeArguments();
			List<TypeParameter> parameters = method.typeParameters();
			List<Type> result;
			if(parameters.size() > 0) {
				Java language = method.language(Java.class);
				// Substitute
				List<FormalParameter> formalParameters = method.formalParameters();
				List<TypeReference> references = new ArrayList<TypeReference>();
				for(FormalParameter par: formalParameters) {
					TypeReference tref = par.getTypeReference();
					TypeReference clone = tref.clone();
					clone.setUniParent(tref.parent());
					references.add(clone);
				}
				result = new ArrayList<Type>();
				for(TypeReference tref: references) {
					for(TypeParameter par: actualTypeParameters.assigned()) {
						NonLocalJavaTypeReference.replace(language.reference(actualTypeParameters.type(par)), par, (JavaTypeReference<?>) tref);
					}
					result.add(tref.getElement());
				}
				
			} else {
				result = method.header().formalParameterTypes();
			}
			return result;
		}

		private TypeAssignmentSet actualTypeParameters(NormalMethod<?, ?, ?> method) throws LookupException {
			List<ActualTypeArgument> typeArguments = typeArguments();
			Java language = method.language(Java.class);
			List<TypeParameter> parameters = method.typeParameters();
			TypeAssignmentSet formals;
			if(typeArguments.size() > 0) {
				formals = new TypeAssignmentSet();
				int size = typeArguments.size();
				for(int i=0; i< size; i++) {
					formals.add(new ActualTypeAssignment(parameters.get(i),typeArguments.get(i).upperBound()));
				}
			} else {
				// perform type inference
				FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet();
				List<ActualArgument> actualParameters = actualArgumentList().getActualParameters();
				List<Type> formalParameters = method.header().formalParameterTypes();
				int size = actualParameters.size();
				for(int i=0; i< size; i++) {
					// if the formal parameter type is reference type, add a constraint
					Type argType = actualParameters.get(i).getExpression().getType();
					if(argType.is(language.REFERENCE_TYPE) == Ternary.TRUE) {
						constraints.add(new SSConstraint(language.reference(argType), formalParameters.get(i)));
					}
				}
				formals = constraints.resolve();
			}
			return formals;
		}

		private boolean matchingApplicableBySubtyping(NormalMethod method, List<Type> formalParameterTypesInContext,TypeAssignmentSet actualTypeParameters) throws LookupException {
			boolean result = true;
			int size = formalParameterTypesInContext.size();
			List<ActualArgument> actualParameters = actualArgumentList().getActualParameters();
			for(int i=0; result && i < size; i++) {
				Type formalType = formalParameterTypesInContext.get(i);
				Type actualType = actualParameters.get(i).getExpression().getType();
				result = actualType.subTypeOf(formalType) || convertibleThroughUncheckedConversionAndSubtype(actualType, formalType);
			}
			List<TypeParameter> typeParameters = method.typeParameters();
			if(result && typeParameters.size() > 0) {
				result = actualTypeParameters.valid();
			}
			return result;
		}
		
		private boolean convertibleThroughUncheckedConversionAndSubtype(Type first, Type second) {
			
		}
		
		private boolean matchingApplicableByConversion(NormalMethod method, List<Type> formalParameterTypesInContext) throws LookupException {
		}
		
		private boolean variableApplicableBySubtyping(NormalMethod method) throws LookupException {
			//return JavaVarargsOrder.create().contains(actuals,formalTypes);
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
          return MoreSpecificTypesOrder.create().contains(((MethodHeader) first.header()).formalParameterTypes(), ((MethodHeader) second.header()).formalParameterTypes());
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
}