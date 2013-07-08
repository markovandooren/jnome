package be.kuleuven.cs.distrinet.jnome.core.statement;

import be.kuleuven.cs.distrinet.chameleon.oo.statement.Statement;
import be.kuleuven.cs.distrinet.chameleon.support.statement.TryStatement;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

public class JavaTryStatement extends TryStatement {

	public JavaTryStatement(Statement statement) {
		super(statement);
	}

	private Single<ResourceBlock> _resources = new Single<>(this);
	
	public ResourceBlock resourceBlock() {
		return _resources.getOtherEnd();
	}
	
	public void setResourceBlock(ResourceBlock block) {
		set(_resources,block);
	}
	
	@Override
	protected JavaTryStatement cloneSelf() {
		return new JavaTryStatement(null);
	}
}
