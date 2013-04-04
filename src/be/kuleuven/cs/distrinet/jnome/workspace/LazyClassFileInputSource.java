package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.InputSourceNamespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputSourceImpl;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.parser.ASMClassParser;

public class LazyClassFileInputSource extends InputSourceImpl {

	private ASMClassParser _parser;
	
	public LazyClassFileInputSource(ASMClassParser parser, InputSourceNamespace ns, DocumentLoader loader) throws InputException {
		super(loader);
		if(parser == null) {
			throw new IllegalArgumentException();
		}
		this._parser = parser;
		if(ns == null) {
			throw new IllegalArgumentException();
		}
		setNamespace(ns);
	}
	
	@Override
	public String toString() {
		return _parser.name();
	}

	@Override
	public List<String> targetDeclarationNames(Namespace ns) {
		return Collections.singletonList(_parser.name());
	}

	@Override
	public List<Declaration> targetDeclarations(String name) throws LookupException {
		try {
			load();
		} catch (InputException e) {
			throw new LookupException("Error opening file",e);
		}
		List<Declaration> list = (List)rawDocument().namespaceDeclarations().get(0).declarations();
		List<Declaration> result = new ArrayList<Declaration>();
		for(Declaration decl: list) {
				if(decl.name().equals(name)) {
					result.add(decl);
				}
		}
		return result;
	}

	@Override
	public void doLoad() throws InputException {
		if(! isLoaded()) {
			try {
				Namespace ns = namespace();
				setDocument(_parser.load((Java) ns.language()));
			} catch (Exception e) {
				throw new InputException(e);
			}
		}
	}
	
	
	public ASMClassParser parser() {
		return _parser;
	}

	@Override
	public LazyClassFileInputSource clone() {
		try {
			return new LazyClassFileInputSource(parser(),null,null);
		} catch (InputException e) {
			// Won't be connected, so no exception
			throw new ChameleonProgrammerException(e);
		}
	}

}
