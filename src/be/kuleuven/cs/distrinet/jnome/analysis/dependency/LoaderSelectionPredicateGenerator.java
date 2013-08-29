package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsBinary;
import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsSource;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.DocumentLoaderContentProvider.BinaryNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.DocumentLoaderContentProvider.LoaderGroupNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.DocumentLoaderContentProvider.ProjectNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.DocumentLoaderContentProvider.SourceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.DocumentLoaderNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector.TristatePredicateGenerator;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class LoaderSelectionPredicateGenerator extends TristatePredicateGenerator<Object, Element> {
	
	public LoaderSelectionPredicateGenerator(TristatePredicateGenerator<Object, Element> next) {
		super(next);
	}

	@Override
	protected UniversalPredicate<? super Element, Nothing> grayed(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed, 
			TristatePredicateGenerator<Object,Element>  first) {
		UniversalPredicate<? super Element, Nothing> result = null;
		if(node instanceof ProjectNode | node instanceof LoaderGroupNode) {
			result = first.childrenDisjunction(node, checked, grayed, first);
		} else if(node instanceof DocumentLoaderNode) {
			DocumentLoader currentLoader = ((DocumentLoaderNode)node).domainObject();
			result = new LoadedBy(Element.class, currentLoader).and(first.childrenDisjunction(node, checked, grayed, first));
		} 
		return result;
	}

	@Override
	protected UniversalPredicate<? super Element, Nothing> checked(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed, 
			TristatePredicateGenerator<Object,Element>  first) {
		if(node instanceof SourceNode) {
			return new IsSource();
		} else if(node instanceof BinaryNode) {
			return new IsBinary();
		} else if (node instanceof DocumentLoaderNode) {
			DocumentLoader currentLoader = ((DocumentLoaderNode)node).domainObject();
			return new LoadedBy(Element.class, currentLoader);
		} 
		return null;
	}
}