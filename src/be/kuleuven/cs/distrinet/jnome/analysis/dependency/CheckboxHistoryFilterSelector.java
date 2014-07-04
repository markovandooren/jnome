package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalysis;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalysis.HistoryFilter;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.CheckboxSelector;

public class CheckboxHistoryFilterSelector extends CheckboxSelector implements HistoryFilterSelector {

	public CheckboxHistoryFilterSelector(String message, boolean initialValue, HistoryFilter<?, ?> filter) {
		super(message, initialValue);
		if(filter == null) {
			throw new IllegalArgumentException();
		}
		_filter = filter;
	}

	private HistoryFilter<?, ?> _filter;
	
	@Override
	public HistoryFilter<?, ?> filter() {
		if(selected()) {
			return _filter;
		} else {
			return new DependencyAnalysis.NOOP();
		}
	}

}
