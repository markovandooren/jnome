package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DefaultDependencyOptions;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.eclipse.view.dependency.DependencyConfiguration;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.CheckboxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.PredicateSelector;

public class JavaDependencyOptions extends DefaultDependencyOptions {

	@Override
	public DependencyConfiguration createConfiguration() {
		List<PredicateSelector<? super Element>> source = new ArrayList<>();
		List<PredicateSelector<? super Element>> target = new ArrayList<>();
		List<PredicateSelector<? super Element>> cref = new ArrayList<>();
		cref.add(new CheckboxSelector<>(new NoSubtypeReferences(), "Ignore Subtype Relations"));
		target.add(onlySource());
		return new DependencyConfiguration(source, cref,target,Collections.EMPTY_LIST);
	}

}
