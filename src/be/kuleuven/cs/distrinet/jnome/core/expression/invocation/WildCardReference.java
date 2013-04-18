/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.ElementImpl;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

public abstract class WildCardReference<E extends WildCardReference> extends ElementImpl implements JavaTypeReference {
	
		public abstract E clone();
	
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

		public Type getType() throws LookupException {
			return getElement();
		}

		public TypeReference intersection(TypeReference other) {
			return other.intersectionDoubleDispatch(this);
		}

		public TypeReference intersectionDoubleDispatch(TypeReference other) {
			return language(ObjectOrientedLanguage.class).createIntersectionReference(clone(), other.clone());
		}

		public TypeReference intersectionDoubleDispatch(IntersectionTypeReference other) {
			IntersectionTypeReference result = other.clone();
			result.add(clone());
			return result;
		}

		public Declaration getDeclarator() throws LookupException {
			return getElement();
		}

		public LookupContext targetContext() throws LookupException {
			return getType().targetContext();
		}
}
