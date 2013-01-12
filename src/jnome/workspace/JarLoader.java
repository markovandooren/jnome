package jnome.workspace;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jnome.input.parser.ASMClassParser;
import chameleon.core.language.Language;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.util.Pair;
import chameleon.util.Util;
import chameleon.workspace.AbstractZipLoader;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSource;

public class JarLoader extends AbstractZipLoader {

	
	public JarLoader(String path) {
		super(path);
		addFileExtension(".class");
	}
	
//	/**
//	 * Create a {@link JarFile} object that represents the jar file
//	 * from which elements must be loaded. If the path is not absolute
//	 * it is interpreted as a path relative to the project root.
//	 * 
//	 * @throws IOException When the path is not a valid path for a jar file.
//	 */
// /*@
//   @ public behavior
//   @
//   @ post \result != null;
//   @ post \result.getAbsolutePath().equals(project().absolutePath(path()));
//   @*/
//	protected JarFile createJarFile() throws IOException {
//		return new JarFile(project().absolutePath(path()));
//	}

	
	@Override
	protected void processMap(ZipFile jar, List<Pair<Pair<String, String>, ZipEntry>> names) throws InputException {
		Map<String, ASMClassParser> map = new HashMap<String, ASMClassParser>();
  	for(Pair<Pair<String,String>,ZipEntry> pair: names) {
  		ZipEntry entry = pair.second();
  		String qn = pair.first().second();
			String name = Util.getLastPart(qn);
			String packageFQN = namespaceFQN(entry.getName());
			ASMClassParser parser = new ASMClassParser(jar,entry, name, packageFQN);
			String second = Util.getAllButFirstPart(qn);
			String key = (packageFQN == null ? name : packageFQN+"."+Util.getFirstPart(qn));
			if(second != null) {
				ASMClassParser asmClassParser = map.get(key);
				// Deal with bad jars that contain class files of inner classes but not the outer class
				// e.g. the OS X rt.jar.
				if(asmClassParser != null) {
					asmClassParser.add(parser, second);
				}
			} else {
				map.put(key, parser);
				InputSourceNamespace ns = (InputSourceNamespace) view().namespace().getOrCreateNamespace(packageFQN);
				InputSource source = createInputSource(parser, ns);
				addInputSource(source);
			}
  	}
	}

	protected LazyClassFileInputSource createInputSource(ASMClassParser parser, InputSourceNamespace ns) throws InputException {
		return new LazyClassFileInputSource(parser,ns);
	}

	protected Language language() {
		return view().language();
	}
	}
