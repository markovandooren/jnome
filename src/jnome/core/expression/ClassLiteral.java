package jnome.core.expression;


import java.util.ArrayList;
import java.util.List;

import jnome.core.type.BasicJavaTypeReference;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends Expression<ClassLiteral> {

  public ClassLiteral(TypeReference tref) {
    setTarget(tref);
  }

  protected Type actualType() throws LookupException {
  	BasicJavaTypeReference tref = (BasicJavaTypeReference) language(ObjectOrientedLanguage.class).createTypeReferenceInDefaultNamespace("java.lang.Class");
  	tref.addArgument(new BasicTypeArgument<BasicTypeArgument>(target().clone()));
  	tref.setUniParent(this);
  	return tref.getElement();
  }

  public ClassLiteral clone() {
    TypeReference target = target();
		TypeReference clone = (target == null ? null : target.clone());
		ClassLiteral result = new ClassLiteral(clone);
    return result;
  }
  
  public List<Element> children() {
  	List<Element> result = new ArrayList<Element>();
  	Util.addNonNull(target(), result);
  	return result;
  }
 
	/**
	 * TARGET
	 */
	private SingleAssociation<ClassLiteral,TypeReference> _typeReference = new SingleAssociation<ClassLiteral,TypeReference>(this);

  
  public TypeReference target() {
    return _typeReference.getOtherEnd();
  }
  
  public void setTarget(TypeReference type) {
    _typeReference.connectTo(type.parentLink());
  }

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    return getTypeReference().getType().getTypeAccessibilityDomain();
//  }
}
