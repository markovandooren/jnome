package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.NamespaceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreePruner;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class NamespaceSelectionPredicateGenerator extends TristateTreePruner<Object, Element> {

	public NamespaceSelectionPredicateGenerator(TristateTreePruner<Object, Element> next) {
		super(next);
	}

	@Override
		protected UniversalPredicate<? super Element, Nothing> grayed(
				TreeNode<?,Object> node, 
				Set<TreeNode<?,Object>> checked, 
				Set<TreeNode<?,Object>> grayed,
				TristateTreePruner<Object,Element>  first) {
		UniversalPredicate<Element, Nothing> result = null;
			if(node instanceof NamespaceNode) {
				// Because namespaces in Java are not hierarchical we combine the current
				// one with the nested ones using an OR operator.
				final Namespace ns = ((NamespaceNode)node).domainObject();
				result = new UniversalPredicate<Element, Nothing>(Element.class) {

					@Override
					public boolean uncheckedEval(Element t) throws Nothing {
						return nearestNamespace(t) == ns;
					}

					private Namespace nearestNamespace(Element t) {
						return t.logical().nearestAncestorOrSelf(t, Namespace.class);
					}
					
					public String toString() {
						return "namespace = "+ns.getFullyQualifiedName();
					}

				}.or(first.childrenDisjunction(node, checked, grayed, first));
			}
			return result;
		}

	@Override
	protected UniversalPredicate<? super Element, Nothing> checked(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed,
			TristateTreePruner<Object,Element>  first) {
			if(node instanceof NamespaceNode) {
				final Namespace currentNamespace = ((NamespaceNode)node).domainObject();
				return new UniversalPredicate<Element, Nothing>(Element.class) {
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

//					@Override
//					public boolean canSucceedBeyond(Element node) {
//						return node == currentNamespace || currentNamespace.hasAncestor(node) || node.hasAncestor(currentNamespace);
//					};

				};
			}
			return null;
		}
	}