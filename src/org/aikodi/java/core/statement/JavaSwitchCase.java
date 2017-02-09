package org.aikodi.java.core.statement;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.statement.SwitchCase;
import org.aikodi.chameleon.support.statement.SwitchLabel;
import org.aikodi.chameleon.support.statement.SwitchStatement;
import org.aikodi.java.core.enumeration.EnumType;

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
