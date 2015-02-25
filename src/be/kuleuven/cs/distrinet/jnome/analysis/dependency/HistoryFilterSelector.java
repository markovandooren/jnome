package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import org.aikodi.chameleon.analysis.dependency.DependencyAnalysis.HistoryFilter;
import org.aikodi.chameleon.ui.widget.Selector;

public interface HistoryFilterSelector extends Selector {
	
	public HistoryFilter<?, ?> filter();
	
}