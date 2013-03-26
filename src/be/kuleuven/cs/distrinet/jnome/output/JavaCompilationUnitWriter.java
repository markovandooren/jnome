package be.kuleuven.cs.distrinet.jnome.output;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.exception.ModelException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.plugin.build.CompilationUnitWriter;

public class JavaCompilationUnitWriter extends CompilationUnitWriter {


	public JavaCompilationUnitWriter(String extension) {
		super(extension);
	}

	public String fileName(Document compilationUnit) throws LookupException, ModelException {
		Type result = mainType(compilationUnit);
		String name = (result == null ? null : result.name()+extension());
		return name;
	}

	public String packageFQN(Document compilationUnit) throws LookupException, ModelException {
		return mainType(compilationUnit).namespace().getFullyQualifiedName();
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
	public JavaCompilationUnitWriter clone() {
		return new JavaCompilationUnitWriter(extension());
	}
}
