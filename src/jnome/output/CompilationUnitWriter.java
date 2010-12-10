package jnome.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.exception.ModelException;
import chameleon.oo.type.Type;
import chameleon.plugin.output.Syntax;
import chameleon.test.provider.ElementProvider;

public class CompilationUnitWriter {

	String _extension;

	public CompilationUnitWriter(File outputDir, String extension) {
		_outputDir = outputDir;
		_extension = extension;
	}

	public String outputDirName() {
		return outputDir().getAbsolutePath();
	}
	
	public File outputDir() {
		return _outputDir;
	}
	
	private File _outputDir;
	
	public File write(CompilationUnit cu) throws LookupException, ModelException, IOException {
		Syntax writer = cu.language().plugin(Syntax.class);
		if(writer != null) {
			String fileName = fileName(cu);
			if(fileName != null) {
				String packageFQN = packageFQN(cu);
				String relDirName = packageFQN.replace('.', File.separatorChar);
				File out = new File(outputDirName()+File.separatorChar + relDirName + File.separatorChar + fileName);
				File parent = out.getParentFile();
				parent.mkdirs();
				out.createNewFile();
				FileWriter fw = new FileWriter(out);
				fw.write(writer.toCode(cu));
				fw.close();
				System.out.println("Wrote: "+out.getAbsolutePath());
				return out;
			}
		} 
		return null;
	}
	
	public String fileName(CompilationUnit compilationUnit) throws LookupException, ModelException {
		Type result = mainType(compilationUnit);
		String name = (result == null ? null : result.getName()+_extension);
		return name;
	}

	public String packageFQN(CompilationUnit compilationUnit) throws LookupException, ModelException {
		return mainType(compilationUnit).getNamespace().getFullyQualifiedName();
	}
	
	private Type mainType(CompilationUnit compilationUnit) throws LookupException, ModelException {
		Type result = null;
		for(Type type: compilationUnit.descendants(Type.class)) {
			if((type.nearestAncestor(Type.class) == null) && ((result == null) || (type.scope().ge(result.scope())))) {
				result = type;
			}
		}
		return result;
	}
}
