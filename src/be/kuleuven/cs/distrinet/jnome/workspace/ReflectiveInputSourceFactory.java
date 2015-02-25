package be.kuleuven.cs.distrinet.jnome.workspace;

import org.aikodi.chameleon.core.namespace.Namespace;

import be.kuleuven.cs.distrinet.jnome.input.ReflectiveClassParser;

public interface ReflectiveInputSourceFactory {
	public LazyReflectiveDocumentLoader create(ReflectiveClassParser parser, String className, Namespace namespace);
}
