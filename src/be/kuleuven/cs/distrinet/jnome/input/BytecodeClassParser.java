package be.kuleuven.cs.distrinet.jnome.input;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;

public interface BytecodeClassParser {

	public Document read(Class clazz, RootNamespace root, Document doc) throws LookupException;

}
