package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalysis.HistoryFilter;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.Selector;

public interface HistoryFilterSelector extends Selector {
	
	public HistoryFilter<?, ?> filter();
	
}