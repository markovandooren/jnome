package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.Set;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.ui.widget.tree.NamespaceNode;
import org.aikodi.chameleon.ui.widget.tree.TreeNode;
import org.aikodi.chameleon.ui.widget.tree.TristateTreePruner;

import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;
import be.kuleuven.cs.distrinet.rejuse.tree.TreePredicate;

public class NamespaceSelectionPredicateGenerator extends TristateTreePruner<Object, Element> {

	public NamespaceSelectionPredicateGenerator(TristateTreePruner<Object, Element> next) {
		super(next);
	}

	@Override
	protected TreePredicate<? super Element, Nothing> grayed(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed,
			TristateTreePruner<Object,Element>  first) {
		TreePredicate<? super Element, Nothing> result = null;
		if(node instanceof NamespaceNode) {
			// Because namespaces in Java are not hierarchical we combine the current
			// one with the nested ones using an OR operator.
			final Namespace currentNamespace = ((NamespaceNode)node).domainObject();
			TreePredicate<? super Element, Nothing> childrenDisjunction = first.childrenDisjunction(node, checked, grayed, first);
			result = new TreePredicate<Element, Nothing>(Element.class) {

				@Override
				public boolean uncheckedEval(Element t) throws Nothing {
					return nearestNamespace(t) == currentNamespace;
				}

				private Namespace nearestNamespace(Element t) {
					return t.logical().nearestAncestorOrSelf(t, Namespace.class);
				}

				@Override
				public boolean canSucceedBeyond(Element node) {
					return node == currentNamespace || currentNamespace.hasAncestor(node) || node.hasAncestor(currentNamespace);
				};


				public String toString() {
					return "namespace = "+currentNamespace.getFullyQualifiedName();
				}

			}.orTree((TreePredicate)childrenDisjunction);
		}
		return result;
	}

	@Override
	protected TreePredicate<? super Element, Nothing> checked(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed,
			TristateTreePruner<Object,Element>  first) {
		if(node instanceof NamespaceNode) {
			final Namespace currentNamespace = ((NamespaceNode)node).domainObject();
			return new TreePredicate<Element, Nothing>(Element.class) {
				@Override
				public boolean uncheckedEval(Element t) throws Nothing {
					Namespace namespace = nearestNamespace(t);
					return namespace == currentNamespace || namespace.hasAncestor(currentNamespace);
				}

				private Namespace nearestNamespace(Element t) {
					return t.logical().nearestAncestorOrSelf(t, Namespace.class);
				}

				public String toString() {
					return "namespace ancestor = "+currentNamespace.getFullyQualifiedName();
				}

				@Override
				public boolean canSucceedBeyond(Element node) {
					return node == currentNamespace || currentNamespace.hasAncestor(node) || node.logical().hasAncestorOrSelf(node,currentNamespace);
				};

			};
		}
		return null;
	}
}