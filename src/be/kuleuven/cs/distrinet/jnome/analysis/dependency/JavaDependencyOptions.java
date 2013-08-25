package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DefaultDependencyOptions;
import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsSource;
import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.eclipse.view.dependency.DependencyConfiguration;
import be.kuleuven.cs.distrinet.chameleon.oo.analysis.dependency.NoSupertypeReferences;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.CheckboxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LexicalTreeContentProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LexicalTreeContentProvider.DocumentLoaderNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LexicalTreeContentProvider.NamespaceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LexicalTreeContentProvider.ProjectNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LexicalTreeContentProvider.SourceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.PredicateSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TreeNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TreeViewNodeLabelProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TristateTreeSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.TristateTreeSelector.TristatePredicateGenerator;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.False;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class JavaDependencyOptions extends DefaultDependencyOptions {

	@Override
	public DependencyConfiguration createConfiguration() {
		List<PredicateSelector<? super Element>> source = new ArrayList<>();
		List<PredicateSelector<? super Element>> target = new ArrayList<>();
		List<PredicateSelector<? super Element>> cref = new ArrayList<>();
		cref.add(new CheckboxSelector<>(new NoSupertypeReferences(), "Ignore Subtype Relations"));
//		target.add(onlySource());
		target.add(sourceSelector());
		return new DependencyConfiguration(source, cref,target,Collections.EMPTY_LIST);
	}

	private PredicateSelector<Element> sourceSelector() {
		LexicalTreeContentProvider contentProvider = new LexicalTreeContentProvider();
		
		TreeViewNodeLabelProvider labelProvider = new TreeViewNodeLabelProvider();
		TristatePredicateGenerator<Object,Element> generator = new TristatePredicateGenerator<Object,Element>() {

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
				UniversalPredicate<? super Element, Nothing> result = new False();
				if(node instanceof NamespaceNode) {
					final Namespace ns = ((NamespaceNode)node).domainObject();
					result = new UniversalPredicate<Element, Nothing>(Element.class) {

						@Override
						public boolean uncheckedEval(Element t) throws Nothing {
							return t.namespace() == ns;
						}
					};
				}
				for(TreeNode<?> child: node.children()) {
					result = result.or((UniversalPredicate)create(child, checked, grayed));
				}
				return result;
			}

			private UniversalPredicate<? super Element, Nothing> checked(TreeNode<?> node, Set<TreeNode<?>> checked, Set<TreeNode<?>> grayed) {
				if(node instanceof SourceNode) {
					return new IsSource();
				} else if (node instanceof DocumentLoaderNode) {
					final DocumentLoader currentLoader = ((DocumentLoaderNode)node).domainObject();
					return new UniversalPredicate<Element, Nothing>(Element.class) {
						@Override
						public boolean uncheckedEval(Element t) throws Nothing {
							DocumentLoader loader = t.nearestAncestor(Document.class).inputSource().loader();
							while(true) {
								if(loader == currentLoader) {
									return true;
								}
								Object o = loader.container();
								if(o instanceof DocumentLoader) {
									loader = (DocumentLoader) o;
								} else {
								  break;
								}
							}
							return false;
						}
					};
				} else if(node instanceof NamespaceNode) {
					final Namespace currentNamespace = ((NamespaceNode)node).domainObject();
					return new UniversalPredicate<Element, Nothing>(Element.class) {
						@Override
						public boolean uncheckedEval(Element t) throws Nothing {
							return t.namespace().ancestors().contains(currentNamespace);
						}
					};
				}
				return new False();
			}
		};
		return new TristateTreeSelector<Object,Element>(contentProvider, generator, labelProvider);
	}
	
}
