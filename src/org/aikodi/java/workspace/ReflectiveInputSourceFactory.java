package org.aikodi.java.workspace;

import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.java.input.ReflectiveClassParser;

public interface ReflectiveInputSourceFactory {
	public LazyReflectiveDocumentLoader create(ReflectiveClassParser parser, String className, Namespace namespace);
}
