package be.kuleuven.cs.distrinet.jnome.core.expression;


import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaBasicTypeArgument;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends Expression {

  public ClassLiteral(TypeReference tref) {
    setTarget(tref);
  }

  protected Type actualType() throws LookupException {
  	BasicJavaTypeReference tref = (BasicJavaTypeReference) language(ObjectOrientedLanguage.class).createTypeReferenceInNamespace("java.lang.Class", view().namespace());
  	tref.addArgument(language(Java.class).createBasicTypeArgument(clone(target())));
  	tref.setUniParent(this);
  	return tref.getElement();
  }

  protected ClassLiteral cloneSelf() {
		return new ClassLiteral(null);
  }
  
	/**
	 * TARGET
	 */
	private Single<TypeReference> _typeReference = new Single<TypeReference>(this);

  
  public TypeReference target() {
    return _typeReference.getOtherEnd();
  }
  
  public void setTarget(TypeReference type) {
    set(_typeReference,type);
  }

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    return getTypeReference().getType().getTypeAccessibilityDomain();
//  }
}
