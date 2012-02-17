package jnome.core.expression.invocation;



import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.reference.CrossReferenceTarget;
import chameleon.oo.expression.MethodInvocation;
import chameleon.support.member.simplename.method.NormalMethod;

/**
 * @author Marko van Dooren
 */

public abstract class ConstructorDelegation extends MethodInvocation<NormalMethod> {

  public ConstructorDelegation(CrossReferenceTarget target) {
    super(target);
  }

  @Override
  public DeclarationSelector<NormalMethod> createSelector() {
    return new NamelessConstructorSelector(this);
  }

}
