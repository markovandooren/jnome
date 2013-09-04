package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.tree.TreePredicate;

public class LoadedBy extends TreePredicate<Element, Nothing> {
	private final DocumentLoader currentLoader;

	public LoadedBy(Class<Element> type, DocumentLoader currentLoader) {
		super(type);
		this.currentLoader = currentLoader;
	}

	@Override
	public boolean uncheckedEval(Element t) throws Nothing {
		boolean result = false;
		if(t instanceof Namespace) {
			return currentLoader.namespaces().contains(t);
		} else {
			Document document = t.nearestAncestor(Document.class);
			// The signature of a namespace is not part of document.
			if(document != null) {
				DocumentLoader loader = document.inputSource().loader();
				while(true) {
					if(loader == currentLoader) {
						result = true;
					}
					Object o = loader.container();
					if(o instanceof DocumentLoader) {
						loader = (DocumentLoader) o;
					} else {
						break;
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "loaded by "+currentLoader.label();
	}

	@Override
	public boolean canSucceedBeyond(Element node) throws Nothing {
		boolean result = false;
		if(node instanceof Namespace) {
			for(Namespace ns: currentLoader.namespaces()) {
				if(ns == node || ns.hasAncestor(node)) {
					result = true;
					break;
				}
			}
		} else {
			Document document = node.nearestAncestor(Document.class);
			// The signature of a namespace is not part of document.
			if(document != null) {
				DocumentLoader loader = document.inputSource().loader();
				while(true) {
					if(loader == currentLoader) {
						result = true;
					}
					Object o = loader.container();
					if(o instanceof DocumentLoader) {
						loader = (DocumentLoader) o;
					} else {
						break;
					}
				}
			}
		}
		return result;
	}
}