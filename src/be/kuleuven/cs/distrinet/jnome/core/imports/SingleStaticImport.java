package be.kuleuven.cs.distrinet.jnome.core.imports;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.declaration.Signature;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.core.lookup.TwoPhaseDeclarationSelector;
import org.aikodi.chameleon.core.namespacedeclaration.Import;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

public class SingleStaticImport extends Import {

	public SingleStaticImport(TypeReference tref, String name) {
		setTypeReference(tref);
		_name = name;
	}
	
	private String _name;
	
	public String name() {
		return _name;
	}
	
	@Override
	protected SingleStaticImport cloneSelf() {
		return new SingleStaticImport(null, name());
	}

	@Override
	public List<Declaration> demandImports() throws LookupException {
		return new ArrayList<Declaration>();
	}

	@Override
	public <D extends Declaration> List<? extends SelectionResult> demandImports(DeclarationSelector<D> selector) throws LookupException {
		return new ArrayList<SelectionResult>();
	}

	@Override
	public List<Declaration> directImports() throws LookupException {
		return (List)members();
	}

	@Override
	public <D extends Declaration> List<? extends SelectionResult> directImports(DeclarationSelector<D> selector) throws LookupException {
		Type type = typeReference().getElement();
		return selector.selection(type.declarations());
	}
	
	public List<? extends SelectionResult> members() throws LookupException {
		Type type = typeReference().getElement();
		return selector().selection(type.declarations());
	}
	
	public DeclarationSelector<Member> selector() {
		return new TwoPhaseDeclarationSelector<Member>() {

//			@Override
//			public WeakPartialOrder<Member> order() {
//				return new SelectorWithoutOrder.EqualityOrder<Member>();
//			}
			
			@Override
			protected void applyOrder(List<SelectionResult<Member>> tmp) throws LookupException {
				
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
			public String selectionName(DeclarationContainer container) throws LookupException {
				return name();
			}
			
		};
	}

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

	private Single<TypeReference> _typeReference = new Single<TypeReference>(this);

  
  public TypeReference typeReference() {
    return _typeReference.getOtherEnd();
  }

  public void setTypeReference(TypeReference reference) {
  	set(_typeReference,reference);
  }

}
