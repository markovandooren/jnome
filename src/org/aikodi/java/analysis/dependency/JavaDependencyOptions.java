package org.aikodi.java.analysis.dependency;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.analysis.OptionGroup;
import org.aikodi.chameleon.analysis.PredicateOptionGroup;
import org.aikodi.chameleon.analysis.dependency.Dependency;
import org.aikodi.chameleon.analysis.dependency.DependencyAnalysis;
import org.aikodi.chameleon.analysis.dependency.DependencyAnalysis.HistoryFilter;
import org.aikodi.chameleon.analysis.dependency.DependencyAnalysis.NOOP;
import org.aikodi.chameleon.analysis.dependency.DependencyOptions;
import org.aikodi.chameleon.analysis.dependency.DependencyResult;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.ui.widget.LabelProvider;
import org.aikodi.chameleon.ui.widget.PredicateSelector;
import org.aikodi.chameleon.ui.widget.checkbox.CheckboxPredicateSelector;
import org.aikodi.chameleon.ui.widget.list.ComboBoxSelector;
import org.aikodi.chameleon.ui.widget.list.ListContentProvider;
import org.aikodi.chameleon.ui.widget.tree.CheckStateProvider;
import org.aikodi.chameleon.ui.widget.tree.DocumentScannerContentProvider;
import org.aikodi.chameleon.ui.widget.tree.DocumentScannerContentProvider.SourceNode;
import org.aikodi.chameleon.ui.widget.tree.TreeViewNodeLabelProvider;
import org.aikodi.chameleon.ui.widget.tree.TristateTreePruner;
import org.aikodi.chameleon.ui.widget.tree.TristateTreeSelector;
import org.aikodi.chameleon.util.action.GuardedTreeWalker;
import org.aikodi.chameleon.util.action.TopDown;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.java.core.type.AnonymousType;
import org.aikodi.java.tool.JavaDeclarationDecomposer;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.exception.Handler;
import org.aikodi.rejuse.function.Function;
import org.aikodi.rejuse.graph.UniEdge;
import org.aikodi.rejuse.predicate.True;
import org.aikodi.rejuse.predicate.TypePredicate;
import org.aikodi.rejuse.predicate.UniversalPredicate;
import org.aikodi.rejuse.tree.PrunedTreeStructure;
import org.aikodi.rejuse.tree.TreePredicate;
import org.aikodi.rejuse.tree.TreeStructure;

import com.google.common.collect.ImmutableList;

public class JavaDependencyOptions extends DependencyOptions {
  
  public static final RedundantInheritedDependencyFilter REDUNDANT_INHERITED_DEPENDENCY_FILTER = new RedundantInheritedDependencyFilter();


  @Override
  public void setContext(Object context) {
    super.setContext(context);
    if(context instanceof Project) {
      _project = (Project) context;
    } else if(context instanceof Element) {
      _project = ((Element)context).project();
    }
    //		_java = (Java7) _project.views().get(0).language();
    _root = _project.views().get(0).namespace();
    try {
      _throwable = _root.find("java.lang.Throwable",Type.class);
    } catch (LookupException e) {
    }
  }

  private Project _project;
  private Namespace _root;
  //	private Java7 _java;
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

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public DependencyResult analyze() {
    TristateTreePruner<Object,Element> generator = new LoaderSelectionPredicateGenerator(new NamespaceSelectionPredicateGenerator(null));
    TreePredicate<? super Element, Nothing> source = generator.create(_source._locationSelector.root(), _source._locationSelector.checked(), _source._locationSelector.grayed());
    UniversalPredicate sourcePredicate = _source.predicate().and(source);
    UniversalPredicate crossReferencePredicate = _dependencies.crossReferencePredicate();
    UniversalPredicate targetPredicate = _target.predicate();
    UniversalPredicate dependencyPredicate = _dependencies.predicate();
    HistoryFilter<Declaration, Declaration> historyFilter = _dependencies.historyFilter();
    TreePredicate<? super Element, Nothing> targetLocation = generator.create(_target._locationSelector.root(), _target._locationSelector.checked(), _target._locationSelector.grayed());
    Class<Declaration> sourceType = Declaration.class;
    UniversalPredicate and2 = targetPredicate.and(targetLocation);
    UniversalPredicate dependencyPredicate2 = dependencyPredicate;
    HistoryFilter<Declaration, Declaration> historyFilter2 = historyFilter;
    DependencyAnalysis<Declaration, Declaration> dependencyAnalysis = new DependencyAnalysis<Declaration,Declaration>(
        sourceType,
        sourcePredicate, 
        crossReferencePredicate,
        sourceType,
        mapper(), 
        and2, 
        dependencyPredicate2,
        historyFilter2);
    TreeStructure<Element> logicalStructure = _root.logical();
    PrunedTreeStructure<Element> sourceStructure = new PrunedTreeStructure(logicalStructure, source);
    GuardedTreeWalker<Element, LookupException, Nothing> guardedTreeWalker = new GuardedTreeWalker<>(dependencyAnalysis, Handler.resume());
    TopDown<Element, Nothing> topDown = new TopDown<>(guardedTreeWalker);
    topDown.traverse(sourceStructure);
    DependencyResult result = dependencyAnalysis.result();
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
      addPredicateSelector(noSuperTypes());
      addHistoryFilterSelector(new CheckboxHistoryFilterSelector("Prune redundant dependencies on super classes", true, (HistoryFilter<?, ?>) REDUNDANT_INHERITED_DEPENDENCY_FILTER));
    }

