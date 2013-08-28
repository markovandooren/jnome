package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsBinary;
import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsSource;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.BinaryNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.DocumentLoaderNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.NamespaceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.SourceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector.TristatePredicateGenerator;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.False;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class ContainerSelectionPredicateGenerator implements TristatePredicateGenerator<Object, Element> {
	@Override
	public UniversalPredicate<? super Element, Nothing> create(
			TreeNode<?> treeNode, Set<TreeNode<?>> checked, Set<TreeNode<?>> grayed) {
		UniversalPredicate<? super Element, Nothing> result;
		if(checked.contains(treeNode)) {
			result = checked(treeNode,checked,grayed);
		} else if(grayed.contains(treeNode)) {
			result = grayed(treeNode,checked,grayed);
		} else {
			result = new False();
		}
		return result;
	}

	private UniversalPredicate<? super Element, Nothing> grayed(TreeNode<?> node, Set<TreeNode<?>> checked, Set<TreeNode<?>> grayed) {
		UniversalPredicate<? super Element, Nothing> result = childrenDisjunction(node, checked, grayed);
		if(node instanceof DocumentLoaderNode) {
			DocumentLoader currentLoader = ((DocumentLoaderNode)node).domainObject();
			result = new LoadedBy(Element.class, currentLoader).and(result);
		} else if(node instanceof NamespaceNode) {
			// Because namespaces in Java are not hierarchical we combine the current
			// one with the nested ones using an OR operator.
			final Namespace ns = ((NamespaceNode)node).domainObject();
			result = new UniversalPredicate<Element, Nothing>(Element.class) {

				@Override
				public boolean uncheckedEval(Element t) throws Nothing {
					return t.namespace() == ns;
				}
			}.or(result);
		}
		return result;
	}

	protected UniversalPredicate<? super Element, Nothing> childrenDisjunction(TreeNode<?> node, Set<TreeNode<?>> checked,
			Set<TreeNode<?>> grayed) {
		UniversalPredicate<? super Element, Nothing> result = new False();
		for(TreeNode<?> child: node.children()) {
			result = result.or((UniversalPredicate)create(child, checked, grayed));
		}
		return result;
	}

	private UniversalPredicate<? super Element, Nothing> checked(TreeNode<?> node, Set<TreeNode<?>> checked, Set<TreeNode<?>> grayed) {
		if(node instanceof SourceNode) {
			return new IsSource();
		} else if(node instanceof BinaryNode) {
			return new IsBinary();
		}
		
		else if (node instanceof DocumentLoaderNode) {
			DocumentLoader currentLoader = ((DocumentLoaderNode)node).domainObject();
			return new LoadedBy(Element.class, currentLoader);
		} else if(node instanceof NamespaceNode) {
			final Namespace currentNamespace = ((NamespaceNode)node).domainObject();
			return new UniversalPredicate<Element, Nothing>(Element.class) {
				@Override
				public boolean uncheckedEval(Element t) throws Nothing {
					Namespace namespace = t.namespace();
					return namespace == currentNamespace || namespace.hasAncestor(currentNamespace);
				}
			};
		}
		return new False();
	}
}