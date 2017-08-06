package org.aikodi.java.workspace;

import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.document.Document;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.LazyNamespace;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.namespace.RootNamespace;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.DocumentLoaderImpl;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.LazyDocumentLoader;
import org.aikodi.java.input.ReflectiveClassParser;

public class LazyReflectiveDocumentLoader extends DocumentLoaderImpl implements LazyDocumentLoader {

	public LazyReflectiveDocumentLoader(ClassLoader loader, ReflectiveClassParser parser, String fqn, LazyNamespace ns, DocumentScanner documentLoader) throws InputException {
		init(documentLoader);
		_parser = parser;
		_fqn = fqn;
		_name = Util.getLastPart(fqn);
		_root = (RootNamespace) ns.defaultNamespace();
		_loader = loader;
		setNamespace(ns);
	}
	
	@Override
	public DocumentLoaderImpl clone() {
		try {
			return new LazyReflectiveDocumentLoader(_loader, _parser, _fqn, null, null);
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
	public List<String> refreshTargetDeclarationNames(Namespace ns) {
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
		return (List<Declaration>) (List)rawDocument().lexical().children(NamespaceDeclaration.class).get(0).lexical().children(Type.class);
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
	
	@Override
	protected String resourceName() {
		return "via reflection: "+_fqn;
	}
}
