/**
 * 
 */
package org.aikodi.java.core.expression.invocation;

import java.util.HashSet;
import java.util.Set;

import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.association.Single;
import org.aikodi.java.core.type.JavaTypeReference;

public abstract class WildCardReference extends ElementImpl implements JavaTypeReference {
	
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

		@Override
		public String toString() {
			return toString(new HashSet<>());
		}

}
