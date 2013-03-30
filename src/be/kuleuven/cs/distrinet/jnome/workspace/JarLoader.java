package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.jnome.input.parser.ASMClassParser;
import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.InputSourceNamespace;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.AbstractZipLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.FileLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputSource;

public class JarLoader extends AbstractZipLoader {

	
	/**
	 * Create a new jar loader for the jar with the given path, file filter, and base loader setting.
	 * 
	 * @param path The path of the jar file from which elements must be loaded.
	 * @param filter A filter that selects files in the zip file based on their paths.
	 * @param isBaseLoader Indicates whether the loader is responsible for loading a base library.
	 */
 /*@
   @ public behavior
   @
   @ pre path != null;
   @ post path() == path;
   @ post isBaseLoader() == isBaseLoader;
   @*/
	public JarLoader(String path, SafePredicate<? super String> filter, boolean isBaseLoader) {
		super(path, filter, isBaseLoader);
	}

	/**
	 * Create a new jar loader for the jar with the given path, file filter. The new loader will
	 * not be responsible for loading a base library.
	 * 
	 * @param path The path of the jar file from which elements must be loaded.
	 * @param filter A filter that selects files in the zip file based on their paths.
	 */
 /*@
   @ public behavior
   @
   @ pre path != null;
   @ post path() == path;
   @ post isBaseLoader() == false;
   @*/
	public JarLoader(String path, SafePredicate<? super String> filter) {
		this(path, filter, false);
	}
	
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
				createInputSource(parser, ns);
			}
  	}
	}

	protected void createInputSource(ASMClassParser parser, InputSourceNamespace ns) throws InputException {
		LazyClassFileInputSource result = new LazyClassFileInputSource(parser,ns,this);
		addInputSource(result);
	}

	protected Language language() {
		return view().language();
	}
	
	@Override
	public int compareTo(DocumentLoader o) {
		//FIXME Just a hack for now, need proper support for a classpath.
		//      e.g. put the document loaders in a path like structure.
		int result = super.compareTo(o);
		if(result == 0) {
			result = o instanceof FileLoader ? -1 : 0; 
		}
		return result;
	}
}
