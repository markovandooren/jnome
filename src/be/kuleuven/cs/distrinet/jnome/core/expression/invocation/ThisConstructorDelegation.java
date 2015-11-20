package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.lookup.DeclarationCollector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.ClassBody;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
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

  @Override
  public NormalMethod getElement() throws LookupException {
		DeclarationCollector<NormalMethod> collector = new DeclarationCollector<NormalMethod>(selector());
	  nearestAncestor(ClassBody.class).lexicalContext().lookUp(collector);
	  return collector.result();
  }

  protected ThisConstructorDelegation cloneSelf() {
    return new ThisConstructorDelegation();
  }

}
