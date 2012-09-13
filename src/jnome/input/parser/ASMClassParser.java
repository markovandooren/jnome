package jnome.input.parser;

import jnome.core.language.Java;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.PureWildcard;

import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.rejuse.association.SingleAssociation;

import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.ExtendsWildcard;
import chameleon.oo.type.generics.SuperWildcard;

public class ASMClassParser {

	public ASMClassParser(Java lang) {
		_lang = lang;
	}
	
	public Java language() {
		return _lang;
	}
	
	private Java _lang;
	
	protected TypeReference toRef(String tref) {
		SignatureExtractor signatureExtractor = new SignatureExtractor(language());
		new SignatureReader(tref).accept(signatureExtractor);
		return signatureExtractor.typeReference();
	}
	
	
	
	protected static class SignatureExtractor extends SignatureVisitor {

		public SignatureExtractor(Java lang) {
			super(4);
			_lang = lang;
		}
		
		private Java _lang;
		
		public Java language() {
			return _lang;
		}
		
		private JavaTypeReference _tref;
		
		public JavaTypeReference typeReference() {
			return _tref;
		}
		
		@Override
		public void visitBaseType(char t) {
			String fqn;
			if(t == 'V') {
				fqn = "void";
			} else {
				throw new ChameleonProgrammerException();
			}
			_tref = language().createTypeReference(fqn);
			connect(_tref);
		}
		
		@Override
		public void visitClassType(String fqn) {
			_tref = language().createTypeReference(fqn);
			connect(_tref);
		}
		
		protected void connect(TypeReference tref) {
			// do nothing by default;
		}
		
		@Override
		public SignatureVisitor visitArrayType() {
			return new SignatureExtractor(language()) {
				@Override
				protected void connect(TypeReference tref) {
					_tref = new ArrayTypeReference((JavaTypeReference) tref);
					SignatureExtractor.this.connect(_tref);
				}
			};
		}
		
		@Override
		public SignatureVisitor visitTypeArgument(char kind) {
			// create visitor with 'this' as its parent.
			final ActualTypeArgumentWithTypeReference arg;
			if(kind == SignatureVisitor.INSTANCEOF) {
				arg = new BasicTypeArgument(null);
			} else if(kind == SignatureVisitor.EXTENDS) {
				arg = new ExtendsWildcard(null);
			} else if(kind == SignatureVisitor.SUPER) {
				arg = new SuperWildcard(null);
			} else {
				throw new ChameleonProgrammerException();
			}
			((BasicJavaTypeReference)typeReference()).addArgument(arg);
			return new SignatureExtractor(language()) {
				/**
				 * If there is no bound, we replace the type argument with a pure wildcard.
				 * I GUESS that * is modeled as an EXTENDS bound without a type reference.
				 */
				@Override
				public void visitEnd() {
					if(this.typeReference() == null) {
						SingleAssociation parentLink = arg.parentLink();
						parentLink.getOtherRelation().replace(parentLink, new PureWildcard().parentLink());
					}
				}
				protected void connect(TypeReference tref) {
					arg.setTypeReference(tref);
				};
			};
		}
		
	}
	
	
}
