package org.aikodi.java.input;

import org.aikodi.chameleon.core.document.Document;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.RootNamespace;

public interface BytecodeClassParser {

	public Document read(Class clazz, RootNamespace root, Document doc) throws LookupException;

}
