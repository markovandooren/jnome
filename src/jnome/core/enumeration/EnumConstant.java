package jnome.core.enumeration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.Reference;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.expression.ActualArgumentList;
import chameleon.core.expression.ActualParameter;
import chameleon.core.expression.Invocation;
import chameleon.core.member.FixedSignatureMember;
import chameleon.core.member.Member;
import chameleon.core.type.Type;
import chameleon.util.Util;

public class EnumConstant extends FixedSignatureMember<EnumConstant,Type,SimpleNameSignature,EnumConstant> {

	public EnumConstant(SimpleNameSignature signature) {
		super(signature);
	}
	
	@Override
	public EnumConstant clone() {
		EnumConstant result = new EnumConstant(signature().clone());
		return result;
	}

	public Set<Member> getIntroducedMembers() {
		Set result = new HashSet();
		result.add(this);
		return result;
	}

	public Type getNearestType() {
		return parent().getNearestType();
	}

	public List<Element> children() {
    List<Element> result = new ArrayList<Element>();
    result.add(actualArgumentList());
    return result;
	}

	/**
	 * ACTUAL PARAMETERS
	 */
 private Reference<EnumConstant,ActualArgumentList> _parameters = new Reference<EnumConstant,ActualArgumentList>(this);
 
 public ActualArgumentList actualArgumentList() {
	 return _parameters.getOtherEnd();
 }

  public void addParameter(ActualParameter parameter) {
  	actualArgumentList().addParameter(parameter);
  }

  public void removeParameter(ActualParameter parameter) {
  	actualArgumentList().removeParameter(parameter);
  }

  public List<ActualParameter> getActualParameters() {
    return actualArgumentList().getActualParameters();
  }

}