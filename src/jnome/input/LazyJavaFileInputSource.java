package jnome.input;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.oo.type.Type;
import chameleon.util.Util;
import chameleon.workspace.FileInputSource;

public class LazyJavaFileInputSource extends FileInputSource {

	
	public LazyJavaFileInputSource(File file, ModelFactory factory) {
		super(file,factory);
	}
	
	@Override
	public List<String> declarationNames() {
		return Collections.singletonList(Util.getAllButLastPart(file().getName()));
	}
	
	@Override
	public List<Declaration> declarations(String name) throws LookupException {
		try {
			load();
		} catch (IOException e) {
			throw new LookupException("Error opening file",e);
		} catch (ParseException e) {
			throw new LookupException("Error parsing file",e);
		}
		return (List)document().children(NamespaceDeclaration.class).get(1).children(Type.class);
	}

}
