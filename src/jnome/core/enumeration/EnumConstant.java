package jnome.core.enumeration;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.language.Language;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LocalLookupStrategy;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.expression.Expression;
import chameleon.oo.member.FixedSignatureMember;
import chameleon.oo.member.Member;
import chameleon.oo.type.ClassBody;
import chameleon.oo.type.DeclarationWithType;
import chameleon.oo.type.Type;
import chameleon.util.association.Multi;
import chameleon.util.association.Single;

public class EnumConstant extends FixedSignatureMember implements DeclarationWithType, DeclarationContainer {

	public EnumConstant(SimpleNameSignature signature) {
		super(signature);
	}
	
	@Override
	public EnumConstant clone() {
		EnumConstant result = new EnumConstant((SimpleNameSignature) signature().clone());
		result.setBody(body().clone());
		for(Expression arg: actualArguments()) {
			result.addParameter(arg.clone());
		}
		return result;
	}

	public List<Member> getIntroducedMembers() {
		List<Member> result = new ArrayList<Member>();
		result.add(this);
		return result;
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
  
  public ClassBody body() {
  	return _body.getOtherEnd();
  }
  
  public void setBody(ClassBody body) {
  	set(_body,body);
//  	if(body == null) {
//  		_body.connectTo(null);
//  	} else {
//  		_body.connectTo(body.parentLink());
//  	}
  }
  
  private Single<ClassBody> _body = new Single<ClassBody>(this);

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	@Override
	public Class<SimpleNameSignature> signatureType() {
		return SimpleNameSignature.class;
	}

	public Type declarationType() throws LookupException {
		return nearestAncestor(Type.class);
	}

	public LocalLookupStrategy<?> targetContext() throws LookupException {
  	Language language = language();
  	if(language != null) {
		  return language.lookupFactory().createTargetLookupStrategy(this);
  	} else {
  		throw new LookupException("Element of type "+getClass().getName()+" is not connected to a language. Cannot retrieve target context.");
  	}
	}

	public List<? extends Declaration> locallyDeclaredDeclarations() throws LookupException {
		return body().declarations();
	}

	public List<? extends Declaration> declarations() throws LookupException {
		List<Declaration> result = new ArrayList<Declaration>();
		result.addAll(body().declarations());
		result.addAll(nearestAncestor(Type.class).declarations());
		return result;
	}

	public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}

	public Declaration declarator() {
		return this;
	}

	public void setName(String name) {
		setSignature(new SimpleNameSignature(name));
	}

	@Override
	public boolean complete() throws LookupException {
		return true;
	}
	
	@Override
	public LookupStrategy localStrategy() throws LookupException {
		return language().lookupFactory().createLocalLookupStrategy(this);
	}
}
