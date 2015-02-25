package be.kuleuven.cs.distrinet.jnome.core.expression;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.IntersectionType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.support.expression.ConditionalExpression;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaDerivedType;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

import com.google.common.collect.ImmutableList;

public class JavaConditionalExpression extends ConditionalExpression {

	public JavaConditionalExpression(Expression condition, Expression first, Expression second) {
		super(condition, first, second);
	}

	@Override
	protected ConditionalExpression cloneSelf() {
		return new JavaConditionalExpression(null,null,null);
	}
	
	@Override
	protected Type actualType() throws LookupException {
		Type result = basicType();
		if(result == null) {
			Type first = getFirst().getType();
			Type second = getSecond().getType();
			Java java = language(Java.class);
			JavaView view = view(JavaView.class);
			boolean firstPrimitive = first.isTrue(java.VALUE_TYPE);
			boolean secondPrimitive = second.isTrue(java.VALUE_TYPE);
			Type boxFirst = java.box(first);
			Type boxSecond = java.box(second);
			if(firstPrimitive || secondPrimitive) {
				if(firstPrimitive && second.sameAs(boxFirst)) {
					result = first;
				} else {
					if(secondPrimitive && first.sameAs(boxSecond)) {
						result = second;
					}
				}
			}
			if(result == null) {
				if(java.convertibleToNumeric(first) && java.convertibleToNumeric(second)) {
					Namespace root = namespace().defaultNamespace();
					Type tByte = java.findType("java.lang.Byte", root);
					Type tbyte = view.primitiveType("byte");
					Type tShort = java.findType("java.lang.Short", root);
					Type tshort = view.primitiveType("short");
					Type tChar = java.findType("java.lang.Char", root);
					Type tchar = view.primitiveType("char");
					boolean firstConstInt = false;
					boolean secondConstInt = false;
					if((boxFirst.sameAs(tByte) && boxSecond.sameAs(tShort)) ||
							(boxSecond.sameAs(tByte) && boxFirst.sameAs(tShort)) ){
						result = tshort;
						
					} else if((first.sameAs(tchar) || first.sameAs(tshort) || first.sameAs(tbyte)) && secondConstInt) {
						// return first if the const int value of 2nd fits
						throw new LookupException("Constant expressions not supported yet.");
					} else if((second.sameAs(tchar) || second.sameAs(tshort) || second.sameAs(tbyte)) && firstConstInt) {
						// return second if the const int value of 1st fits 
						throw new LookupException("Constant expressions not supported yet.");
					} else if((first.sameAs(tChar) || first.sameAs(tShort) || first.sameAs(tByte)) && secondConstInt){
						// return unbox(first) if the const int value of 2nd fits
						throw new LookupException("Constant expressions not supported yet.");
					} else if((second.sameAs(tChar) || second.sameAs(tShort) || second.sameAs(tByte)) && firstConstInt) {
						// return unbox(second) if the const int value of 1st fits 
						throw new LookupException("Constant expressions not supported yet.");
					} else {
						result = java.binaryNumericPromotion(first, second);
					}
				}
				if(result == null) {
					result = java.subtypeRelation().leastUpperBound(ImmutableList.of(java.reference(boxFirst), java.reference(boxSecond)));
//					result = IntersectionType.create(ImmutableList.of(boxFirst, boxSecond));
					if(result instanceof JavaDerivedType) {
						result = ((JavaDerivedType)result).captureConversion();
					}
				}
			} 
		}
		
		return result;
		
//		else {
//  		ObjectOrientedLanguage language = language(ObjectOrientedLanguage.class);
//  		if (firstType.isTrue(language.REFERENCE_TYPE) && secondType.isTrue(language.REFERENCE_TYPE)) {
//  			TypeReference first = language.reference(firstType);
//  			TypeReference second = language.reference(secondType);
//  			return language.subtypeRelation().leastUpperBound(ImmutableList.of(first,second));
//  		}
//  	}
	}
	
	
}
