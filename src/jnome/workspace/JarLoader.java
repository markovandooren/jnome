package jnome.workspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jnome.input.parser.ASMClassParser;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.util.Pair;
import chameleon.util.Util;
import chameleon.workspace.InputException;
import chameleon.workspace.ProjectException;
import chameleon.workspace.View;

public class JarLoader extends AbstractJarLoader {

	public JarLoader(String path) {
		super(path);
	}
	
	@Override
	protected void notifyProjectAdded(View view) throws ProjectException {
		try {
			createInputSources();
		} catch (Exception e) {
			throw new ProjectException(e);
		}
	}
	
	protected String fileExtension() {
		return ".class";
	}
	
	protected void createInputSources() throws IOException, LookupException, InputException {
		JarFile jar = createJarFile();
  	Enumeration<JarEntry> entries = jar.entries();
  	List<Pair<Pair<String,String>, JarEntry>> names = new ArrayList<Pair<Pair<String,String>, JarEntry>>();
  	while(entries.hasMoreElements()) {
  		JarEntry entry = entries.nextElement();
  		String name = entry.getName();
  		if(name.endsWith(fileExtension())) {
  			String tmp = Util.getAllButLastPart(name).replace('/', '.').replace('$', '.');
  			if(! tmp.matches(".*\\.[0-9].*")) {
  				names.add(new Pair<Pair<String,String>, JarEntry>(new Pair<String,String>(tmp,Util.getLastPart(Util.getAllButLastPart(name).replace('/', '.')).replace('$', '.')), entry));
  			}
  		}
  	}
  	// The entries must be sorted first such that if an inner class in processed, its outer
  	// class will have been processed first.
  	// TODO Explain why this is actually necessary. Not even sure it is needed anymore now that we have lazy
  	// loading. With eager loading it would try to add the inner class to the outer when the outer wasn't
  	// loaded yet.
  	Collections.sort(names, new Comparator<Pair<Pair<String,String>,JarEntry>>(){
			@Override
			public int compare(Pair<Pair<String,String>, JarEntry> o1, Pair<Pair<String,String>, JarEntry> o2) {
				int first = o1.first().first().length();
				int second = o2.first().first().length();
				return first - second;
			}
  	});
  	Map<String, ASMClassParser> map = new HashMap<String, ASMClassParser>();
  	for(Pair<Pair<String,String>,JarEntry> pair: names) {
  		JarEntry entry = pair.second();
  		String qn = pair.first().second();
			String name = Util.getLastPart(qn);
			String packageFQN = packageFQN(entry.getName());
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
				LazyClassFileInputSource source = new LazyClassFileInputSource(parser,ns);
				addInputSource(source);
			}
  	}
	}

	protected Language language() {
		return view().language();
	}
	
	private String packageFQN(String entryName) {
		return Util.getAllButLastPart(Util.getAllButLastPart(entryName).replace('/', '.'));
	}
}
