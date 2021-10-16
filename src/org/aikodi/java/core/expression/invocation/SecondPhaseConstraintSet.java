package org.aikodi.java.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.language.WrongLanguageException;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.language.LanguageWithBoxing;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguageImpl;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.BoxableTypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.support.expression.AssignmentExpression;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.type.JavaTypeReference;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;

public class SecondPhaseConstraintSet extends ConstraintSet<SecondPhaseConstraint> {

	public SecondPhaseConstraintSet(MethodInvocation invocation, MethodHeader invokedMethod, FirstPhaseConstraintSet origin) {
		super(invocation,invokedMethod);
		_assignments = new TypeAssignmentSet(typeParameters());
		_origin = origin;
	}
	
	private FirstPhaseConstraintSet _origin;

	private List<BoxableTypeReference> Us(TypeParameter Tj, Class<? extends SecondPhaseConstraint> kind) throws LookupException {
		List<BoxableTypeReference> Us = new ArrayList<BoxableTypeReference>();
		for(SecondPhaseConstraint constraint: constraints()) {
			if((kind.isInstance(constraint)) && constraint.typeParameter().sameAs(Tj)) {
				Us.add(constraint.URef());
			}
		}
		return Us;
	}
	
	private Type leastUpperBound(List<? extends BoxableTypeReference> Us, Java7 language) throws LookupException {
		return language.subtypeRelation().leastUpperBound(Us);
	}

	private void processSuperTypeConstraints() throws LookupException {
		Java7 language = null;
		for(TypeParameter p: typeParameters()) {
			boolean hasSuperConstraints = false;
			for(SecondPhaseConstraint constraint: constraints()) {
				if(constraint instanceof SupertypeConstraint && constraint.typeParameter().sameAs(p)) {
					hasSuperConstraints = true;
					break;
				}
			}
			if(hasSuperConstraints) {
				List<BoxableTypeReference> Us = Us(p, SupertypeConstraint.class);
				if(language == null) {
					language = p.language(Java7.class);
				}
				add(new ActualTypeAssignment(p, leastUpperBound(Us,language)));
			}
		}
	}
	
	private void processEqualityConstraints() throws LookupException {
		boolean searching = true;
		while(searching) {
			// Keep processing until there are no equality constraints.
		  EqualTypeConstraint eq = first(EqualTypeConstraint.class);
			if(eq != null) {
			  eq.process();
			} else {
				searching = false;
			}
		}
	}
	
	
	public TypeAssignmentSet assignments() {
		return _assignments;
	}
	
	public void add(TypeAssignment assignment) {
		_assignments.add(assignment);
	}
	
	private TypeAssignmentSet _assignments;

  public void process() throws LookupException {
  	processEqualityConstraints();
  	processSuperTypeConstraints();
  	processUnresolvedParameters();
  }
  
  private void processUnresolvedParameters() throws LookupException {
  	// JLS 15.12.2.8
  	// In context of assignment with type S.
  	if(inContextOfAssignmentConversion()) {
  		processUnresolved(S());
  	} else {
  		// Perform under the assumption that S is java.lang.Object
  		if(! unresolvedParameters().isEmpty()) {
  			View view = invocation().view();
				ObjectOrientedLanguage language = (ObjectOrientedLanguage) view.language();
  			processUnresolved((JavaTypeReference) language.createTypeReferenceInNamespace(language.getDefaultSuperClassFQN(),view.namespace()));
  		}
  	}
  }

  private BoxableTypeReference box(BoxableTypeReference ref) throws WrongLanguageException, LookupException {
  	return ref.box();
  }
  
