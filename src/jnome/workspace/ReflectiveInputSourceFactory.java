package jnome.workspace;

import jnome.input.ReflectiveClassParser;
import chameleon.core.namespace.Namespace;

public interface ReflectiveInputSourceFactory {
	public LazyReflectiveInputSource create(ReflectiveClassParser parser, String className, Namespace namespace);
}