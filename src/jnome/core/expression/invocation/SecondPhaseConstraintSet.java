package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;

import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.IntersectionType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.ActualTypeArgumentWithTypeReference;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.PureWildCard;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;
import chameleon.exception.ChameleonProgrammerException;

public class SecondPhaseConstraintSet extends ConstraintSet<SecondPhaseConstraint> {

	public Set<Type> ST(JavaTypeReference U) throws LookupException {
		return U.getElement().getAllSuperTypes();
	}

	public Set<Type> EST(JavaTypeReference U) throws LookupException {
		Set<Type> STU = ST(U);
		Set<Type> result = new HashSet<Type>();
		for(Type type:STU) {
			result.add(U.language(Java.class).erasure(type));
		}
		return result;
	}

	public Set<Type> EC(TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = Us(Tj);
		List<Set<Type>> ESTs = new ArrayList<Set<Type>>();
		for(JavaTypeReference URef: Us) {
			ESTs.add(EST(URef));
		}
		Set<Type> result;
		int size = ESTs.size();
		if(size > 0) {
			result = ESTs.get(0);
			for(int i = 1; i< size; i++) {
				result.retainAll(ESTs.get(i));
			}
		} else {
		  result = new HashSet<Type>();
		}
		return result;
		// Take intersection
	}

	private List<JavaTypeReference> Us(TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = new ArrayList<JavaTypeReference>();
		for(SecondPhaseConstraint constraint: constraints()) {
			if(constraint.typeParameter().sameAs(Tj)) {
				Us.add(constraint.URef());
			}
		}
		return Us;
	}
	
	public Set<Type> MEC(TypeParameter Tj) throws LookupException {
		final Set<Type> EC = EC(Tj);
		new UnsafePredicate<Type, LookupException>() {
			@Override
			public boolean eval(final Type first) throws LookupException {
				return ! new UnsafePredicate<Type, LookupException>() {
					@Override
					public boolean eval(Type second) throws LookupException {
						return (! first.sameAs(second)) && (second.subTypeOf(first));
					}
				}.exists(EC);
			}
		}.filter(EC);
		return EC;
	}
	
	public Set<Type> Inv(Type G, TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = Us(Tj);
		Set<Type> result = new HashSet<Type>();
		for(JavaTypeReference U: Us) {
			result.addAll(Inv(G, U));
		}
		return result;
	}
	
	public Set<Type> Inv(Type G, JavaTypeReference U) throws LookupException {
		Type base = G.baseType();
		Set<Type> superTypes = ST(U);
		Set<Type> result = new HashSet<Type>();
		for(Type superType: superTypes) {
			if(superType.baseType().sameAs(base)) {
				result.add(superType);
			}
		}
		return result; 
	}
	
	public Type lci(Set<Type> types) throws LookupException {
		List<Type> list = new ArrayList<Type>(types);
		return lci(list);
	}
	
	public Type lci(List<Type> types) throws LookupException {
		int size = types.size();
		if(size == 1) {
			return types.get(0);
		} else if(size >= 2) {
			Type lci = lci(types.get(0), types.get(1));
			if(size == 2) {
				return lci;
			} else {
				List<Type> others = new ArrayList<Type>(types);
				// remove the first
				others.remove(0);
				// replace the second with lci
				others.set(0, lci);
				return lci(others);
				
			}
		} else {
			throw new ChameleonProgrammerException("The list of types to compute lci contains less than one element.");
		}
	}
	
	public Type lci(Type first, Type second) throws LookupException {
		Type result = first.clone();
		List<ActualTypeArgument> firstArguments = arguments(first);
		List<ActualTypeArgument> secondArguments = arguments(second);
		int size = firstArguments.size();
		if(secondArguments.size() != size) {
			throw new ChameleonProgrammerException("The number of type parameters from the first list: "+size+" is different from the number of type parameters in the second list: "+secondArguments.size());
		}
		List<TypeParameter> newParameters = lcta(firstArguments, secondArguments);
		result.replaceAllParameter(newParameters);
		return result;
	}
	
