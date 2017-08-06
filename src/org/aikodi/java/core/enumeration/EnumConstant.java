package org.aikodi.java.core.enumeration;

import java.util.List;

import org.aikodi.chameleon.core.declaration.BasicDeclaration;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.Name;
import org.aikodi.chameleon.core.lookup.LocalLookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.type.ClassBody;
import org.aikodi.chameleon.oo.type.ClassWithBody;
import org.aikodi.chameleon.oo.type.DeclarationWithType;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.util.association.Multi;
import org.aikodi.chameleon.util.association.Single;
import org.aikodi.rejuse.contract.Contracts;

public class EnumConstant extends BasicDeclaration implements DeclarationWithType {

	public EnumConstant(String name) {
		setSignature(new Name(name));
	}
	
	@Override
	public EnumConstant cloneSelf() {
		return new EnumConstant(name());
	}

	public List<Declaration> getIntroducedMembers() {
		return Util.createNonNullList(this);
	}

	/**
	 * ACTUAL PARAMETERS
	 */
 private Multi<Expression> _parameters = new Multi<Expression>(this);
 
  public void addParameter(Expression parameter) {
  	add(_parameters, parameter);
  }
  
  public void addAllParameters(List<Expression> parameters) {
  	Contracts.notNull(parameters, "The list of parameters cannot be null.");
  	parameters.forEach(p -> addParameter(p));
  }

  public void removeParameter(Expression parameter) {
  	remove(_parameters,parameter);
  }

  public List<Expression> actualArguments() {
    return _parameters.getOtherEnds();
  }
  
	private Single<ClassWithBody> _anonymousType = new Single<ClassWithBody>(this, "type");
	
	public void setBody(ClassBody body) {
		if(body == null) {
			_anonymousType.connectTo(null);
		} else {
			set(_anonymousType,createAnonymousType(body));
		}
	}
	
	public ClassWithBody getAnonymousType() {
		return _anonymousType.getOtherEnd();
	}
	
	private void setAnonymousType(ClassWithBody type) {
		set(_anonymousType, type);
	}

  private ClassWithBody createAnonymousType(ClassBody body) {
  	RegularType anon = new EnumConstantType();
	  anon.setBody(body);
		return anon;
	}
  
  public ClassBody body() {
  	ClassWithBody anonymousType = getAnonymousType();
  	if(anonymousType != null) {
  		return anonymousType.body();
  	} else {
  		return null;
  	}
  }

	public Type declarationType() throws LookupException {
		return lexical().nearestAncestor(Type.class);
	}

	@Override
	public LocalLookupContext<?> targetContext() throws LookupException {
		Type type = getAnonymousType();
		if(type == null) {
		  return lexical().nearestAncestor(EnumType.class).targetContext();
		}
    return type.targetContext();
	}
	
}
