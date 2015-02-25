package be.kuleuven.cs.distrinet.jnome.core.statement;

import org.aikodi.chameleon.oo.statement.Statement;
import org.aikodi.chameleon.support.statement.TryStatement;
import org.aikodi.chameleon.util.association.Single;

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
