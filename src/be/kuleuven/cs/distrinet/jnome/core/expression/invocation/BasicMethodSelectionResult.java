package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.BasicTypeArgument;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.DirectJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaDerivedType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.rejuse.association.SingleAssociation;

public class BasicMethodSelectionResult implements MethodSelectionResult {

	public BasicMethodSelectionResult(Method template, TypeAssignmentSet assignment,int phase, boolean requiredUncheckedConversion) {
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
	public Declaration finalDeclaration() throws LookupException {
		return instantiatedMethodTemplate(_template);
	}
	
	@Override
	public SelectionResult updatedTo(Declaration declaration) {
		Method method = (Method) declaration;
		TypeAssignmentSet assignment = _assignment == null ? null : _assignment.updatedTo(method.typeParameters());
		return new BasicMethodSelectionResult(method, assignment, _phase,_requiredUncheckedConversion);
	}

	private Method _template;
	
	@Override
	public Method template() {
		return _template;
	}
	
	private TypeAssignmentSet _assignment;
	
	public TypeAssignmentSet typeAssignment() {
		return _assignment;
	}
	
	protected Method instantiatedMethodTemplate(Method method) throws LookupException {
		Method result=method;
		int nbTypeParameters = _assignment == null ? 0 : _assignment.nbAssignments();
		boolean cloned = false;
		if(nbTypeParameters > 0) {
			result = clonedMethod();
			cloned=true;
			for(int i=1; i <= nbTypeParameters;i++) {
				TypeParameter originalPar = _template.typeParameter(i);
				TypeParameter clonedPar = result.typeParameter(i);
				// we detach the signature from the clone.
				Type assignedType = _assignment.type(originalPar);
				Java language = _template.language(Java.class);
				JavaTypeReference reference = language.reference(assignedType);
				Element parent = reference.parent();
				reference.setUniParent(null);
				BasicTypeArgument argument = language.createBasicTypeArgument(reference);
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
			TypeReference erasure = (TypeReference)method.language(Java.class).erasure(result.returnTypeReference());
			result.setReturnTypeReference(erasure);
		} else {
			if(nbTypeParameters > 0) {
				Type returnType = returnType(method);
				if(returnType instanceof JavaDerivedType) {
					TypeReference oldDeclaredReturnTypeReference = result.returnTypeReference();
					Type declaredReturnType = result.returnType();
					// The capture conversion may refer to the old return type for the actual type arguments.
					// FIXME This is still a hack though, we should create a captured type reference based on the return type reference
					// there we can just clone any arguments such that the original return type reference is no longer needed.
					// FIXME No more substitutions can be done in the new return type reference as it is a direct one. Therefore
					//       we do need that captured type reference.
					result.setReturnTypeReference(new DirectJavaTypeReference(((JavaDerivedType)declaredReturnType).captureConversion()));
					oldDeclaredReturnTypeReference.setUniParent(result);
				}
			}
		}
		return result;
	}
	
	public Type returnType(Method method) throws LookupException {
		return method.returnType();
	}

	protected Method clonedMethod() {
		Method result;
		result = Util.clone(_template);
		result.setOrigin(_template);
		result.setUniParent(_template.parent());
		return result;
	}

}