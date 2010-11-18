package jnome.core.expression.invocation;

import chameleon.core.declaration.DeclarationContainer;
import chameleon.oo.type.Type;

public class NamelessConstructorSelector extends AbstractConstructorSelector {
  /**
	 * 
	 */
	private final ConstructorDelegation<?> _constructorDelegation;

	protected ConstructorDelegation invocation() {
		return _constructorDelegation;
	}
	
	/**
	 * @param constructorDelegation
	 */
	public NamelessConstructorSelector(ConstructorDelegation constructorDelegation) {
		_constructorDelegation = constructorDelegation;
	}

//	@Override
//  public boolean selectedRegardlessOfName(NormalMethod declaration)
//      throws LookupException {
//  	return declaration.is(_constructorDelegation.language(ObjectOrientedLanguage.class).CONSTRUCTOR) == Ternary.TRUE;
//  }
  
//  @Override
//  public boolean selectedBasedOnName(Signature signature) throws LookupException {
//    boolean result = false;
//    if(signature instanceof MethodSignature) {
//    	MethodSignature sig = (MethodSignature)signature;
//      List<Type> actuals = _constructorDelegation.getActualParameterTypes();
//      List<Type> formals = sig.parameterTypes();
//      if (MoreSpecificTypesOrder.create().contains(actuals, formals)) {
//        result = true;
//      }
//    }
//    return result;
//  }

  // @FIXME: generalize along with implementation in
  // SimpleNameMethodInvocation
//  @Override
//  public WeakPartialOrder<NormalMethod> order() {
//    return new WeakPartialOrder<NormalMethod>() {
//      @Override
//      public boolean contains(NormalMethod first, NormalMethod second)
//          throws LookupException {
//        return MoreSpecificTypesOrder.create().contains(first.header()
//            .formalParameterTypes(), second.header().formalParameterTypes());
//      }
//    };
//  }

//  @Override
//  public Class<NormalMethod> selectedClass() {
//    return NormalMethod.class;
//  }

	@Override
	public String selectionName(DeclarationContainer<?,?> container) {
		return _constructorDelegation.nearestAncestor(Type.class).signature().name();
	}
}