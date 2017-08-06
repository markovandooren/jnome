package org.aikodi.java.core.expression.invocation;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.type.DirectJavaTypeReference;
import org.aikodi.java.core.type.JavaType;
import org.aikodi.java.core.type.JavaTypeInstantiation;
import org.aikodi.java.core.type.JavaTypeReference;
import org.aikodi.rejuse.association.SingleAssociation;

/**
 * A class for selecting methods in Java 7.
 * 
 * It keeps track of the type assignment that was done,
 * whether or not unchecked conversion was required,
 * and in which phase the result was found. This information
 * is required to be able to search in multiple locations
 * and afterwards combined the results.
 * 
 * @author Marko van Dooren
 *
 * @param <M> The kind of method that is being looked up.
 */
public class BasicMethodSelectionResult<M extends Method> implements MethodSelectionResult<M> {

	public BasicMethodSelectionResult(M template, TypeAssignmentSet assignment,int phase, boolean requiredUncheckedConversion) {
		_template = template;
		_assignment = assignment;
		_phase = phase;
		_requiredUncheckedConversion = requiredUncheckedConversion;
	}
	
	@Override
	public boolean requiredUncheckedConversion() {
		return _requiredUncheckedConversion;
	}
	
	private boolean _requiredUncheckedConversion;
	
	public int phase() {
		return _phase;
	}
	
	private int _phase;
	
	/**
	 * Return the method result of the lookup. The type assignments are applied
	 * to the template, and all type conversions are applied such that the result
	 * behaves as specified in the JLS. 
	 */
	@Override
	public M finalDeclaration() throws LookupException {
		return instantiatedMethodTemplate(_template);
	}
	
	@Override
	public SelectionResult<M> updatedTo(Declaration declaration) {
		Method method = (Method) declaration;
		TypeAssignmentSet assignment = _assignment == null ? null : _assignment.updatedTo(method.typeParameters());
		return new BasicMethodSelectionResult<M>((M) method, assignment, _phase,_requiredUncheckedConversion);
	}

	private M _template;
	
	@Override
	public M template() {
		return _template;
	}
	
	private TypeAssignmentSet _assignment;
	
	public TypeAssignmentSet typeAssignment() {
		return _assignment;
	}
	
	protected M instantiatedMethodTemplate(Method method) throws LookupException {
		M result=(M) method;
		int nbTypeParameters = _assignment == null ? 0 : _assignment.nbAssignments();
		boolean cloned = false;
		if(nbTypeParameters > 0) {
			result = clonedMethod();
			cloned=true;
			for(int i=0; i < nbTypeParameters;i++) {
				TypeParameter originalPar = _template.typeParameter(i);
				TypeParameter clonedPar = result.typeParameter(i);
				// we detach the signature from the clone.
				Type assignedType = _assignment.type(originalPar);
				Java7 language = _template.language(Java7.class);
				JavaTypeReference reference = language.reference(assignedType);
				Element parent = reference.lexical().parent();
				reference.setUniParent(null);
				EqualityTypeArgument argument = language.createEqualityTypeArgument(reference);
				argument.setUniParent(parent);
				TypeParameter newPar = new InstantiatedTypeParameter(clonedPar.name(), argument);
				SingleAssociation parentLink = clonedPar.parentLink();
				parentLink.getOtherRelation().replace(parentLink, newPar.parentLink());
			}
		}
		if(requiredUncheckedConversion()) {
			if(! cloned) {
				result = clonedMethod();
			}
			TypeReference erasure = (TypeReference)method.language(Java7.class).erasure(result.returnTypeReference());
			result.setReturnTypeReference(erasure);
		} else {
			if(nbTypeParameters > 0) {
				Type returnType = returnType(method);
				if(returnType instanceof JavaTypeInstantiation) {
					TypeReference oldDeclaredReturnTypeReference = result.returnTypeReference();
					Type declaredReturnType = result.returnType();
					// The capture conversion may refer to the old return type for the actual type arguments.
					// FIXME This is still a hack though, we should create a captured type reference based on the return type reference
					// there we can just clone any arguments such that the original return type reference is no longer needed.
					// FIXME No more substitutions can be done in the new return type reference as it is a direct one. Therefore
					//       we do need that captured type reference.
					result.setReturnTypeReference(new DirectJavaTypeReference(((JavaTypeInstantiation)declaredReturnType).captureConversion()));
					oldDeclaredReturnTypeReference.setUniParent(result);
				}
			}
		}
		return result;
	}
	
	public Type returnType(Method method) throws LookupException {
		return method.returnType();
	}

	protected M clonedMethod() {
		M result = Util.clone(_template);
		result.setOrigin(_template);
		result.setUniParent(_template.parent());
		return result;
	}

}