	public List<ActualTypeArgument> arguments(Type type) {
		List<TypeParameter> parameters = type.parameters();
		List<ActualTypeArgument> result = new ArrayList<ActualTypeArgument>();
		for(TypeParameter parameter: parameters) {
			if(parameter instanceof InstantiatedTypeParameter) {
				result.add(((InstantiatedTypeParameter) parameter).argument());
			} else {
				throw new ChameleonProgrammerException("Trying to get the actual type arguments of a type that still has formal type parameters");
			}
		}
		return result;
	}
	
	public List<TypeParameter> lcta(List<ActualTypeArgument> firsts, List<ActualTypeArgument> seconds) throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		int size = firsts.size();
		for(int i=0; i<size;i++) {
			result.add(new InstantiatedTypeParameter(((InstantiatedTypeParameter)firsts.get(i).parent()).signature().clone(),lcta(firsts.get(i), seconds.get(i))));
		}
		return result;
	}
	
	public List<JavaTypeReference> typeReferenceList(ActualTypeArgumentWithTypeReference first, ActualTypeArgumentWithTypeReference second) throws LookupException {
		List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
		list.add((JavaTypeReference) first.typeReference());
		list.add((JavaTypeReference) second.typeReference());
		return list;
	}
	
	public ActualTypeArgument lcta(ActualTypeArgument<?> first, ActualTypeArgument second) throws LookupException {
		if(first instanceof BasicTypeArgument || second instanceof BasicTypeArgument) {
			if(first instanceof BasicTypeArgument && second instanceof BasicTypeArgument) {
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					return first.clone();
				} else {
					List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
					list.add((JavaTypeReference) ((BasicTypeArgument)first).typeReference());
					list.add((JavaTypeReference) ((BasicTypeArgument)second).typeReference());
					return new ExtendsWildCard(lub(list));
				}
			}
			if(first instanceof ExtendsWildCard || second instanceof ExtendsWildCard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				ExtendsWildCard ext = (ExtendsWildCard)(basic == first ? second : first);
				return new ExtendsWildCard(lub(typeReferenceList(basic,ext)));
			}
			if(first instanceof SuperWildCard || second instanceof SuperWildCard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				SuperWildCard ext = (SuperWildCard)(basic == first ? second : first);
				return new SuperWildCard(first.language(Java.class).glb(typeReferenceList(basic,ext)));
			}
		} else if(first instanceof ExtendsWildCard || second instanceof ExtendsWildCard) {
			if(first instanceof ExtendsWildCard && second instanceof ExtendsWildCard) {
				List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
				list.add((JavaTypeReference) ((ExtendsWildCard)first).typeReference());
				list.add((JavaTypeReference) ((ExtendsWildCard)second).typeReference());
				return new ExtendsWildCard(lub(list));
			}
			if(first instanceof SuperWildCard || second instanceof SuperWildCard) {
				ExtendsWildCard ext = (ExtendsWildCard) (first instanceof ExtendsWildCard? first : second);
				SuperWildCard sup = (SuperWildCard)(ext == first ? second : first);
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					return new BasicTypeArgument(ext.typeReference().clone());
				} else {
					return new PureWildCard();
				}
			}
		} else if (first instanceof SuperWildCard && second instanceof SuperWildCard) {
			return new SuperWildCard(first.language(Java.class).glb(typeReferenceList((SuperWildCard)first,(SuperWildCard)second)));
		}
		throw new ChameleonProgrammerException("lcta is not defined for the given actual type arguments of types " + first.getClass().getName() + " and " + second.getClass().getName());
	}
	
	public JavaTypeReference lub(List<? extends JavaTypeReference> types) {
		return null;
		
	}
	
	public Type CandidateInvocation(Type G, TypeParameter Tj) throws LookupException {
		return lci(Inv(G,Tj));
	}
	
	public Type Candidate(Type W, TypeParameter Tj) throws LookupException {
		if(W.parameters().size() > 0) {
			return CandidateInvocation(W, Tj);
		} else {
			return W;
		}
	}
	
	public Type inferredType(TypeParameter Tj) throws LookupException {
		List<Type> MEC = new ArrayList<Type>(MEC(Tj));
		List<Type> candidates = new ArrayList<Type>();
		for(Type W:MEC) {
			candidates.add(Candidate(W,Tj));
		}
		return new IntersectionType(candidates);
	}
}
