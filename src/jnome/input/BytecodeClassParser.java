package jnome.input;

import chameleon.core.document.Document;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.RootNamespace;

public interface BytecodeClassParser {

	public Document read(Class clazz, RootNamespace root, Document doc) throws LookupException;

}