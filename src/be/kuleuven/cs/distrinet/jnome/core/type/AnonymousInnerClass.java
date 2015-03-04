package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.declaration.Name;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.ConstructorInvocation;

public class AnonymousInnerClass extends AbstractAnonymousInnerClass {

	public AnonymousInnerClass(ConstructorInvocation invocation) {
		_invocation = invocation;
	}
	
	@Override
	public Name signature() {
		Name result = new Name("");
		result.setUniParent(this);
		return result;
	}
	
	private ConstructorInvocation _invocation;
	
	public ConstructorInvocation invocation() {
		return _invocation;
	}
	
	public TypeReference typeReference() {
		return nearestAncestor(ConstructorInvocation.class).getTypeReference();
	}

	@Override
	public Type erasure() {
		return this;
	}
	
	@Override
	protected void copyContents(Type from, boolean link) {
		copyEverythingExceptInheritanceRelations(from,link);
	}

	@Override
	protected AnonymousInnerClass cloneSelf() {
		//FIXME Fix this ugly code. Probably by creating a factory method to create
		//      the appropriate parameter block for a give parameter type.
		AnonymousInnerClass anonymousInnerClass = new AnonymousInnerClass(invocation());
		anonymousInnerClass.parameterBlock(TypeParameter.class).disconnect();
		return anonymousInnerClass;
	}
	
//	@Override
//	public List<InheritanceRelation> inheritanceRelations() {
//		compile
//	}
	
}
