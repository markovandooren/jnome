package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyNamespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputSourceImpl;
import be.kuleuven.cs.distrinet.jnome.input.ReflectiveClassParser;

public class LazyReflectiveInputSource extends InputSourceImpl {

	public LazyReflectiveInputSource(ClassLoader loader, ReflectiveClassParser parser, String fqn, LazyNamespace ns, DocumentLoader documentLoader) throws InputException {
		init(documentLoader);
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
			return new LazyReflectiveInputSource(_loader, _parser, _fqn, null, null);
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
	protected void doRefresh() throws InputException {
		Class clazz;
		try {
			setDocument(new Document());
			clazz = _loader.loadClass(_fqn);
			parser().read(clazz, _root, rawDocument());
		} catch (Exception e) {
			throw new InputException(e);
		}
	}

	private ClassLoader _loader;
	
}
