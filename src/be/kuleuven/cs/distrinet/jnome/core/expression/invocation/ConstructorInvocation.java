package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.DeclarationCollector;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.type.ClassBody;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.member.simplename.method.RegularMethodInvocation;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousInnerClass;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;

/**
 * @author Marko van Dooren
 *
 */
public class ConstructorInvocation extends RegularMethodInvocation implements DeclarationContainer {

  /**
   * Create a new constructor invocation of the given type.
   * 
   * @param type A reference to the type of which an object is constructed.
   * @param outerObject An expression for the outer object of the object that is constructed (if any).
   */
  public ConstructorInvocation(BasicJavaTypeReference type, CrossReferenceTarget outerObject) {
    super(type.name(),outerObject);
    setTypeReference(type);
  }

  public boolean isDiamondInvocation() {
    return _isDiamond;
  }

  public void setDiamond(boolean value) {
    _isDiamond = value;
  }

  private boolean _isDiamond;

  /**
   * TYPE REFERENCE
   */
  private Single<BasicJavaTypeReference> _typeReference = new Single<BasicJavaTypeReference>(this);


  public BasicJavaTypeReference getTypeReference() {
    return _typeReference.getOtherEnd();
  }

  public void setTypeReference(BasicJavaTypeReference type) {
    set(_typeReference, type);
  }

  /******************
   * ANONYMOUS TYPE *
   ******************/

  //    public void setAnonymousType(Type type) {
  //      if(type != null) {
  //        _anon.connectTo(type.parentLink());
  //      } else {
  //        _anon.connectTo(null);
  //      }
  //    }
  //
  //    public Type getAnonymousInnerType() {
  //    return _anon.getOtherEnd();
  //  }



  private Single<Type> _anonymousType = new Single<Type>(this);

  public void setBody(ClassBody body) {
    if(body == null) {
      _anonymousType.connectTo(null);
    } else {
      set(_anonymousType,createAnonymousType(body));
    }
  }

  private Type createAnonymousType(ClassBody body) {
    RegularType anon = new AnonymousInnerClass(this);
    anon.setBody(body);
    return anon;
  }

  protected Type actualType() throws LookupException {
    Type result = getAnonymousInnerType();
    if (result == null) {
      result = getTypeReference().getElement();
    }
    return result;
  }

  protected Type auxType() throws LookupException {
    Type result = getAnonymousInnerType();
    if (result == null) {
      result = getTypeReference().getElement();
      if(isDiamondInvocation()) {
        result = (Type) result.origin();
      }
    }
    return result;
  }

  @Override
  public NormalMethod getElement() throws LookupException {
    DeclarationCollector<NormalMethod> collector = new DeclarationCollector<NormalMethod>(selector());
    auxType().targetContext().lookUp(collector);
    return collector.result();
  }

  public Type getAnonymousInnerType() {
    return _anonymousType.getOtherEnd();
  }

  private void setAnonymousType(Type anonymous) {
    set(_anonymousType,anonymous);
  }

  protected ConstructorInvocation cloneSelf() {
    return new ConstructorInvocation(clone(getTypeReference()), null);
  }

  @Override
  public LookupContext lookupContext(Element element) throws LookupException {
    if ((element == getTypeReference()) && (getTarget() != null)) {
      return getTarget().targetContext();
    } else {
      return super.lookupContext(element);
    }
  }

  @Override
  public DeclarationSelector<NormalMethod> createSelector() throws LookupException {
    return new ConstructorSelector(this, getTypeReference().getElement().name());
  }

  public List<? extends Declaration> locallyDeclaredDeclarations() throws LookupException {
    return declarations();
  }

  public List<? extends Type> declarations() throws LookupException {
    List<Type> result = new ArrayList<Type>();
    if(getAnonymousInnerType() != null) {
      result.add(getAnonymousInnerType());
    }
    return result;
  }

  public <D extends Declaration> List<? extends SelectionResult> declarations(DeclarationSelector<D> selector) throws LookupException {
    return selector.selection(declarations());
  }

  @Override
  public LookupContext localContext() throws LookupException {
    return language().lookupFactory().createLocalLookupStrategy(this);
  }

}
