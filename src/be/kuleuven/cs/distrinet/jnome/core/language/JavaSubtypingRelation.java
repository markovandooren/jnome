/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.language;

import static be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations.exists;
import static be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations.forAll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.SubtypeRelation;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.IntersectionType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.TypeIndirection;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeArgumentWithTypeReference;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;
import org.aikodi.chameleon.oo.type.generics.InstantiatedParameterType;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;

import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.NonLocalJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaIntersectionTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.NullType;
import be.kuleuven.cs.distrinet.jnome.core.type.PureWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.RawType;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class JavaSubtypingRelation extends SubtypeRelation {

	public JavaSubtypingRelation(Java7 java) {
		_java = java;
	}

	private Java7 _java;

	public Java7 java() {
		return _java;
	}

	public static class CaptureReference extends NonLocalJavaTypeReference {

		public CaptureReference(JavaTypeReference tref) {
			super(tref,null);
		}

		@Override
		public Element lookupParent() {
			return nearestAncestor(TypeParameter.class);
		}

		@Override
		protected CaptureReference cloneSelf() {
			return new CaptureReference(null);
		}

	}

	/**
	 * <p>
	 * Compute the least upper bound according to Section 4.10.4 of the Java
	 * Language Specification version 8. The given binder is used.
	 * </p>
	 * 
	 * <ol>
	 * <li>If the list contains only a single type reference, then the result is
	 * the type referenced by that type reference.</li>
	 * <li>Otherwise
	 * <ul>
	 * <li>Let {@link #ST(JavaTypeReference)} of <code>Us.get(i)</code> be the set
	 * of super types of <code>Us.get(i)</code>.</li>
	 * <li>Let {@link #EST(JavaTypeReference)} of <code>Us.get(i)</code> be the
	 * set of erased super types of <code>Us.get(i)</code></li>
	 * </ul>
	 * </li>
	 * </ol>
	 * 
	 * @param Us
	 * @param root
	 * @return
	 * @throws LookupException
	 */
	private Type leastUpperBound(List<? extends TypeReference> Us, Binder root) throws LookupException {
		if(Us.size() == 1) {
			return Us.get(0).getElement();
		}
		List<Type> MEC = new ArrayList<Type>(MEC((List<? extends JavaTypeReference>) Us));
		List<Type> candidates = new ArrayList<Type>(MEC.size());
		int size = MEC.size();
		for(int i=0; i<size;i++) {
			Type W = MEC.get(i);
			candidates.add(Best(W,(List<? extends JavaTypeReference>) Us,root));
		}
		return intersection(candidates);
	}

	/**
	 * <p>Compute the least upper bound according to Section 4.10.4 of the Java Language Specification
	 * version 8.</p> 
	 * <p>The names of the parameters in this class are chosen to match the names
	 * used in the Java Language Specification.</p>
	 */
	@Override
	public Type leastUpperBound(List<? extends TypeReference> Us) throws LookupException {
		return leastUpperBound(Us, null);
	}

	private Set<Type> MEC(List<? extends JavaTypeReference> Us) throws LookupException {
		Set<Type> EC = EC(Us);
		Predicate<Type, LookupException> predicate = first -> ! exists(EC, second -> (! first.sameAs(second)) && (second.subtypeOf(first)));
		CollectionOperations.filter(EC, predicate);
		return EC;
	}

	private Set<Type> EC(List<? extends JavaTypeReference> Us) throws LookupException {
		Set<Type> result = EST(Us.get(0));
		// At this point, usually is immutable.
		int size = Us.size();
		if(size > 1) {
			// only copy if we have to
			result = new HashSet<>(result);
			for(int i = 1; i< size; i++) {
				// no need to copy the usually immutable sets since we use them only
				// to remove values from result.
				Set<Type> tmp = new HashSet<>();
				final Set<Type> est = EST(Us.get(i));
				for(Type r: result) {
					for(Type e: est) {
						boolean add = r.sameAs(e);
						if(! add) {
							// TODO get rid of dependency on the bounds
							add = e.upperBound().subtypeOf(r.upperBound()) && 
									e.lowerBound().subtypeOf(r.lowerBound()) && 
									r.upperBound().subtypeOf(e.upperBound()) && 
									r.lowerBound().subtypeOf(e.lowerBound());
						}
						if(add) {
							tmp.add(e);
						}
					}
				}
				result = tmp;
				//        result.retainAll(est);
			}
		}
		return result;
	}

	/**
	 * <p>Compute the set of erased super type of the type referenced by the given type
	 * reference.</p>
	 * 
	 * <p>The set of erased super types of a type U is the set of types |W| where
	 * W is in {@link #ST(JavaTypeReference)} of U and |W| is the {@link JavaType#erasure()}
	 * of W.</p>
	 * 
	 * @param U A type reference that refers to the type of which the set of erased
	 *          super types is requested. The type reference cannot be null. 
	 * @return
	 * @throws LookupException
	 */
	protected Set<Type> EST(JavaTypeReference U) throws LookupException {
		//FIXME This is wrong
		return ((JavaType)U.getElement()).erasure().getSelfAndAllSuperTypesView();
	}

	private Type intersection(List<Type> candidates)
			throws LookupException {
		if(candidates.isEmpty()) {
			throw new LookupException("No candidates for the inferred type");
		}  else {
			return java().plugin(ObjectOrientedFactory.class).createIntersectionType(candidates);
		}
	}



	protected Set<Type> ST(JavaTypeReference U) throws LookupException {
		return U.getElement().getSelfAndAllSuperTypesView();
	}

	private Type Candidate(Type G, List<? extends JavaTypeReference> Us, Binder root) throws LookupException {
		return lcp(relevant(G,Us),root);
	}

	private Type Best(Type W, List<? extends JavaTypeReference> Us, Binder root) throws LookupException {
		if(W.parameters(TypeParameter.class).size() > 0) {
			//Relevant
			return Candidate(W, Us,root);
		} else {
			return W;
		}
	}

	private List<Type> relevant(Type G, List<? extends JavaTypeReference> Us) throws LookupException {
		List<Type> result = new ArrayList<Type>();
		for(JavaTypeReference U: Us) {
			result.add(relevant(G, U));
		}
		return result;
	}

	private Type relevant(Type G, JavaTypeReference U) throws LookupException {
		return U.getElement().superTypeJudge().get(G);
	}

	private Type lcp(List<Type> types, Binder root) throws LookupException {
		int size = types.size();
		if(size > 0) {
			Type lci = types.get(0);
			for(int i = 1; i < size; i++) {
				lci = lci(lci, types.get(i),root);
			}
			return lci;
		} else {
			throw new ChameleonProgrammerException("The list of types to compute lci is empty.");
		}
	}

	private Type lci(Type first, Type second, Binder root) throws LookupException {
		Type result = first;
		if(first.nbTypeParameters(TypeParameter.class) > 0) {
			result = Util.clone(first);
			result.setUniParent(first.parent());
			List<TypeArgument> firstArguments = arguments(first);
			List<TypeArgument> secondArguments = arguments(second);
			int size = firstArguments.size();
			if(secondArguments.size() != size) {
				throw new ChameleonProgrammerException("The number of type parameters from the first list: "+size+" is different from the number of type parameters in the second list: "+secondArguments.size());
			}
			List<TypeParameter> newParameters = lcta(firstArguments, secondArguments,root);
			result.replaceAllParameters(TypeParameter.class,newParameters);
		}
		return result;
	}

	private List<TypeArgument> arguments(Type type) {
		List<TypeParameter> parameters = type.parameters(TypeParameter.class);
		List<TypeArgument> result = new ArrayList<TypeArgument>();
		for(TypeParameter parameter: parameters) {
			result.add(Java7.cloneActualTypeArgument(parameter));
		}
		return result;
	}

	private List<TypeParameter> lcta(List<TypeArgument> firsts, List<TypeArgument> seconds, Binder root) throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		int size = firsts.size();
		for(int i=0; i<size;i++) {
			TypeArgument ith = firsts.get(i);
			Element parent = ith.parent();
			result.add(new InstantiatedTypeParameter(((TypeParameter)parent).name(),lcta(ith, seconds.get(i),root)));
		}
		return result;
	}

	private TypeReference glb(List<? extends JavaTypeReference> typeReferenceList) {
		return new JavaIntersectionTypeReference(typeReferenceList);
	}



	private TypeArgument lcta(TypeArgument first, TypeArgument second, Binder root) throws LookupException { // , List<List<? extends TypeReference>> trace
		TypeArgument result;
		if(first instanceof EqualityTypeArgument || second instanceof EqualityTypeArgument) {
			if(first instanceof EqualityTypeArgument && second instanceof EqualityTypeArgument) {
				Type U = ((EqualityTypeArgument)first).type();
				Type V = ((EqualityTypeArgument)second).type();
				if(U.sameAs(V)) {
					// lcta(U,V) = U if U = V
					result = Util.clone(first);
				} else {
					// otherwise ? extends lub(U,V)
					List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
					list.add((JavaTypeReference) ((EqualityTypeArgument)first).typeReference());
					list.add((JavaTypeReference) ((EqualityTypeArgument)second).typeReference());
					result = new Binder(list,root).argument();
				}
			} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
				EqualityTypeArgument basic = (EqualityTypeArgument) (first instanceof EqualityTypeArgument? first : second);
				ExtendsWildcard ext = (ExtendsWildcard)(basic == first ? second : first);
				result = new Binder(typeReferenceList(basic,ext),root).argument();
			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				EqualityTypeArgument basic = (EqualityTypeArgument) (first instanceof EqualityTypeArgument? first : second);
				SuperWildcard ext = (SuperWildcard)(basic == first ? second : first);
				result = java().createSuperWildcard(glb(typeReferenceList(basic,ext)));
			} else {
				result = null;
			}
		} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
			if(first instanceof ExtendsWildcard && second instanceof ExtendsWildcard) {
				List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
				list.add((JavaTypeReference) ((ExtendsWildcard)first).typeReference());
				list.add((JavaTypeReference) ((ExtendsWildcard)second).typeReference());
				result = new Binder(list,root).argument();

			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				ExtendsWildcard ext = (ExtendsWildcard) (first instanceof ExtendsWildcard? first : second);
				Type U = ((EqualityTypeArgument)first).type();
				Type V = ((EqualityTypeArgument)second).type();
				if(U.sameAs(V)) {
					result = java().createEqualityTypeArgument(Util.clone(ext.typeReference()));
				} else {
					result = java().createPureWildcard();
				}
			} else {
				result = null;
			}
		} else if (first instanceof SuperWildcard && second instanceof SuperWildcard) {
			result = java().createSuperWildcard(glb(typeReferenceList((SuperWildcard)first,(SuperWildcard)second)));
		} else {
			result = null;
		}
		if(result == null) {
			throw new ChameleonProgrammerException("lcta is not defined for the given actual type arguments of types " + first.getClass().getName() + " and " + second.getClass().getName());
		}
		result.setUniParent(first.parent()); 
		return result;
	}

	private List<JavaTypeReference> typeReferenceList(TypeArgumentWithTypeReference first, TypeArgumentWithTypeReference second) throws LookupException {
		List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
		list.add((JavaTypeReference) first.typeReference());
		list.add((JavaTypeReference) second.typeReference());
		return list;
	}


	private class Binder {

		public Binder(List<? extends JavaTypeReference> refs, Binder next) {
			_refs = ImmutableList.copyOf(refs);
			_next = next;
		}

		public TypeArgument argument() throws LookupException {
			return argument(_refs, this);
		}

		private boolean _active;

		private boolean _looped;

		protected TypeArgument argument(List<? extends JavaTypeReference> refs, Binder root) throws LookupException {
			if(_active && equal(refs,_refs)) {
				_looped = true;
				return loop();
			} else if(_next != null) {
				_active = true;
				return _next.argument(refs, root);
			} else {
				_active = true;
				Type leastUpperBound = leastUpperBound(refs, root);
				if(!_looped) {
					return createArgument(leastUpperBound);
				} else {
					return loop();
				}

			}
		}

		private boolean equal(List<? extends JavaTypeReference> first, List<? extends JavaTypeReference> second) throws LookupException {
			int size = first.size();
			boolean result = (size == second.size());
			if(result){
				List<Type> Tfirst = new ArrayList<>();
				for(int i=0; i<size;i++) {
					Tfirst.add(first.get(i).getElement());
				}
				for(int i=0; i<size;i++) {
					if(! Tfirst.contains(second.get(i).getElement())) {
						result = false;
						break;
					}
				}
			}
			return result;
		}

		protected TypeArgument loop() {
			return new PureWildcard();
		}

		protected TypeArgument createArgument(Type type) throws LookupException {
			final JavaTypeReference reference = java().reference(type);
			Element parent = reference.parent();
			reference.setUniParent(null);
			final NonLocalJavaTypeReference nonLocal = new NonLocalJavaTypeReference(reference,parent);
			return java().createExtendsWildcard(nonLocal);
		}

		private Binder _next;

		private List<? extends JavaTypeReference> _refs;
	}

	public static class UncheckedConversionIndicator {

		public void set() {
			_set=true;
		}

		public boolean isSet() {
			return _set;
		}

		private boolean _set;
	}

	public boolean convertibleThroughUncheckedConversionAndSubtyping(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first instanceof RawType) {
			result = ((RawType)first).convertibleThroughUncheckedConversionAndSubtyping(second);
		} else if(first instanceof ArrayType && second instanceof ArrayType) {
			ArrayType first2 = (ArrayType)first;
			ArrayType second2 = (ArrayType)second;
			result = convertibleThroughUncheckedConversionAndSubtyping(first2.elementType(), second2.elementType());
		} else {
			result = first.subtypeOf(second);
		}
		return result;
	}

	public boolean convertibleThroughMethodInvocationConversion(Type first, Type second, UncheckedConversionIndicator indicator) throws LookupException {
		boolean result = false;
		// JLS 4.1 & JLS 4.10 : Null type is convertible to any reference type.
		// FIXME Bad design! Delegating reference widening to the type itself would get rid
		// of this stupid case.
		boolean uncheckedConversion = false;
		if(first instanceof NullType) {
			result = second.isTrue(java().REFERENCE_TYPE);
		} else {
			if(first instanceof InstantiatedParameterType) {
				Type aliasedType = ((InstantiatedParameterType)first).aliasedType();
				result = convertibleThroughMethodInvocationConversion(aliasedType, second, indicator);
			} else {
				//FIXME This does not occur in tests. Can it actually occur?
				if(second instanceof InstantiatedParameterType) {
					return convertibleThroughMethodInvocationConversion(first, ((InstantiatedParameterType)second).aliasedType(), indicator);
				}

				// A) Identity conversion 
				if(first.sameAs(second)) {
					result = true;
				}
				// B) Widening conversion
				else if(convertibleThroughWideningPrimitiveConversion(first, second)) {
					// the result cannot be a raw type so no unchecked conversion is required.
					result = true;
				}
				// C) unboxing and optional widening conversion.
				else if(convertibleThroughUnboxingAndOptionalWidening(first,second)) {
					result = true;
				}
				// D) boxing and widening reference conversion.
				else if(convertibleThroughBoxingAndOptionalWidening(first,second)){
					// can't be raw, so no unchecked conversion can apply
					result = true;
				} else {

					// E) reference widening
					result = first.subtypeOf(second);
					//      if(superType != null) {
					if(! result) {
						//        if(superType.sameAs(second)) {
						//          result = true;
						//        } else {
						Type superType = first.getSuperType(second);
						if(superType != null) {
							if(convertibleThroughUncheckedConversionAndSubtyping(superType, second)) {
								uncheckedConversion = true;
								result = true;
							}
						}
						//        }
					}
				}
				if(uncheckedConversion) {
					indicator.set();
				}
			}
		}
		return result;
	}
	private boolean convertibleThroughBoxingAndOptionalWidening(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first.is(java().PRIMITIVE_TYPE) == Ternary.TRUE) {
			Type boxed = java().box(first);
			result = convertibleThroughWideningReferenceConversion(boxed, second);
		}
		return result;
	}

	private boolean convertibleThroughUnboxingAndOptionalWidening(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first.is(java().UNBOXABLE_TYPE) == Ternary.TRUE) {
			Type unboxed = java().unbox(first);
			if(unboxed.sameAs(second)) {
				result = true;
			} else {
				result = convertibleThroughWideningPrimitiveConversion(unboxed, second);
			}
		}
		return result;
	}

	private boolean convertibleThroughWideningReferenceConversion(Type first, Type second) throws LookupException {
		return first.subtypeOf(second);
	}

	private boolean convertibleThroughWideningPrimitiveConversion(Type first, Type second) throws LookupException {
		return primitiveWideningConversionCandidates(first).contains(second);
	}

	private Collection<Type> primitiveWideningConversionCandidates(Type type) throws LookupException {
		if(_primitiveWideningConversionCandidates == null) {
			_primitiveWideningConversionCandidates = new HashMap<>();
			JavaView view = type.view(JavaView.class);
			Type pDouble = view.primitiveType("double");
			Type pFloat = view.primitiveType("float");
			Type pLong = view.primitiveType("long");
			Type pInt = view.primitiveType("int");
			Type pByte = view.primitiveType("byte");
			Type pShort = view.primitiveType("short");
			Type pChar = view.primitiveType("char");

			Builder<Type> builder = ImmutableList.<Type>builder();

			builder.add(pDouble);
			//{double}
			ImmutableList<Type> list = builder.build();
			_primitiveWideningConversionCandidates.put(pFloat, list);

			builder.add(pFloat);
			//{float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pLong, list);

			builder.add(pLong);
			//{long,float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pInt, list);

			builder.add(pInt);
			//{int,long,float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pChar, list);
			_primitiveWideningConversionCandidates.put(pShort, list);

			builder.add(pShort);
			//{short,int,long,float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pByte, list);
		}
		List<Type> result = _primitiveWideningConversionCandidates.get(type);
		if(result == null) {
			result = ImmutableList.of();
		}
		return result;
	}

	private Map<Type, List<Type>> _primitiveWideningConversionCandidates;


}
