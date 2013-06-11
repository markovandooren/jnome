package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.Result;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class DependencyResult extends Result<DependencyResult> {

	public DependencyResult(Type type, Set<Type> dependencies) {
		Set<Type> myDeps = ImmutableSet.<Type>copyOf(dependencies);
		_dependencies = ImmutableMap.<Type, Set<Type>>builder().put(type, myDeps).build(); 
	}
	
	private DependencyResult(DependencyResult first, DependencyResult second) {
		_dependencies = ImmutableMap.<Type, Set<Type>>builder()
				                        .putAll(first._dependencies)
				                        .putAll(second._dependencies).build();
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
	
	public Map<Type,Set<Type>> dependencies() {
		return _dependencies;
	}

	private ImmutableMap<Type, Set<Type>> _dependencies;
}
