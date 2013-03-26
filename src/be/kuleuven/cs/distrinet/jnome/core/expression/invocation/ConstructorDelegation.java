package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;



import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;

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
