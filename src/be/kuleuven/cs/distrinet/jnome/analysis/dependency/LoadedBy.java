package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class LoadedBy extends UniversalPredicate<Element, Nothing> {
	private final DocumentLoader currentLoader;

	public LoadedBy(Class<Element> type, DocumentLoader currentLoader) {
		super(type);
		this.currentLoader = currentLoader;
	}

	@Override
	public boolean uncheckedEval(Element t) throws Nothing {
		if(t instanceof Namespace) {
			return currentLoader.namespaces().contains(t); 
		}
		DocumentLoader loader = t.nearestAncestor(Document.class).inputSource().loader();
		while(true) {
			if(loader == currentLoader) {
				return true;
			}
			Object o = loader.container();
			if(o instanceof DocumentLoader) {
				loader = (DocumentLoader) o;
			} else {
			  break;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "loaded by "+currentLoader.label();
	}
}