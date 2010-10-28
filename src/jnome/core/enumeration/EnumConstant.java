package jnome.core.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.language.Language;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.member.FixedSignatureMember;
import chameleon.core.member.Member;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.ClassBody;
import chameleon.oo.type.DeclarationWithType;
import chameleon.oo.type.Type;
import chameleon.util.Util;

public class EnumConstant extends FixedSignatureMember<EnumConstant,Type,SimpleNameSignature,EnumConstant> implements DeclarationWithType<EnumConstant,Type,SimpleNameSignature,EnumConstant>, DeclarationContainer<EnumConstant, Type>{

	public EnumConstant(SimpleNameSignature signature) {
		super(signature);
	}
	
	@Override
	public EnumConstant clone() {
		EnumConstant result = new EnumConstant(signature().clone());
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

	public List<Element> children() {
    List<Element> result = super.children();
    result.addAll(actualArguments());
    Util.addNonNull(body(), result);
    return result;
	}

	/**
	 * ACTUAL PARAMETERS
	 */
 private OrderedMultiAssociation<EnumConstant,Expression> _parameters = new OrderedMultiAssociation<EnumConstant,Expression>(this);
 
  public void addParameter(Expression parameter) {
  	setAsParent(_parameters, parameter);
  }
  
  public void addAllParameters(List<Expression> parameters) {
  	for(Expression param: parameters) {
  		addParameter(param);
  	}
  }

  public void removeParameter(Expression parameter) {
  	_parameters.remove(parameter.parentLink());
  }

  public List<Expression> actualArguments() {
    return _parameters.getOtherEnds();
  }
  
  public ClassBody body() {
  	return _body.getOtherEnd();
  }
  
  public void setBody(ClassBody body) {
  	setAsParent(_body,body);
//  	if(body == null) {
//  		_body.connectTo(null);
//  	} else {
//  		_body.connectTo(body.parentLink());
//  	}
  }
  
  private SingleAssociation<EnumConstant,ClassBody> _body = new SingleAssociation<EnumConstant, ClassBody>(this);

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

	public LookupStrategy targetContext() throws LookupException {
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

}