	private void processUnresolved(BoxableTypeReference Sref) throws LookupException {
		// WARNING
		// INCOMPLETE see processSubtypeConstraints
		BoxableTypeReference RRef = (BoxableTypeReference) invokedGenericMethod().returnTypeReference();
		FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(), invokedGenericMethod());
		View view = RRef.view();
		BoxableTypeReference SprimeRef = box(Sref);
		Java7 java = view.language(Java7.class);
		if(! RRef.getElement().sameAs(java.voidType(RRef.view().namespace()))) {
		  // the constraint S >> R', provided R is not void	
			BoxableTypeReference RprimeRef = substitutedReference(RRef);
			constraints.add(new GGConstraint(SprimeRef, RprimeRef.getElement()));
		}
		// additional constraints Bi[T1=B(T1) ... Tn=B(Tn)] >> Ti where Bi is the declared bound of Ti
		for(TypeParameter param: typeParameters()) {
			BoxableTypeReference Bi = (JavaTypeReference) param.upperBoundReference();
			BoxableTypeReference BiAfterSubstitution = substitutedReference(Bi);
			
			Type Ti = (Type) param.selectionDeclaration();
			Type BTi = assignments().type(param);
			if(BTi == null) {
				BTi = Ti;
			}
			constraints.add(new GGConstraint(BiAfterSubstitution, Ti));
			constraints.add(new SSConstraint(java.reference(BTi), BiAfterSubstitution.getElement()));
			
		}
		for(GGConstraint constraint: _origin.generatedGG()) {
			constraints.add(new GGConstraint(substitutedReference(constraint.ARef()), constraint.F()));
		}
		for(EQConstraint constraint: _origin.generatedEQ()) {
			constraints.add(new EQConstraint(substitutedReference(constraint.ARef()), constraint.F()));
		}
		
