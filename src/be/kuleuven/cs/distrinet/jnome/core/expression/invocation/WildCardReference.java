/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.IntersectionTypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

public abstract class WildCardReference<E extends WildCardReference> extends ElementImpl implements JavaTypeReference {
	
		public WildCardReference(TypeReference tref) {
			set(_tref,tref);
		}

		public TypeReference typeReference() {
			return _tref.getOtherEnd();
		}
		
		private Single<TypeReference> _tref = new Single<TypeReference>(this); 
		
		@Override
		public Verification verifySelf() {
			return Valid.create();
		}

		public JavaTypeReference componentTypeReference() {
			return this;
		}

		public JavaTypeReference erasedReference() {
			return this;
		}

		public JavaTypeReference toArray(int dimension) {
			throw new ChameleonProgrammerException();
		}

		public Declaration getDeclarator() throws LookupException {
			return getElement();
		}

		public LookupContext targetContext() throws LookupException {
			return getType().targetContext();
		}
}
