package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
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
}