		SecondPhaseConstraintSet seconds = constraints.secondPhase();
		// JLS: Any equality constraints are resolved ...
		seconds.processEqualityConstraints();
		// JLS: ..., and then, for each remaining constraint of the form Ti <: Uk, the argument
		//      Ti is inferred to be glb(U1,...,Uk)
		seconds.processSubtypeConstraints();
		//Any remaining type variable T that has not yet been inferred is then inferred
		// to have type Object . If a previously inferred type variable P uses T , then P is
		// inferred to be P [T =Object ].
		for(TypeParameter param: seconds.unresolvedParameters()) {
			seconds.add(new ActualTypeAssignment(param, java.getDefaultSuperClass(view.namespace())));
		}
  	//FIXME Perform substitution
		
		
		for(TypeParameter param: unresolvedParameters()) {
			add(seconds.assignments().assignment(param));
		}
	}
	
	public void substituteRHS(BoxableTypeReference tref, EqualTypeConstraint eq) throws LookupException {
		for(SecondPhaseConstraint constraint: constraints()) {
			if(constraint != eq) {
				if(constraint.typeParameter().sameAs(eq.typeParameter())) {
					remove(constraint);
				} else {
					final TypeParameter tp = eq.typeParameter();
					TypeReference uRef = constraint.URef();
					NonLocalJavaTypeReference.replace(tref, tp, uRef);
				}
			}
		}
	}
	
	private void processSubtypeConstraints() throws LookupException {
		// WARNING INCOMPLETE (but I should have written why :/ )
		for(TypeParameter p: typeParameters()) {
			boolean hasSubConstraints = false;
			for(SecondPhaseConstraint constraint: constraints()) {
				if(constraint instanceof SubtypeConstraint && constraint.typeParameter().sameAs(p)) {
					hasSubConstraints = true;
					break;
				}
			}
			if(hasSubConstraints) {
				Type glb = glb(p);
				add(new ActualTypeAssignment(p, glb));
				JavaTypeReference tref = (JavaTypeReference) glb.language(ObjectOrientedLanguage.class).reference(glb);
        // WARNING As far as I can tell, this is not in the Java language specification.
				// In the language specification, the substitution is done only when equality
				// constraints have been generated and when there are no constraints (then Object is used).
				//
				// If the JLS is not incomplete on this point, we are missing a constraint.
				//
				// Without this substitution however, there is a problem where there a subtype constraint
				// is created for a parameter that uses another parameter in its bounds. For example
				// in the Java 8 method <T, C extends Collection<T>> Collector<T, ?, C> 
				// {@link java.util.stream.Collectors#toCollection(Supplier<C>)}. The constraint
				// for C is 'C <: Collection<T>>'. Type T, however, should not escape the method and be
				// part of the inferred type. Because there cannot be a loop in the bounds according
				// to the language specification, we can safely perform the substitution.
				// FIXME We don't actually perform that check. Since we iterate over each parameter only
				// once, at most we would get an incorrect result. Still, we should throw an exception instead.
				for(SecondPhaseConstraint constraint: constraints()) {
					if(! constraint.typeParameter().sameAs(p)) {
  					TypeReference uRef = constraint.URef();
				    NonLocalJavaTypeReference.replace(tref, p, uRef);
					}
				}
			}
		}

	}
	
	private Type glb(TypeParameter Tj) throws LookupException {
		// INCOMPLETE: If there are multiple constraints, we cannot
		// return TjType in case of self references in the bounds.
		// we must clone the entire type parameter block to ensure
		// that any other references to Tj are properly rerouted
		// to the 'fresh type variable X' which will just
		// be a clone of Tj with glb(URefs) as its extends constraint bound.
		List<BoxableTypeReference> URefs = Us(Tj, SubtypeConstraint.class);
		boolean recursive = false;
		List<Type> Us = new ArrayList<Type>();
		Type TjType = null;
		for(TypeReference URef: URefs) {
			List<TypeReference> descendants = URef.lexical().descendants(TypeReference.class);
			descendants.add(URef);
			for(TypeReference tref: descendants) {
				Type type = tref.getElement();
				if(type instanceof TypeVariable && ((TypeVariable) type).parameter().origin() == Tj) {
					TjType = type;
					recursive = true;
				}
			}
			if(recursive) {
				break;
			}
			Us.add(URef.getElement());
		}
		if(! recursive) {
			Type intersectionType = Tj.language().plugin(ObjectOrientedFactory.class).createIntersectionType(Us);
			return intersectionType;
		} else {
			return TjType;
		}
	}

	
	private Set<TypeParameter> unresolvedParameters() {
		Set<TypeParameter> typeParameters = ImmutableSet.copyOf(typeParameters());
		return Sets.difference(typeParameters, resolvedParameters());
	}

	private Set<TypeParameter> resolvedParameters() {
		Builder<TypeParameter> builder = ImmutableSet.<TypeParameter>builder();
		for(TypeAssignment assignment: assignments().assignments()) {
			builder.add(assignment.parameter());
		}
		return builder.build();
	}
	


	private BoxableTypeReference substitutedReference(BoxableTypeReference RRef) throws LookupException {
		BoxableTypeReference RprimeRef = Util.clone(RRef);
		RprimeRef.setUniParent(RRef.lexical().parent());
		// Let R' = R[T1=B(T1) ... Tn=B(Tn)] where B(Ti) is the type inferred for Ti in the previous section, or Ti if no type was inferred.
		for(TypeAssignment assignment: assignments().assignments()) {
			Type type = assignment.type();
			BoxableTypeReference replacement = RRef.language(LanguageWithBoxing.class).reference(type);
			RprimeRef = (BoxableTypeReference) NonLocalJavaTypeReference.replace(replacement, assignment.parameter(), RprimeRef);
		}
		return RprimeRef;
	}
  
	private BoxableTypeReference S() throws LookupException {
  	if(! inContextOfAssignmentConversion()) {
  		throw new ChameleonProgrammerException();
  	} else {
  		return ((TypeParameter)typeParameters().get(0)).language(Java7.class).reference(((AssignmentExpression)invocation().parent()).variableExpression().getType());
  	}
  }
  
  private boolean inContextOfAssignmentConversion() {
  	return invocation().parent() instanceof AssignmentExpression;
  }
  
}
