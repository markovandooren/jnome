package be.kuleuven.cs.distrinet.jnome.core.expression;


import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends Expression {

  public ClassLiteral(TypeReference tref) {
    setTarget(tref);
  }

  protected Type actualType() throws LookupException {
  	BasicJavaTypeReference tref = (BasicJavaTypeReference) language(ObjectOrientedLanguage.class).createTypeReferenceInNamespace("java.lang.Class", view().namespace());
  	tref.addArgument(new BasicTypeArgument(target().clone()));
  	tref.setUniParent(this);
  	return tref.getElement();
  }

  public ClassLiteral clone() {
    TypeReference target = target();
		TypeReference clone = (target == null ? null : target.clone());
		ClassLiteral result = new ClassLiteral(clone);
    return result;
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
	public VerificationResult verifySelf() {
		return Valid.create();
	}

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    return getTypeReference().getType().getTypeAccessibilityDomain();
//  }
}
