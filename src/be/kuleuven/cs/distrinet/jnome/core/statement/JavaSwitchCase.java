package be.kuleuven.cs.distrinet.jnome.core.statement;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchCase;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchLabel;
import be.kuleuven.cs.distrinet.chameleon.support.statement.SwitchStatement;
import be.kuleuven.cs.distrinet.jnome.core.enumeration.EnumType;

public class JavaSwitchCase extends SwitchCase {

	protected JavaSwitchCase() {
	}
	
  public JavaSwitchCase(SwitchLabel label) {
  	super(label);
  }


  /**
   * JLS 6.3: The scope of an enum constant declared in an enum with name E is the
   *          body of E and any switch label of a switch statement with an expression
   *          of type E.
   */
	@Override
	public LookupContext lookupContext(Element child) throws LookupException {
		if(getLabel() == child) {
			SwitchStatement switchStatement = nearestAncestor(SwitchStatement.class);
			Type type = switchStatement.getExpression().getType();
			if(type instanceof EnumType) {
				return type.localContext();
			}
		}
		return lexicalContext();
	}


}
