package be.kuleuven.cs.distrinet.jnome.workspace;

import be.kuleuven.cs.distrinet.jnome.input.ReflectiveClassParser;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;

public interface ReflectiveInputSourceFactory {
	public LazyReflectiveInputSource create(ReflectiveClassParser parser, String className, Namespace namespace);
}
