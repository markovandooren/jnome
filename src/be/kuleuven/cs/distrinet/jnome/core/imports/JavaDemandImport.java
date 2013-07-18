package be.kuleuven.cs.distrinet.jnome.core.imports;

import java.util.Iterator;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.DemandImport;
import be.kuleuven.cs.distrinet.chameleon.core.reference.SimpleReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;

public class JavaDemandImport extends DemandImport {

	public JavaDemandImport(SimpleReference<? extends DeclarationContainer> ref) {
		super(ref);
	}
	
	/**
	 * A regular demand import in Java only imports types.
	 */
  @Override
  protected void filterImportedElements(List<Declaration> declarations) throws LookupException {
//		new TypePredicate(Type.class).filter(declarations);
		Iterator<Declaration> iterator = declarations.iterator();
		while(iterator.hasNext()) {
			Declaration next = iterator.next();
			if(! (next instanceof Type)) {
				iterator.remove();
			}
		}
  }
  
  @Override
  protected DemandImport cloneSelf() {
  	return new JavaDemandImport(null);
  }
}
