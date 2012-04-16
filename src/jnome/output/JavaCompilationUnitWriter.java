package jnome.output;

import java.io.File;

import chameleon.core.document.Document;
import chameleon.core.lookup.LookupException;
import chameleon.exception.ModelException;
import chameleon.oo.type.Type;
import chameleon.plugin.Plugin;
import chameleon.plugin.build.CompilationUnitWriter;

public class JavaCompilationUnitWriter extends CompilationUnitWriter {


	public JavaCompilationUnitWriter(File outputDir, String extension) {
		super(outputDir,extension);
	}

	public String fileName(Document compilationUnit) throws LookupException, ModelException {
		Type result = mainType(compilationUnit);
		String name = (result == null ? null : result.getName()+extension());
		return name;
	}

	public String packageFQN(Document compilationUnit) throws LookupException, ModelException {
		return mainType(compilationUnit).getNamespace().getFullyQualifiedName();
	}
	
	private Type mainType(Document compilationUnit) throws LookupException, ModelException {
		Type result = null;
		for(Type type: compilationUnit.descendants(Type.class)) {
			if((type.nearestAncestor(Type.class) == null) && ((result == null) || (type.scope().ge(result.scope())))) {
				result = type;
			}
		}
		return result;
	}

	@Override
	public Plugin clone() {
		return new JavaCompilationUnitWriter(outputDir(), extension());
	}
}
