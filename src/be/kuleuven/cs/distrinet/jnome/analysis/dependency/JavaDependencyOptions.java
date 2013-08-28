package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.analysis.AbstractAnalysisOptions;
import be.kuleuven.cs.distrinet.chameleon.analysis.OptionGroup;
import be.kuleuven.cs.distrinet.chameleon.analysis.PredicateOptionGroup;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.Dependency;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalysis;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
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
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TreeViewNodeLabelProvider;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.tree.TristateTreeSelector.TristatePredicateGenerator;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.function.Function;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;
import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

import com.google.common.collect.ImmutableList;

public class JavaDependencyOptions extends AbstractAnalysisOptions {

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
	
	@Override
	public DependencyAnalysis<Element,Declaration> createAnalysis() {
		UniversalPredicate sourcePredicate = _source.predicate();
		UniversalPredicate crossReferencePredicate = _dependencies.crossReferencePredicate();
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
			addPredicateSelector(sourceSelector());
			addPredicateSelector(noAnonymousClasses());
		}
	}

	private TargetOptionGroup _target = new TargetOptionGroup();
	private class TargetOptionGroup extends PredicateOptionGroup {
		public TargetOptionGroup() {
			super("Target");
			addPredicateSelector(declarationTypeSelector());
			addPredicateSelector(sourceSelector());
			addPredicateSelector(noExceptions());
		}
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
		}, "Ignore lexical descendants",true);
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
	
	private PredicateSelector<Element> sourceSelector() {
		LexicalTreeContentProvider contentProvider = new LexicalTreeContentProvider();
		
		TreeViewNodeLabelProvider labelProvider = new TreeViewNodeLabelProvider();
		TristatePredicateGenerator<Object,Element> generator = new ContainerSelectionPredicateGenerator();
		TristateTreeSelector<Object, Element> tristateTreeSelector = new TristateTreeSelector<Object,Element>(contentProvider, generator, labelProvider);
		return tristateTreeSelector;
	}

}
