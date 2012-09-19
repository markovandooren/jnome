package jnome.input;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.input.ModelFactory;
import chameleon.oo.type.Type;
import chameleon.util.Util;
import chameleon.workspace.FileInputSource;
import chameleon.workspace.InputException;

public abstract class JavaFileInputSource extends FileInputSource {

	public JavaFileInputSource(File file, ModelFactory factory) {
		super(file, factory);
	}
	
	@Override
	public List<Declaration> targetDeclarations(String name) throws LookupException {
		try {
			load();
		} catch (InputException e) {
			throw new LookupException("Error opening file",e);
		}
		List<Type> children = (List)document().children(NamespaceDeclaration.class).get(0).children(Type.class);
		List<Declaration> result = new ArrayList<Declaration>(1);
		for(Type t: children) {
			if(t.name().equals(name)) {
				result.add(t);
			}
		}
		return result;
	}
	
	@Override
	public List<String> targetDeclarationNames(Namespace ns) {
		return Collections.singletonList(Util.getAllButLastPart(file().getName()));
	}


}