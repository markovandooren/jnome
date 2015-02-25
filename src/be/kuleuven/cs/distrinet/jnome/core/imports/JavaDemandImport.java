package be.kuleuven.cs.distrinet.jnome.core.imports;

import java.util.Iterator;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespacedeclaration.DemandImport;
import org.aikodi.chameleon.core.reference.NameReference;
import org.aikodi.chameleon.oo.type.Type;

public class JavaDemandImport extends DemandImport {

	public JavaDemandImport(NameReference<? extends DeclarationContainer> ref) {
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
