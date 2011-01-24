package jnome.core.imports;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.SelectorWithoutOrder;
import chameleon.core.lookup.TwoPhaseDeclarationSelector;
import chameleon.core.member.Member;
import chameleon.core.namespacepart.Import;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public class SingleStaticImport extends Import<SingleStaticImport> {

	public SingleStaticImport(TypeReference tref, String name) {
		setTypeReference(tref);
		_name = name;
	}
	
	private String _name;
	
	public String name() {
		return _name;
	}
	
	@Override
	public SingleStaticImport clone() {
		return new SingleStaticImport(typeReference().clone(), name());
	}

	@Override
	public List<Declaration> demandImports() throws LookupException {
		return new ArrayList<Declaration>();
	}

	@Override
	public <D extends Declaration> List<D> demandImports(DeclarationSelector<D> selector) throws LookupException {
		return new ArrayList<D>();
	}

	@Override
	public List<Declaration> directImports() throws LookupException {
		return (List)members();
	}

	@Override
	public <D extends Declaration> List<D> directImports(DeclarationSelector<D> selector) throws LookupException {
		Type type = typeReference().getElement();
		return selector.selection(type.declarations());
	}
	
	public List<Member> members() throws LookupException {
		Type type = typeReference().getElement();
		return selector().selection(type.declarations());
	}
	
	public DeclarationSelector<Member> selector() {
		return new TwoPhaseDeclarationSelector<Member>() {

			@Override
			public WeakPartialOrder<Member> order() {
				return new SelectorWithoutOrder.EqualityOrder<Member>();
			}

			@Override
			public boolean selectedBasedOnName(Signature signature) throws LookupException {
				return signature.name().equals(name());
			}

			@Override
			public Class<Member> selectedClass() {
				return Member.class;
			}

			@Override
			public boolean selectedRegardlessOfName(Member declaration) throws LookupException {
				ObjectOrientedLanguage language = (ObjectOrientedLanguage) declaration.language(ObjectOrientedLanguage.class);
				return declaration.is(language.CLASS) == Ternary.TRUE;
			}

			@Override
			public String selectionName(DeclarationContainer<?,?> container) throws LookupException {
				return name();
			}
			
		};
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

  public List<Element> children() {
    return Util.createNonNullList(typeReference());
  }

	private SingleAssociation<SingleStaticImport, TypeReference> _typeReference = new SingleAssociation<SingleStaticImport, TypeReference>(this);

  
  public TypeReference typeReference() {
    return (TypeReference)_typeReference.getOtherEnd();
  }

  public void setTypeReference(TypeReference reference) {
  	if(reference != null) {
  		_typeReference.connectTo(reference.parentLink());
  	}
  	else {
  		_typeReference.connectTo(null);
  	}
  }

}
