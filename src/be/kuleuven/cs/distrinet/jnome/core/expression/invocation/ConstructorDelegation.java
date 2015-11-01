package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;



import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;

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

  /**
   * @{inheritDoc}
   */
  @Override
  public Class<NormalMethod> referencedType() {
    return NormalMethod.class;
  }
}
