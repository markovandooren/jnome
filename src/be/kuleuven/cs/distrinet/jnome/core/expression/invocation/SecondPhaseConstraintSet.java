package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.language.WrongLanguageException;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.type.IntersectionType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.FormalParameterType;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.support.expression.AssignmentExpression;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

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

	private List<JavaTypeReference> Us(TypeParameter Tj, Class<? extends SecondPhaseConstraint> kind) throws LookupException {
		List<JavaTypeReference> Us = new ArrayList<JavaTypeReference>();
		for(SecondPhaseConstraint constraint: constraints()) {
			if((kind.isInstance(constraint)) && constraint.typeParameter().sameAs(Tj)) {
				Us.add(constraint.URef());
			}
		}
		return Us;
	}
	


	
	private Type leastUpperBound(List<? extends JavaTypeReference> Us, Java7 language) throws LookupException {
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
				List<JavaTypeReference> Us = Us(p, SupertypeConstraint.class);
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
//  			TypeParameter typeParameter = typeParameters().get(0);
  			View view = invocation().view();
				ObjectOrientedLanguage language = (ObjectOrientedLanguage) view.language();
  			processUnresolved((JavaTypeReference) language.createTypeReferenceInNamespace(language.getDefaultSuperClassFQN(),view.namespace()));
  		}
  	}
  }

  private JavaTypeReference box(JavaTypeReference ref) throws WrongLanguageException, LookupException {
  	return ref.box();
  }
  
	private void processUnresolved(JavaTypeReference Sref) throws LookupException {
		// WARNING
		// INCOMPLETE see processSubtypeConstraints
		JavaTypeReference RRef = (JavaTypeReference) invokedGenericMethod().returnTypeReference();
		FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(), invokedGenericMethod());
		View view = RRef.view();
		JavaTypeReference SprimeRef = box(Sref);
		Java7 java = view.language(Java7.class);
		if(! RRef.getElement().sameAs(java.voidType(RRef.view().namespace()))) {
		  // the constraint S >> R', provided R is not void	
			JavaTypeReference RprimeRef = substitutedReference(RRef);
			constraints.add(new GGConstraint(SprimeRef, RprimeRef.getElement()));
		}
		// additional constraints Bi[T1=B(T1) ... Tn=B(Tn)] >> Ti where Bi is the declared bound of Ti
		for(TypeParameter param: typeParameters()) {
			JavaTypeReference Bi = (JavaTypeReference) param.upperBoundReference();
			JavaTypeReference BiAfterSubstitution= substitutedReference(Bi);
			
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
		// SPEED why not use the 'java' var and view from above? 
		// not going to change this during the refactoring, too risky
		for(TypeParameter param: seconds.unresolvedParameters()) {
			seconds.add(new ActualTypeAssignment(param, java.getDefaultSuperClass(view.namespace())));
		}
		for(TypeParameter param: unresolvedParameters()) {
			add(seconds.assignments().assignment(param));
		}
	}
	
	private void processSubtypeConstraints() throws LookupException {
		// WARNING
		// INCOMPLETE
		for(TypeParameter p: typeParameters()) {
			boolean hasSubConstraints = false;
			for(SecondPhaseConstraint constraint: constraints()) {
				if(constraint instanceof SubtypeConstraint && constraint.typeParameter().sameAs(p)) {
					hasSubConstraints = true;
					break;
				}
			}
			if(hasSubConstraints) {
				add(new ActualTypeAssignment(p, glb(p)));
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
		List<JavaTypeReference> URefs = Us(Tj, SubtypeConstraint.class);
		boolean recursive = false;
		List<Type> Us = new ArrayList<Type>();
		Type TjType = null;
		for(JavaTypeReference URef: URefs) {
			List<TypeReference> descendants = URef.descendants(TypeReference.class);
			descendants.add(URef);
			for(TypeReference tref: descendants) {
				Type type = tref.getElement();
				if(type instanceof FormalParameterType && ((FormalParameterType) type).parameter().origin() == Tj) {
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
			Type intersectionType = IntersectionType.create(Us);
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
	


	private JavaTypeReference substitutedReference(JavaTypeReference RRef) throws LookupException {
		JavaTypeReference RprimeRef = Util.clone(RRef);
		RprimeRef.setUniParent(RRef.parent());
		// Let R' = R[T1=B(T1) ... Tn=B(Tn)] where B(Ti) is the type inferred for Ti in the previous section, or Ti if no type was inferred.
		for(TypeAssignment assignment: assignments().assignments()) {
			Type type = assignment.type();
			JavaTypeReference replacement = RRef.language(Java7.class).reference(type);
//			replacement.setUniParent(RRef.language().defaultNamespace()); XXX
			RprimeRef = (JavaTypeReference) NonLocalJavaTypeReference.replace(replacement, assignment.parameter(), RprimeRef);
		}
		return RprimeRef;
	}
  
	private JavaTypeReference S() throws LookupException {
  	if(! inContextOfAssignmentConversion()) {
  		throw new ChameleonProgrammerException();
  	} else {
//  		return new DirectJavaTypeReference(((AssignmentExpression)invocation().parent()).getVariable().getType());
  		return ((TypeParameter)typeParameters().get(0)).language(Java7.class).reference(((AssignmentExpression)invocation().parent()).variableExpression().getType());
  	}
  }
  
  private boolean inContextOfAssignmentConversion() {
  	return invocation().parent() instanceof AssignmentExpression;
  }
  
}
