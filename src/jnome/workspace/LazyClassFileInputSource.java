package jnome.workspace;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jnome.input.parser.ASMClassParser;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.oo.type.Type;
import chameleon.util.Util;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSource;

public class LazyClassFileInputSource implements InputSource {

	private ASMClassParser _parser;
	
	public LazyClassFileInputSource(ASMClassParser parser) throws LookupException {
		this._parser = parser;
		((InputSourceNamespace)parser.namespace()).addInputSource(this);
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
				_type = _parser.load();
			} catch (LookupException | IOException e) {
				throw new InputException(e);
			}
		}
	}
	
	private Type _type;

}
