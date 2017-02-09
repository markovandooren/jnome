package org.aikodi.java.core.statement;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
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

	
	@Override
	public LookupContext lookupContext(Element child) throws LookupException {
	  ResourceBlock resourceBlock = resourceBlock();
    if(child == resourceBlock || resourceBlock == null) {
	    return super.lookupContext(child);
	  } else {
	    return resourceBlock.lookupContext(null);
	  }
	}
}
