package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.DeclarationCollector;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.ClassBody;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.workspace.View;

/**
 * @author Marko van Dooren
 */
public class ThisConstructorDelegation extends ConstructorDelegation {

  public ThisConstructorDelegation(){
    super(null);
  }

  protected String getName() {
    return "this";
  }

  protected Type actualType() throws LookupException {
    View view = view();
		return view.language(ObjectOrientedLanguage.class).voidType(view.namespace());
  }

//  public NormalMethod getMethod() throws LookupException {
//	   return nearestAncestor(Type.class).lookupContext().lookUp(selector());
//  }

  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
		DeclarationCollector<X> collector = new DeclarationCollector<X>(selector);
	  nearestAncestor(ClassBody.class).lexicalContext().lookUp(collector);
	  return collector.result();
//	   if(result != null) {
//		   return result;
//	   } else {
//	  	 nearestAncestor(ClassBody.class).lookupContext().lookUp(selector);
//	  	 throw new LookupException("Cannot find the target of a this constructor delegation.");
//	   }
  }

  protected ThisConstructorDelegation cloneSelf() {
    return new ThisConstructorDelegation();
  }

}
