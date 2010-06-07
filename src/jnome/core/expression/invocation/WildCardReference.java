/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import jnome.core.type.JavaTypeReference;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public abstract class WildCardReference<E extends WildCardReference> extends NamespaceElementImpl<E,Element> implements JavaTypeReference<E> {
	
		public WildCardReference(TypeReference tref) {
			_tref.connectTo(tref.parentLink());
		}

		public TypeReference typeReference() {
			return _tref.getOtherEnd();
		}
		
		private SingleAssociation<WildCardReference, TypeReference> _tref = new SingleAssociation<WildCardReference, TypeReference>(this); 
		
		@Override
		public VerificationResult verifySelf() {
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

		public TypeReference intersectionDoubleDispatch(IntersectionTypeReference<?> other) {
			IntersectionTypeReference<?> result = other.clone();
			result.add(clone());
			return result;
		}

		public Declaration getDeclarator() throws LookupException {
			return getElement();
		}

		public List<? extends Element> children() {
			return Util.createNonNullList(typeReference());
		}
}