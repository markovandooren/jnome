package jnome.core.expression.invocation;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.DeclarationCollector;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReferenceTarget;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.ClassBody;
import chameleon.oo.type.Type;
import chameleon.support.member.simplename.method.NormalMethod;

/**
 * @author Marko van Dooren
 */
public class ThisConstructorDelegation extends ConstructorDelegation<ThisConstructorDelegation> {

  public ThisConstructorDelegation(){
    super(null);
  }

  protected String getName() {
    return "this";
  }

  protected Type actualType() throws LookupException {
    return language(ObjectOrientedLanguage.class).voidType();
  }

//  public NormalMethod getMethod() throws LookupException {
//	   return nearestAncestor(Type.class).lexicalLookupStrategy().lookUp(selector());
//  }

  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
		DeclarationCollector<X> collector = new DeclarationCollector<X>(selector);
	  nearestAncestor(ClassBody.class).lexicalLookupStrategy().lookUp(collector);
	  return collector.result();
//	   if(result != null) {
//		   return result;
//	   } else {
//	  	 nearestAncestor(ClassBody.class).lexicalLookupStrategy().lookUp(selector);
//	  	 throw new LookupException("Cannot find the target of a this constructor delegation.");
//	   }
  }

  protected ThisConstructorDelegation cloneInvocation(CrossReferenceTarget target) {
    return new ThisConstructorDelegation();
  }

}
