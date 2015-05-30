package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.ConstrainedType;
import org.aikodi.chameleon.oo.type.generics.ElementWithTypeConstraints;
import org.aikodi.chameleon.oo.type.generics.TypeConstraint;
import org.aikodi.chameleon.util.association.Multi;

public class ConstrainedTypeReference extends ElementImpl implements JavaTypeReference, ElementWithTypeConstraints  {

  public ConstrainedTypeReference() {
    
  }
  
  public ConstrainedTypeReference(List<TypeConstraint> constraints) {
    constraints.forEach(c -> addConstraint(c));
  }
  
  @Override
  public Type getElement() throws LookupException {
    final ConstrainedType constrainedType = new ConstrainedType(lowerBound(), upperBound());
    constrainedType.setUniParent(this);
    return constrainedType;
  }

  @Override
  public JavaTypeReference erasedReference() {
    JavaTypeReference result = cloneSelf();
    constraints().forEach(c -> {
      final TypeConstraint clone = c.clone(c);
      clone.setTypeReference(((JavaTypeReference)c.typeReference()).erasedReference());
    });
    return result;
  }

  @Override
  public JavaTypeReference componentTypeReference() {
    return this;
  }

  @Override
  protected ConstrainedTypeReference cloneSelf() {
    return new ConstrainedTypeReference();
  }

  
  private Multi<TypeConstraint> _typeConstraints = new Multi<TypeConstraint>(this);
  {
    _typeConstraints.enableCache();
  }
  
  public List<TypeConstraint> constraints() {
    return _typeConstraints.getOtherEnds();
  }
  
  public void addConstraint(TypeConstraint constraint) {
    add(_typeConstraints,constraint);
  }
  
  @Override
  protected synchronized void flushLocalCache() {
    super.flushLocalCache();
    _typeConstraints.flushCache();
  }
  

}
