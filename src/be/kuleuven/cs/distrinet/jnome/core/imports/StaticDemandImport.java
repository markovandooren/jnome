package be.kuleuven.cs.distrinet.jnome.core.imports;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespacedeclaration.DemandImport;
import org.aikodi.chameleon.core.reference.NameReference;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;

import be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;

public class StaticDemandImport extends DemandImport {

	public StaticDemandImport(NameReference<? extends DeclarationContainer> ref) {
		super(ref);
	}
	
	@Override
	protected void filterImportedElements(List<Declaration> declarations) throws LookupException {
	  CollectionOperations.filter(declarations, d-> d.isTrue(language(ObjectOrientedLanguage.class).CLASS));
	}

}
