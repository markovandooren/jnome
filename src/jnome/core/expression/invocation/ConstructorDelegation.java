package jnome.core.expression.invocation;



import chameleon.core.expression.Invocation;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.support.member.simplename.method.NormalMethod;

/**
 * @author Marko van Dooren
 */

public abstract class ConstructorDelegation<E extends ConstructorDelegation<E>>
    extends Invocation<E, NormalMethod> {

  public ConstructorDelegation(InvocationTarget target) {
    super(target);
  }

  @Override
  public DeclarationSelector<NormalMethod> createSelector() {
    return new NamelessConstructorSelector(this);
  }

}
