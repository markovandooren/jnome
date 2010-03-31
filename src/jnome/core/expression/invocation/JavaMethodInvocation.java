package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.method.JavaVarargsOrder;
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
			return matchingApplicableBySubtyping(method) ||
			       matchingApplicableByConversion(method) ||
			       variableApplicableBySubtyping(method);
		}
		
		private List<Type> formalParameterTypesInContext(NormalMethod method) {
			List<ActualTypeArgument> typeArguments = typeArguments();
			List<Type> result;
			if(typeArguments.size() > 0) {
				result = new ArrayList<Type>();
				for(ActualTypeArgument argument: typeArguments) {
					result.add(argument.upperBound());
				}
			} else {
				// perform type inference
				FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet();
				List<ActualArgument> actualParameters = actualArgumentList().getActualParameters();
				List<FormalParameter> formalParameters = method.formalParameters();
				int size = actualParameters.size();
				for(int i=0; i< size; i++) {
					// if the formal parameter type is
					constraints.add(constraint)
				}
			}
			return result;
		}

		private boolean matchingApplicableBySubtyping(NormalMethod method) throws LookupException {
		}
		
		private boolean matchingApplicableByConversion(NormalMethod method) throws LookupException {
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
}