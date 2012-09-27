package jnome.workspace;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jnome.core.language.Java;
import jnome.input.parser.ASMClassParser;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.oo.type.Type;
import chameleon.util.Util;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSourceImpl;

public class LazyClassFileInputSource extends InputSourceImpl {

	private ASMClassParser _parser;
	
	public LazyClassFileInputSource(ASMClassParser parser, InputSourceNamespace ns) {
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
		return Util.createNonNullList(_type);

	}

	@Override
	public void load() throws InputException {
		if(_type == null) {
			try {
				Namespace ns = namespace();
				_type = _parser.load((Java) ns.language());
			} catch (LookupException | IOException e) {
				throw new InputException(e);
			}
		}
	}
	
	private Type _type;
	
	public ASMClassParser parser() {
		return _parser;
	}

	@Override
	public LazyClassFileInputSource clone() {
		LazyClassFileInputSource result = new LazyClassFileInputSource(parser(),null);
		return result;
	}

}
