package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import be.kuleuven.cs.distrinet.chameleon.analysis.OptionGroup;
import be.kuleuven.cs.distrinet.chameleon.analysis.PredicateOptionGroup;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.Dependency;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalysis;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalysis.HistoryFilter;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyOptions;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyResult;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalParameterType;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.LabelProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.PredicateSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.checkbox.CheckboxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.list.ComboBoxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.list.ListContentProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.CheckStateProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.DocumentLoaderContentProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeViewNodeLabelProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreePruner;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.DocumentLoaderContentProvider.SourceNode;
import be.kuleuven.cs.distrinet.chameleon.util.action.TopDown;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputSourceImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.StreamInputSource;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.workspace.LazyClassFileInputSource;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.function.Function;
import be.kuleuven.cs.distrinet.rejuse.graph.Edge;
import be.kuleuven.cs.distrinet.rejuse.graph.UniEdge;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;
import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;
import be.kuleuven.cs.distrinet.rejuse.tree.PrunedTreeStructure;
import be.kuleuven.cs.distrinet.rejuse.tree.TreePredicate;
import be.kuleuven.cs.distrinet.rejuse.tree.TreeStructure;

import com.google.common.collect.ImmutableList;

public class JavaDependencyOptions extends DependencyOptions {

	@Override
	public void setContext(Object context) {
		super.setContext(context);
		if(context instanceof Project) {
			_project = (Project) context;
		} else if(context instanceof Element) {
			_project = ((Element)context).project();
		}
		_java = (Java) _project.views().get(0).language();
		_root = _project.views().get(0).namespace();
		try {
			_throwable = _java.findType("java.lang.Throwable", _root);
		} catch (LookupException e) {
		}
	}
	
	private Project _project;
	private Namespace _root;
	private Java _java;
	private Type _throwable;
	
