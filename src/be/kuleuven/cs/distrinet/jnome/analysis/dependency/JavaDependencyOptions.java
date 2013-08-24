package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DefaultDependencyOptions;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.eclipse.view.dependency.DependencyConfiguration;
import be.kuleuven.cs.distrinet.chameleon.oo.analysis.dependency.NoSupertypeReferences;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.CheckboxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LexicalTreeContentProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.PredicateSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TreeViewNodeLabelProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TreeViewerNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TristateTreeSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TristateTreeSelector.TristatePredicateGenerator;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class JavaDependencyOptions extends DefaultDependencyOptions {

	@Override
	public DependencyConfiguration createConfiguration() {
		List<PredicateSelector<? super Element>> source = new ArrayList<>();
		List<PredicateSelector<? super Element>> target = new ArrayList<>();
		List<PredicateSelector<? super Element>> cref = new ArrayList<>();
		cref.add(new CheckboxSelector<>(new NoSupertypeReferences(), "Ignore Subtype Relations"));
		target.add(onlySource());
		target.add(sourceSelector());
		return new DependencyConfiguration(source, cref,target,Collections.EMPTY_LIST);
	}

	private PredicateSelector<Element> sourceSelector() {
		LexicalTreeContentProvider contentProvider = new LexicalTreeContentProvider();
		
		TreeViewNodeLabelProvider labelProvider = new TreeViewNodeLabelProvider();
		TristatePredicateGenerator<Element> generator = new TristatePredicateGenerator<Element>() {

			@Override
			public UniversalPredicate<? super Element, Nothing> create(
					Object domainObject, boolean checked, boolean grayed) {
				return new True();
			}
		};
		return new TristateTreeSelector<TreeViewerNode,Object,Element>(contentProvider, generator, labelProvider);
	}
	
}
