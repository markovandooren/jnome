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
import org.aikodi.chameleon.oo.type.IntersectionType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.TypeIndirection;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.ActualTypeArgument;
import org.aikodi.chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import org.aikodi.chameleon.oo.type.generics.BasicTypeArgument;
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
		Predicate<Type, LookupException> predicate = first -> ! exists(EC, second -> (! first.sameAs(second)) && (second.subTypeOf(first)));
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
              add = e.upperBound().upperBoundNotHigherThan(r.upperBound(),new TypeFixer()) && 
                  e.lowerBound().upperBoundNotHigherThan(r.lowerBound(),new TypeFixer()) && 
                  r.upperBound().upperBoundNotHigherThan(e.upperBound(),new TypeFixer()) && 
                  r.lowerBound().upperBoundNotHigherThan(e.lowerBound(),new TypeFixer());
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
//		Set<Type> STU = ST(U);
//		Set<Type> result = new HashSet<Type>();
//		for(Type type:STU) {
//			Type erasure = java().erasure(type);
//			result.add(erasure);
//		}
//		return result;

		// TODO I think that the set of erased super types of a type T is the set of super
		//      types of the erasure of T.
		return ((JavaType)U.getElement()).erasure().getSelfAndAllSuperTypesView();
	}

	private Type intersection(List<Type> candidates)
			throws LookupException {
		if(candidates.isEmpty()) {
			throw new LookupException("No candidates for the inferred type");
		} else if(candidates.size() == 1) {
			return candidates.get(0);
		} else {
			return IntersectionType.create(candidates);
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
//		Type base = G.baseType();
//		Set<Type> superTypes = ST(U);
//		//FIXME: Because we use a set, bugs may seem to disappear when debugging.
//		//       Do we have to use a set anyway? The operations applied to it
//		//       further on should work exactly the same whether there are duplicates or not
//		Set<Type> result = new HashSet<Type>();
//		for(Type superType: superTypes) {
//			if(superType.baseType().sameAs(base)) {
//				while(superType instanceof InstantiatedParameterType) {
//					superType = ((InstantiatedParameterType) superType).aliasedType();
//				}
//				result.add(superType);
//			}
//		}
//		return result; 
	}

//	private Type lci(Set<Type> types, Binder root) throws LookupException {
//		List<Type> list = new ArrayList<Type>(types);
//		return lci(list,root);
//	}

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
			List<ActualTypeArgument> firstArguments = arguments(first);
			List<ActualTypeArgument> secondArguments = arguments(second);
			int size = firstArguments.size();
			if(secondArguments.size() != size) {
				throw new ChameleonProgrammerException("The number of type parameters from the first list: "+size+" is different from the number of type parameters in the second list: "+secondArguments.size());
			}
			List<TypeParameter> newParameters = lcta(firstArguments, secondArguments,root);
			result.replaceAllParameters(TypeParameter.class,newParameters);
		}
		return result;
	}

	private List<ActualTypeArgument> arguments(Type type) {
		List<TypeParameter> parameters = type.parameters(TypeParameter.class);
		List<ActualTypeArgument> result = new ArrayList<ActualTypeArgument>();
		for(TypeParameter parameter: parameters) {
			result.add(Java7.argument(parameter));
		}
		return result;
	}

	private List<TypeParameter> lcta(List<ActualTypeArgument> firsts, List<ActualTypeArgument> seconds, Binder root) throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		int size = firsts.size();
		for(int i=0; i<size;i++) {
			ActualTypeArgument ith = firsts.get(i);
			Element parent = ith.parent();
			result.add(new InstantiatedTypeParameter(((TypeParameter)parent).name(),lcta(ith, seconds.get(i),root)));
		}
		return result;
	}
	
  private TypeReference glb(List<? extends JavaTypeReference> typeReferenceList) {
    return new JavaIntersectionTypeReference(typeReferenceList);
  }



	private ActualTypeArgument lcta(ActualTypeArgument first, ActualTypeArgument second, Binder root) throws LookupException { // , List<List<? extends TypeReference>> trace
		ActualTypeArgument result;
		if(first instanceof BasicTypeArgument || second instanceof BasicTypeArgument) {
			if(first instanceof BasicTypeArgument && second instanceof BasicTypeArgument) {
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					// lcta(U,V) = U if U = V
					result = Util.clone(first);
				} else {
					// otherwise ? extends lub(U,V)
					List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
					list.add((JavaTypeReference) ((BasicTypeArgument)first).typeReference());
					list.add((JavaTypeReference) ((BasicTypeArgument)second).typeReference());
					result = new Binder(list,root).argument();
				}
			} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				ExtendsWildcard ext = (ExtendsWildcard)(basic == first ? second : first);
				result = new Binder(typeReferenceList(basic,ext),root).argument();
			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
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
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					result = java().createBasicTypeArgument(Util.clone(ext.typeReference()));
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

	private List<JavaTypeReference> typeReferenceList(ActualTypeArgumentWithTypeReference first, ActualTypeArgumentWithTypeReference second) throws LookupException {
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

		public ActualTypeArgument argument() throws LookupException {
			return argument(_refs, this);
		}

		private boolean _active;

		private boolean _looped;

		protected ActualTypeArgument argument(List<? extends JavaTypeReference> refs, Binder root) throws LookupException {
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

		protected ActualTypeArgument loop() {
			return new PureWildcard();
		}

		protected ActualTypeArgument createArgument(Type type) throws LookupException {
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
		  return first.superTypeJudge().get(second) != null;
//			Set<Type> supers = first.getSelfAndAllSuperTypesView();
//			for(Type type: supers) {
//				if(type.baseType().sameAs(second.baseType())) {
//					return true;
//				}
//			}
			
			
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
			return second.isTrue(java().REFERENCE_TYPE);
		}
		if(first instanceof TypeIndirection) {
			return convertibleThroughMethodInvocationConversion(((TypeIndirection)first).aliasedType(), second, indicator);
		} else if(second instanceof TypeIndirection) {
			return convertibleThroughMethodInvocationConversion(first, ((TypeIndirection)second).aliasedType(), indicator);
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
		  Type superType = first.getSuperType(second);
		  if(superType != null) {
		    if(superType.sameAs(second)) {
		      result = true;
		    } else {
		      if(convertibleThroughUncheckedConversionAndSubtyping(superType, second)) {
		        uncheckedConversion = true;
		        result = true;
		      }

		    }
		  }
		}
		if(uncheckedConversion) {
			indicator.set();
		}
		return result;
	}


	private boolean convertibleThroughBoxingAndOptionalWidening(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first.is(java().PRIMITIVE_TYPE) == Ternary.TRUE) {
			Type tmp = java().box(first);
			result = convertibleThroughWideningReferenceConversion(tmp, second);
		}
		return result;
	}

	private boolean convertibleThroughUnboxingAndOptionalWidening(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first.is(java().UNBOXABLE_TYPE) == Ternary.TRUE) {
			Type tmp = java().unbox(first);
			if(tmp.sameAs(second)) {
				result = true;
			} else {
				result = convertibleThroughWideningPrimitiveConversion(tmp, second);
			}
		}
		return result;
	}

	private boolean convertibleThroughWideningReferenceConversion(Type first, Type second) throws LookupException {
	  return first.subTypeOf(second);
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
