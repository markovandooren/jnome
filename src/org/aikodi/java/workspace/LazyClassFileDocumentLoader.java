package org.aikodi.java.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.DocumentLoaderNamespace;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.workspace.DocumentLoaderImpl;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.LazyDocumentLoader;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.input.parser.ASMClassParser;

public class LazyClassFileDocumentLoader extends DocumentLoaderImpl implements LazyDocumentLoader {

	private ASMClassParser _parser;
	
	public LazyClassFileDocumentLoader(ASMClassParser parser, DocumentLoaderNamespace ns, DocumentScanner loader) throws InputException {
		if(parser == null) {
			throw new IllegalArgumentException();
		}
		this._parser = parser;
		if(ns == null) {
			throw new IllegalArgumentException();
		}
		init(ns,loader);
//		setNamespace(ns);
	}
	
	@Override
	public String toString() {
		return _parser.name();
	}

  @Override
  public List<String> refreshTargetDeclarationNames(Namespace ns) {
    return Collections.singletonList(_parser.name());
  }

	@Override
	public List<Declaration> targetDeclarations(String name) throws LookupException {
		try {
			load();
		} catch (InputException e) {
			throw new LookupException("Error opening file",e);
		}
		List<Declaration> list = (List)rawDocument().children(NamespaceDeclaration.class).get(0).declarations();
		List<Declaration> result = new ArrayList<Declaration>();
		for(Declaration decl: list) {
				if(decl.name().equals(name)) {
					result.add(decl);
				}
		}
		return result;
	}

	@Override
	public void doRefresh() throws InputException {
		try {
			Namespace ns = namespace();
			setDocument(_parser.load((Java7) ns.language()));
		} catch (Exception e) {
			throw new InputException(e);
		}
	}
	
	
	public ASMClassParser parser() {
		return _parser;
	}

	@Override
	public LazyClassFileDocumentLoader clone() {
		try {
			return new LazyClassFileDocumentLoader(parser(),null,null);
		} catch (InputException e) {
			// Won't be connected, so no exception
			throw new ChameleonProgrammerException(e);
		}
	}

	@Override
	protected String resourceName() {
		return _parser.resourceName();
	}
}
