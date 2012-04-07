package jnome.core.expression;


import jnome.core.type.BasicJavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.expression.Expression;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.util.association.Single;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends Expression {

  public ClassLiteral(TypeReference tref) {
    setTarget(tref);
  }

  protected Type actualType() throws LookupException {
  	BasicJavaTypeReference tref = (BasicJavaTypeReference) language(ObjectOrientedLanguage.class).createTypeReferenceInDefaultNamespace("java.lang.Class");
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
