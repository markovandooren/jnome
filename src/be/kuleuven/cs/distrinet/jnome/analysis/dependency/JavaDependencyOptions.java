package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.AbstractAnalysisOptions;
import be.kuleuven.cs.distrinet.chameleon.analysis.OptionGroup;
import be.kuleuven.cs.distrinet.chameleon.analysis.PredicateOptionGroup;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.Dependency;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalysis;
import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsBinary;
import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsSource;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.analysis.dependency.NoSupertypeReferences;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalParameterType;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LabelProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.PredicateSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.checkbox.CheckboxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.list.ComboBoxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.list.ListContentProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.BinaryNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.DocumentLoaderNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.NamespaceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.LexicalTreeContentProvider.SourceNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeNode;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeViewNodeLabelProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector.TristatePredicateGenerator;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.function.Function;
import be.kuleuven.cs.distrinet.rejuse.predicate.False;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;
import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

import com.google.common.collect.ImmutableList;

public class JavaDependencyOptions extends AbstractAnalysisOptions {

	public JavaDependencyOptions() {
		_groups = ImmutableList.of(_target,_source,_crossReferences,_dependencies);
	}
	
	@Override
	public List<? extends OptionGroup> optionGroups() {
		return _groups;
	}
	
	private UniversalPredicate predicate(List<PredicateSelector<?>> selectors) {
		UniversalPredicate result = new True();
		for(PredicateSelector selector: selectors) {
			result = result.and(selector.predicate());
		}
		return result;
	}
	
	@Override
	public DependencyAnalysis<Element,Declaration> createAnalysis() {
		UniversalPredicate sourcePredicate = _source.predicate();
		UniversalPredicate crossReferencePredicate = _crossReferences.predicate();
		UniversalPredicate targetPredicate = _target.predicate();
		UniversalPredicate dependencyPredicate = _dependencies.predicate();
		return new DependencyAnalysis<Element,Declaration>(
				Declaration.class,
				sourcePredicate, 
				crossReferencePredicate,
				Declaration.class,
				mapper(), 
				targetPredicate, 
				dependencyPredicate);
	}
	
	List<? extends OptionGroup> _groups;

	private SourceOptionGroup _source = new SourceOptionGroup();
	private class SourceOptionGroup extends PredicateOptionGroup {
		public SourceOptionGroup() {
			super("Source");
			addPredicateSelector(declarationTypeSelector());
		}
	}

	private TargetOptionGroup _target = new TargetOptionGroup();
	private class TargetOptionGroup extends PredicateOptionGroup {
		public TargetOptionGroup() {
			super("Target");
			addPredicateSelector(declarationTypeSelector());
			addPredicateSelector(sourceSelector());
		}
	}
	
	private CrossReferenceOptionGroup _crossReferences = new CrossReferenceOptionGroup();
	private class CrossReferenceOptionGroup extends PredicateOptionGroup {
		public CrossReferenceOptionGroup() {
			super("Cross-references");
			addPredicateSelector(new CheckboxSelector<>(new NoSupertypeReferences(), "Ignore Subtype Relations",true));
		}
	}	

	private DependencyOptionGroup _dependencies = new DependencyOptionGroup();
	private class DependencyOptionGroup extends PredicateOptionGroup {
		public DependencyOptionGroup() {
			super("Dependency");
			addPredicateSelector(noDescendants());
		}
	}	
	
	
	
//	@Override
//	public DependencyOptions createConfiguration() {
//		
//		
//		List<PredicateSelector<? super Element>> cref = new ArrayList<>();
//		cref.add();
//
//		List<PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>>> dependency = new ArrayList<>();
//		dependency.add();
//		return new DependencyOptions(source, cref,target,dependency,mapper());
//	}
	
	private Function mapper() {
		return new Function<Element,Element,Nothing> (){

			@Override
			public Element apply(Element type) {
				while(type instanceof ArrayType) {
					type = ((ArrayType)type).elementType();
				}
				while(type instanceof DerivedType) {
					type = ((DerivedType)type).baseType();
				}
				while(type instanceof FormalParameterType) {
					type = type.nearestAncestor(Type.class);
				}
				if(type instanceof Type) {
					AnonymousType anon = type.farthestAncestorOrSelf(AnonymousType.class);
					if(anon != null) {
						type = anon.nearestAncestor(Type.class);
					}
				}
				return type;
			}
		};
	}

	
	private PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>> noDescendants() {
		return new CheckboxSelector<Dependency<?,?,?>>(
				
				new UniversalPredicate<Dependency, Nothing>(Dependency.class) {

			@Override
			public boolean uncheckedEval(Dependency t) throws Nothing {
				return ! ((Element) t.target()).hasAncestor((Element)t.source());
			}
		}, "Ignore lexical descendants",true);
	}
	
	private PredicateSelector<Element> declarationTypeSelector() {
		ListContentProvider<Class> contentProvider = new ListContentProvider<Class>() {

			@Override
			public List<Class> items(Object context) {
				return ImmutableList.<Class>of(Declaration.class,Type.class,Method.class);
			}
		};
		LabelProvider labelProvider = new LabelProvider(){
		
			@Override
			public String text(Object object) {
				String result = null; 
				if(object instanceof Class) {
					if(object == Declaration.class) {
						result = "All declarations";
					} else {
						result = ((Class) object).getSimpleName();
					}
				}
				return result;
			}
		};
		Function<Class,UniversalPredicate<? super Element, Nothing>,Nothing> function = new Function<Class, UniversalPredicate<? super Element,Nothing>, Nothing>() {

			@Override
			public UniversalPredicate<? super Element, Nothing> apply(Class argument) throws Nothing {
				return new TypePredicate<>(argument);
			}
		};
		ComboBoxSelector<Class,Element> selector = new ComboBoxSelector<>(contentProvider, labelProvider,function,2);
		return selector;
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
				} else if(node instanceof BinaryNode) {
					return new IsBinary();
				}
				
				else if (node instanceof DocumentLoaderNode) {
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
		TristateTreeSelector<Object, Element> tristateTreeSelector = new TristateTreeSelector<Object,Element>(contentProvider, generator, labelProvider);
		return tristateTreeSelector;
	}

}
