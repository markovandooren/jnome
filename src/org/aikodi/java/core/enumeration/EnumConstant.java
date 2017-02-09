package org.aikodi.java.core.enumeration;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LocalLookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.member.SimpleNameMember;
import org.aikodi.chameleon.oo.type.ClassBody;
import org.aikodi.chameleon.oo.type.ClassWithBody;
import org.aikodi.chameleon.oo.type.DeclarationWithType;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.util.association.Multi;
import org.aikodi.chameleon.util.association.Single;

public class EnumConstant extends SimpleNameMember implements DeclarationWithType {

	public EnumConstant(String name) {
		setName(name);
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
  	for(Expression param: parameters) {
  		addParameter(param);
  	}
  }

  public void removeParameter(Expression parameter) {
  	remove(_parameters,parameter);
  }

  public List<Expression> actualArguments() {
    return _parameters.getOtherEnds();
  }
  
//  public ClassBody body() {
//  	return _body.getOtherEnd();
//  }
//  
//  public void setBody(ClassBody body) {
//  	set(_body,body);
//  }
//  
//  private Single<ClassBody> _body = new Single<ClassBody>(this);

	private Single<ClassWithBody> _anonymousType = new Single<ClassWithBody>(this);
	
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


	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

	public Type declarationType() throws LookupException {
		return nearestAncestor(Type.class);
	}

//	public LocalLookupContext<?> targetContext() throws LookupException {
//  	Language language = language();
//  	if(language != null) {
//		  return language.lookupFactory().createTargetLookupStrategy(this);
//  	} else {
//  		throw new LookupException("Element of type "+getClass().getName()+" is not connected to a language. Cannot retrieve target context.");
//  	}
//	}

//	public List<? extends Declaration> locallyDeclaredDeclarations() throws LookupException {
//		return body().declarations();
//	}
//
//	public List<? extends Declaration> declarations() throws LookupException {
//		List<Declaration> result = new ArrayList<Declaration>();
//		result.addAll(body().declarations());
//		result.addAll(nearestAncestor(Type.class).declarations());
//		return result;
//	}

//	public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
//		return selector.selection(declarations());
//	}

	public Declaration declarator() {
		return this;
	}

	@Override
	public boolean complete() throws LookupException {
		return true;
	}
	
	@Override
	public LocalLookupContext<?> targetContext() throws LookupException {
		Type type = getAnonymousType();
		if(type == null) {
		  return nearestAncestor(EnumType.class).targetContext();
		}
    return type.targetContext();
	}
	
//	@Override
//	public LookupContext localContext() throws LookupException {
//		return language().lookupFactory().createLocalLookupStrategy(this);
//	}
}
