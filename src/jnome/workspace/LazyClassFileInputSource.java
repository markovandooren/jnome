package jnome.workspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jnome.core.language.Java;
import jnome.input.parser.ASMClassParser;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSourceImpl;

public class LazyClassFileInputSource extends InputSourceImpl {

	private ASMClassParser _parser;
	
	public LazyClassFileInputSource(ASMClassParser parser, InputSourceNamespace ns) throws InputException {
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
		List<Declaration> list = (List)rawDocument().namespaceParts().get(0).declarations();
		List<Declaration> result = new ArrayList<>();
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
			} catch (LookupException | IOException e) {
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
			return new LazyClassFileInputSource(parser(),null);
		} catch (InputException e) {
			// Won't be connected, so no exception
			throw new ChameleonProgrammerException(e);
		}
	}

}
