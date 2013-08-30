package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.NamespaceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector.TristatePredicateGenerator;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class NamespaceSelectionPredicateGenerator extends TristatePredicateGenerator<Object, Element> {

	public NamespaceSelectionPredicateGenerator(TristatePredicateGenerator<Object, Element> next) {
		super(next);
	}

	@Override
		protected UniversalPredicate<? super Element, Nothing> grayed(
				TreeNode<?,Object> node, 
				Set<TreeNode<?,Object>> checked, 
				Set<TreeNode<?,Object>> grayed,
				TristatePredicateGenerator<Object,Element>  first) {
		UniversalPredicate<? super Element, Nothing> result = null;
			if(node instanceof NamespaceNode) {
				// Because namespaces in Java are not hierarchical we combine the current
				// one with the nested ones using an OR operator.
				final Namespace ns = ((NamespaceNode)node).domainObject();
				result = new UniversalPredicate<Element, Nothing>(Element.class) {

					@Override
					public boolean uncheckedEval(Element t) throws Nothing {
						Namespace namespace = t.logical().nearestAncestorOrSelf(t, Namespace.class);
						return namespace == ns;
					}
					
					public String toString() {
						return "namespace = "+ns.getFullyQualifiedName();
					};
				}.or(first.childrenDisjunction(node, checked, grayed, first));
			}
			return result;
		}

	@Override
	protected UniversalPredicate<? super Element, Nothing> checked(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed,
			TristatePredicateGenerator<Object,Element>  first) {
		UniversalPredicate<? super Element, Nothing> result = null;
			if(node instanceof NamespaceNode) {
				final Namespace currentNamespace = ((NamespaceNode)node).domainObject();
				return new UniversalPredicate<Element, Nothing>(Element.class) {
					@Override
					public boolean uncheckedEval(Element t) throws Nothing {
//						Namespace namespace = t.namespace();
						Namespace namespace = t.logical().nearestAncestorOrSelf(t, Namespace.class);
						return namespace == currentNamespace || namespace.hasAncestor(currentNamespace);
					}
					
					public String toString() {
						return "namespace ancestor = "+currentNamespace.getFullyQualifiedName();
					};

				};
			}
			return result;
		}
	}