    protected void addCrossReferenceSelector(PredicateSelector<?> selector) {
      add(selector);
      _crossReferencePredicate.add(selector);
    }

    protected UniversalPredicate crossReferencePredicate() {
      return predicate(_crossReferencePredicate);
    }

    protected void addHistoryFilterSelector(HistoryFilterSelector selector) {
      add(selector);
      _historyFilters.add(selector);
    }

    protected HistoryFilter<Declaration, Declaration> historyFilter() {
      HistoryFilter result = new NOOP();
      for(HistoryFilterSelector selector: _historyFilters) {
        result = result.and(selector.filter());
      }
      return result;
    }

    protected List<HistoryFilterSelector> _historyFilters = new ArrayList<>();


    protected List<PredicateSelector<?>> _crossReferencePredicate = new ArrayList<>();


  }	

  private Function<Declaration,List<Declaration>,Nothing> mapper() {
    return new JavaDeclarationDecomposer();
  }

  private PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>> noSuperTypes() {
    return new CheckboxPredicateSelector<Dependency<?,?,?>>(

        new UniversalPredicate<Dependency, Nothing>(Dependency.class) {

          @Override
          public boolean uncheckedEval(Dependency t) throws Nothing {
            Element target = (Element) t.target();
            Element source = (Element)t.source();
            if(source instanceof Type && target instanceof Type) {
              try {
                return ! ((Type)source).subtypeOf((Type) target);
              } catch (LookupException e) {
              }
            }
            return true;
          }
        }, "Ignore super types",true);
  }

  private PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>> noDescendants() {
    return new CheckboxPredicateSelector<Dependency<?,?,?>>(

        new UniversalPredicate<Dependency, Nothing>(Dependency.class) {

          @Override
          public boolean uncheckedEval(Dependency t) throws Nothing {
            return ! ((Element) t.target()).hasAncestor((Element)t.source());
          }
        }, "Ignore lexical descendants",false);
  }

  private PredicateSelector<? super Dependency<? super Element, ? super CrossReference, ? super Declaration>> noAncestors() {
    return new CheckboxPredicateSelector<Dependency<?,?,?>>(IGNORE_LEXICAL_ANCESTORS, "Ignore lexical ancestors",true);
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
        return new TypePredicate(argument);
      }
    };
    ComboBoxSelector<Class,Element> selector = new ComboBoxSelector<>(contentProvider, labelProvider,function,2);
    return selector;
  }

  private PredicateSelector<Element> noAnonymousClasses() {
    return new CheckboxPredicateSelector<Element>(new UniversalPredicate<Element, Nothing>(Element.class) {

      @Override
      public boolean uncheckedEval(Element t) {
        return ! (t instanceof AnonymousType);
      }
    }, "Ignore anonymous types", true);
  }
  private PredicateSelector<Element> noExceptions() {
    return new CheckboxPredicateSelector<Element>(new UniversalPredicate<Element, Nothing>(Element.class) {

      @Override
      public boolean uncheckedEval(Element t) {
        if(t instanceof Type) {
          try {
            return _throwable != null && (! ((Type)t).subtypeOf(_throwable));
          } catch (LookupException e) {
          }
        }
        return true;
      }
    }, "Ignore throwables", true);
  }

  private TristateTreeSelector<Object> loaderSelector() {
    DocumentScannerContentProvider contentProvider = new DocumentScannerContentProvider();

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

  public static class RedundantInheritedDependencyFilter implements HistoryFilter<Element,Declaration>{

    private static class Container {
      public boolean add = true;
    }

    public boolean process(Dependency<Element, CrossReference, Declaration> dependency, DependencyResult result) {
      final Container container = new Container(); 
      final Element newSource = dependency.source();
      final Element newTarget = dependency.target();
      result.<Nothing>filter(object -> {
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
            if(newSourceType != oldSourceType) {
              if (newSourceType.subtypeOf(oldSourceType) && oldTargetType.subtypeOf(newTargetType)) {
                container.add = false;
                return true;
              } else if(oldSourceType.subtypeOf(newSourceType) && newTargetType.subtypeOf(oldTargetType)) {
                return false;
              }
            }
          }
          return true;
        } catch (LookupException e) {
          return true;
        }
      });
      return container.add;
    }
  }

}
