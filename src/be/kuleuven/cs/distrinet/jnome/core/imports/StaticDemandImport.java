package be.kuleuven.cs.distrinet.jnome.core.imports;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.DemandImport;
import be.kuleuven.cs.distrinet.chameleon.core.reference.ElementReference;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.rejuse.predicate.UnsafePredicate;

public class StaticDemandImport extends DemandImport {

	public StaticDemandImport(ElementReference<? extends DeclarationContainer> ref) {
		super(ref);
	}
	
	@Override
	protected void filterImportedElements(List<Declaration> declarations) throws LookupException {
		new UnsafePredicate<Declaration, LookupException>() {

			@Override
			public boolean eval(Declaration object) throws LookupException {
				return object.isTrue(language(ObjectOrientedLanguage.class).CLASS);
			}
		}.filter(declarations);
	}

}
