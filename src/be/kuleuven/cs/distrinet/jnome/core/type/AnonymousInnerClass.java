package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.ConstructorInvocation;

public class AnonymousInnerClass extends AbstractAnonymousInnerClass {

	public AnonymousInnerClass(ConstructorInvocation invocation) {
		_invocation = invocation;
	}
	
	@Override
	public SimpleNameSignature signature() {
		SimpleNameSignature result = new SimpleNameSignature("");
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
	protected AnonymousInnerClass cloneThis() {
		return new AnonymousInnerClass(invocation());
	}
	
//	@Override
//	public List<InheritanceRelation> inheritanceRelations() {
//		compile
//	}
	
}
