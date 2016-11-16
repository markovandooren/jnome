package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import org.aikodi.chameleon.core.document.Document;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.tree.TreePredicate;

public class LoadedBy extends TreePredicate<Element, Nothing> {
	private final DocumentScanner currentLoader;

	public LoadedBy(Class<Element> type, DocumentScanner currentLoader) {
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
				DocumentScanner loader = document.loader().scanner();
				while(true) {
					if(loader == currentLoader) {
						result = true;
					}
					Object o = loader.container();
					if(o instanceof DocumentScanner) {
						loader = (DocumentScanner) o;
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
				DocumentScanner loader = document.loader().scanner();
				while(true) {
					if(loader == currentLoader) {
						result = true;
					}
					Object o = loader.container();
					if(o instanceof DocumentScanner) {
						loader = (DocumentScanner) o;
					} else {
						break;
					}
				}
			}
		}
		return result;
	}
}