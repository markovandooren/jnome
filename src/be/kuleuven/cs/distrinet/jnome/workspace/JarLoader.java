package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.core.namespace.DocumentLoaderNamespace;
import org.aikodi.chameleon.util.Pair;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.AbstractZipScanner;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.FileScanner;
import org.aikodi.chameleon.workspace.InputException;

import be.kuleuven.cs.distrinet.jnome.input.parser.ASMClassParser;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

public class JarLoader extends AbstractZipScanner {

	
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
	public JarLoader(JarFile path, Predicate<? super String,Nothing> filter, boolean isBaseLoader) {
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
	public JarLoader(JarFile path, Predicate<? super String,Nothing> filter) {
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
				DocumentLoaderNamespace ns = (DocumentLoaderNamespace) view().namespace().getOrCreateNamespace(packageFQN);
				createInputSource(parser, ns);
			}
  	}
	}

	protected void createInputSource(ASMClassParser parser, DocumentLoaderNamespace ns) throws InputException {
		LazyClassFileDocumentLoader result = new LazyClassFileDocumentLoader(parser,ns,this);
		add(result);
	}

	protected Language language() {
		return view().language();
	}
	
	@Override
	public int compareTo(DocumentScanner o) {
		//FIXME Just a hack for now, need proper support for a classpath.
		//      e.g. put the document loaders in a path like structure.
		int result = super.compareTo(o);
		if(result == 0) {
			result = o instanceof FileScanner ? 1 : 0; 
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "Jar loader: "+file().getName();
	}
}
