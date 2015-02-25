package be.kuleuven.cs.distrinet.jnome.core.statement;

import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.support.variable.LocalVariableDeclarator;
import org.aikodi.chameleon.util.association.Multi;

public class ResourceBlock extends ElementImpl {

	@Override
	protected Element cloneSelf() {
		return new ResourceBlock();
	}

	private Multi<LocalVariableDeclarator> _resources = new Multi<>(this);
	
	public List<LocalVariableDeclarator> resources() {
		return _resources.getOtherEnds();
	}
	
	public void addResource(LocalVariableDeclarator decl) {
		add(_resources, decl);
	}

	public void removeResource(LocalVariableDeclarator decl) {
		remove(_resources, decl);
	}
}
