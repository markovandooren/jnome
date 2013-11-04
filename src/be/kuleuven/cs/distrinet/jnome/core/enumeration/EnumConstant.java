package be.kuleuven.cs.distrinet.jnome.core.enumeration;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.member.SimpleNameMember;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ClassBody;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ClassWithBody;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DeclarationWithType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.util.association.Multi;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

public class EnumConstant extends SimpleNameMember implements DeclarationWithType, SimpleNameDeclaration {

	public EnumConstant(String name) {
		setName(name);
	}
	
	@Override
	public EnumConstant cloneSelf() {
		return new EnumConstant(name());
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
	public LookupContext targetContext() throws LookupException {
		return getAnonymousType().targetContext();
	}
	
//	@Override
//	public LookupContext localContext() throws LookupException {
//		return language().lookupFactory().createLocalLookupStrategy(this);
//	}
}