	public JavaDependencyOptions() {
		_groups = ImmutableList.of(_target,_source,_dependencies);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DependencyResult analyze() {
		UniversalPredicate sourcePredicate = _source.predicate();
		UniversalPredicate crossReferencePredicate = _dependencies.crossReferencePredicate();
		UniversalPredicate targetPredicate = _target.predicate();
		UniversalPredicate dependencyPredicate = _dependencies.predicate();
		TristateTreePruner<Object,Element> generator = new LoaderSelectionPredicateGenerator(new NamespaceSelectionPredicateGenerator(null));
		TreePredicate<? super Element, Nothing> source = generator.create(_source._locationSelector.root(), _source._locationSelector.checked(), _source._locationSelector.grayed());
		TreePredicate<? super Element, Nothing> targetLocation = generator.create(_target._locationSelector.root(), _target._locationSelector.checked(), _target._locationSelector.grayed());
		DependencyAnalysis<Element, Declaration> dependencyAnalysis = new DependencyAnalysis<Element,Declaration>(
				Declaration.class,
				sourcePredicate.and(source), 
				crossReferencePredicate,
				Declaration.class,
				mapper(), 
				targetPredicate.and(targetLocation), 
				dependencyPredicate,
				new RedundantInheritedDependencyFilter());
		TreeStructure<Element> logicalStructure = _root.logical();
		PrunedTreeStructure<Element> sourceStructure = new PrunedTreeStructure(logicalStructure, source);
		TopDown<Element, Nothing> topDown = new TopDown<>(dependencyAnalysis);
		topDown.traverse(_root, sourceStructure);
		DependencyResult result = dependencyAnalysis.result();
		result.prune();
		return result;
	}
	
	
	List<? extends OptionGroup> _groups;

	private SourceOptionGroup _source = new SourceOptionGroup();
	private class SourceOptionGroup extends PredicateOptionGroup {
		public SourceOptionGroup() {
			super("Source");
			addPredicateSelector(declarationTypeSelector());
//			addPredicateSelector(namespaceSelector());
			_locationSelector = loaderSelector();
			add(_locationSelector);
//			TristateTreePruner<Object,Element> generator = new LoaderSelectionPredicateGenerator(new NamespaceSelectionPredicateGenerator(null));

			addPredicateSelector(noAnonymousClasses());
		}
		
		public TristateTreeSelector<Object> _locationSelector;
	}

	private TargetOptionGroup _target = new TargetOptionGroup();
	private class TargetOptionGroup extends PredicateOptionGroup {
		public TargetOptionGroup() {
			super("Target");
			addPredicateSelector(declarationTypeSelector());
//			addPredicateSelector(namespaceSelector());
			_locationSelector = loaderSelector();
			add(_locationSelector);
			addPredicateSelector(noExceptions());
		}
		public TristateTreeSelector<Object> _locationSelector;
	}
	
	private DependencyOptionGroup _dependencies = new DependencyOptionGroup();
	private class DependencyOptionGroup extends PredicateOptionGroup {
		public DependencyOptionGroup() {
			super("Dependency");
			addPredicateSelector(noDescendants());
			addPredicateSelector(noAncestors());
//			addCrossReferenceSelector(new CheckboxSelector<>(new NoSupertypeReferences(), "Ignore Subtype Relations",true));
			addPredicateSelector(noSuperTypes());
		}
		
		protected void addCrossReferenceSelector(PredicateSelector<?> selector) {
			add(selector);
			_crossReferencePredicate.add(selector);
		}
		
		protected UniversalPredicate crossReferencePredicate() {
			return predicate(_crossReferencePredicate);
		}
		
		protected List<PredicateSelector<?>> _crossReferencePredicate = new ArrayList<>();
	}	
	
	
	private Function mapper() {
		return new Function<Element,Element,Nothing> (){
			@Override
			public Element apply(Element element) {
				element = element.origin();
				while(element instanceof ArrayType) {
					element = ((ArrayType)element).elementType();
				}
				while(element instanceof DerivedType) {
					element = ((DerivedType)element).baseType();
				}
				while(element instanceof FormalParameterType) {
					element = element.nearestAncestor(Type.class);
				}
				if(element instanceof Type) {
					AnonymousType anon = element.farthestAncestorOrSelf(AnonymousType.class);
					if(anon != null) {
						element = anon.nearestAncestor(Type.class);
					}
				}
				return element;
			}
		};
	}

	private PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>> noSuperTypes() {
		return new CheckboxSelector<Dependency<?,?,?>>(
				
				new UniversalPredicate<Dependency, Nothing>(Dependency.class) {

			@Override
			public boolean uncheckedEval(Dependency t) throws Nothing {
				Element target = (Element) t.target();
				Element source = (Element)t.source();
				if(source instanceof Type && target instanceof Type) {
					try {
						return ! ((Type)source).subTypeOf((Type) target);
					} catch (LookupException e) {
					}
				}
				return true;
			}
		}, "Ignore super types",true);
	}
	
	private PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>> noDescendants() {
		return new CheckboxSelector<Dependency<?,?,?>>(
				
				new UniversalPredicate<Dependency, Nothing>(Dependency.class) {

			@Override
			public boolean uncheckedEval(Dependency t) throws Nothing {
				return ! ((Element) t.target()).hasAncestor((Element)t.source());
			}
		}, "Ignore lexical descendants",false);
	}
	
	private PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>> noAncestors() {
		return new CheckboxSelector<Dependency<?,?,?>>(
				
				new UniversalPredicate<Dependency, Nothing>(Dependency.class) {

			@Override
			public boolean uncheckedEval(Dependency t) throws Nothing {
				return ! ((Element) t.source()).hasAncestor((Element)t.target());
			}
		}, "Ignore lexical ancestors",true);
	}
	
	private PredicateSelector<Element> declarationTypeSelector() {
		ListContentProvider<Class> contentProvider = new ListContentProvider<Class>() {

			@Override
			public List<Class> items(Object context) {
				return ImmutableList.<Class>of(Declaration.class,Namespace.class,Type.class,Method.class,Variable.class);
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

	private PredicateSelector<Element> noAnonymousClasses() {
		return new CheckboxSelector<Element>(new UniversalPredicate<Element, Nothing>(Element.class) {

			@Override
			public boolean uncheckedEval(Element t) {
				return ! (t instanceof AnonymousType);
			}
		}, "Ignore anonymous types", true);
	}
	private PredicateSelector<Element> noExceptions() {
		return new CheckboxSelector<Element>(new UniversalPredicate<Element, Nothing>(Element.class) {

			@Override
			public boolean uncheckedEval(Element t) {
				if(t instanceof Type) {
					try {
						return _throwable != null && (! ((Type)t).subTypeOf(_throwable));
					} catch (LookupException e) {
					}
				}
				return true;
			}
		}, "Ignore throwables", true);
	}
	
	private TristateTreeSelector<Object> loaderSelector() {
		DocumentLoaderContentProvider contentProvider = new DocumentLoaderContentProvider();
		
		TreeViewNodeLabelProvider labelProvider = new TreeViewNodeLabelProvider();
		CheckStateProvider checkStateProvider = new CheckStateProvider<Object>() {

			@Override
			public boolean isGrayed(Object element) {
				return false;
			}

			@Override
			public boolean isChecked(Object element) {
				return element instanceof SourceNode;
			}
		};
		TristateTreeSelector<Object> tristateTreeSelector = new TristateTreeSelector<Object>(contentProvider, labelProvider, checkStateProvider);
		return tristateTreeSelector;
	}
	
//	private Selector<Element> namespaceSelector() {
//		NamespaceContentProvider contentProvider = new NamespaceContentProvider();
//		
//		TreeViewNodeLabelProvider labelProvider = new TreeViewNodeLabelProvider();
////		TristatePredicateGenerator<Object,Element> generator = new NamespaceSelectionPredicateGenerator(null);
//		TristateTreeSelector<Object, Element> tristateTreeSelector = new TristateTreeSelector<Object,Element>(contentProvider, labelProvider);
//		return tristateTreeSelector;
//	}

	public static class RedundantInheritedDependencyFilter extends HistoryFilter<Element,Declaration>{
		
		private static class Container {
			public boolean add = true;
		}
		
		public boolean process(Dependency<Element, CrossReference, Declaration> dependency, DependencyResult result) {
			final Container container = new Container(); 
			final Element newSource = dependency.source();
			final Element newTarget = dependency.target();

			result.<Nothing>filter(new AbstractPredicate<Edge<Element>, Nothing>() {

				@Override
				public boolean eval(Edge<Element> object) throws Nothing {
					try {
						if(container.add == false) {
							return true;
						}
						Element oldSource = ((UniEdge<Element>) object).startNode()
								.object();
						Element oldTarget = ((UniEdge<Element>) object).endNode().object();
						if (newSource instanceof Type && oldSource instanceof Type
								&& newTarget instanceof Type && oldTarget instanceof Type) {
							Type newSourceType = (Type) newSource;
							Type newTargetType = (Type) newTarget;
							Type oldSourceType = (Type) oldSource;
							Type oldTargetType = (Type) oldTarget;

							// We first check if the new dependency is redundant.
							// That way, if we reach the else branch, we now
							// for sure that we can remove the old dependency
							// if the second branch is executed.
							if (newSourceType.subTypeOf(oldSourceType) && oldTargetType.subTypeOf(newTargetType)) {
								container.add = false;
								return true;
							} else if(oldSourceType.subTypeOf(newSourceType) && newTargetType.subTypeOf(oldTargetType)) {
								return false;
							}
						}
						return true;
					} catch (LookupException e) {
						return true;
					}
				}
			});
			return container.add;
		}
	}

}
