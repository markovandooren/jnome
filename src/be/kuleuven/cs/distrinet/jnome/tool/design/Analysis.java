package be.kuleuven.cs.distrinet.jnome.tool.design;

import be.kuleuven.cs.distrinet.chameleon.core.analysis.AnalysisResult;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.util.concurrent.SafeAction;

public abstract class Analysis<E extends Element> extends SafeAction<E> {

//	public Analysis() {
//		if(kind == null) {
//			throw new IllegalArgumentException("The given kind is null");
//		}
//		_kind = kind;
//	}
//	
//	private Class<E> _kind;
	
	@Override
	protected final void actuallyPerform(E t) {
		analyse(t);
	}
	
	/**
	 * Perform the analysis on the given element.
	 * 
	 * @param element The element to be analyzed.
	 */
	protected abstract void analyse(E e);
	
	/**
	 * 
	 * @return
	 */
	public abstract AnalysisResult result();

}
