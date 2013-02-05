package jnome.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.rejuse.predicate.SafePredicate;

import jnome.input.parser.ASMClassParser;
import chameleon.core.language.Language;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.util.Pair;
import chameleon.util.Util;
import chameleon.workspace.AbstractZipLoader;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSource;

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
