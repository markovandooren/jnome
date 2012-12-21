package jnome.workspace;

import java.util.Collections;
import java.util.List;

import jnome.input.ReflectiveClassParser;
import chameleon.core.declaration.Declaration;
import chameleon.core.document.Document;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.LazyNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.Type;
import chameleon.util.Util;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSourceImpl;

public class LazyReflectiveInputSource extends InputSourceImpl {

	public LazyReflectiveInputSource(ClassLoader loader, ReflectiveClassParser parser, String fqn, LazyNamespace ns) throws InputException {
		_parser = parser;
		_fqn = fqn;
		_name = Util.getLastPart(fqn);
		_root = (RootNamespace) ns.defaultNamespace();
		_loader = loader;
		setNamespace(ns);
	}
	
	@Override
	public InputSourceImpl clone() {
		try {
			return new LazyReflectiveInputSource(_loader, _parser, _fqn, null);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
	}
	
	private RootNamespace _root;
	
	public ReflectiveClassParser parser() {
		return _parser;
	}
	
	private ReflectiveClassParser _parser;
	
	private String _name;
	
	private String _fqn;
	
	@Override
	public List<String> targetDeclarationNames(Namespace ns) {
		return Collections.singletonList(_name);
	}


	@Override
	public List<Declaration> targetDeclarations(String name) throws LookupException {
		try {
			load();
		} catch (InputException e) {
			throw new LookupException("Error opening file",e);
		}
		// Since we load a class file, there is only 1 top-level declaration: the class or interface defined in the class file.
		// Other top-level classes or interface in the same source file must be package accessible and are stored in their own
		// class files.
		return (List<Declaration>) (List)rawDocument().children(NamespaceDeclaration.class).get(0).children(Type.class);
	}

	@Override
	protected void doLoad() throws InputException {
		Class clazz;
		try {
//			clazz = Class.forName(_fqn, true, _loader);
			setDocument(new Document());
			clazz = _loader.loadClass(_fqn);
			parser().read(clazz, _root, rawDocument());
		} catch (Exception e) {
			throw new InputException(e);
		}
	}

	private ClassLoader _loader;
	
}
