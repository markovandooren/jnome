package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.Result;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;

public class DependencyResult<D extends Declaration> extends Result<DependencyResult<D>> {

	public DependencyResult(D type, Set<D> dependencies) {
//		Set<D> myDeps = ImmutableSet.<D>copyOf(dependencies);
//		_dependencies = ImmutableMap.<D, Set<D>>builder().put(type, myDeps).build();
		Set<D> myDeps = new HashSet<D>(dependencies);
		_dependencies = new HashMap<D,Set<D>>();
		_dependencies.put(type, myDeps);
	}
	
	public DependencyResult() {
//		_dependencies = ImmutableMap.<D, Set<D>>builder().build(); 
		_dependencies = new HashMap<D,Set<D>>();
	}
	
	private DependencyResult(DependencyResult first, DependencyResult second) {
//		_dependencies = ImmutableMap.<D, Set<D>>builder()
//				                        .putAll(first._dependencies)
//				                        .putAll(second._dependencies).build();
		_dependencies = new HashMap<D,Set<D>>();
		_dependencies.putAll(first._dependencies);
		_dependencies.putAll(second._dependencies);
	}
	
	@Override
	public String message() {
		return "TODO";
	}

	@Override
	public DependencyResult and(DependencyResult other) {
		if(other == null) {
			return this;
		} else {
			return new DependencyResult(this, other);
		}
	}

	protected void check() {
		if(_dependencies == null) {
			throw new ChameleonProgrammerException("");
		}
	}
	
	public Map<D,Set<D>> dependencies() {
		return _dependencies;
	}

	private Map<D, Set<D>> _dependencies;
}
