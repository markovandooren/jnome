package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aikodi.chameleon.analysis.dependency.DependencyAnalyzer;
import org.aikodi.chameleon.analysis.dependency.DependencyAnalysis.HistoryFilter;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import be.kuleuven.cs.distrinet.jnome.analysis.dependency.JavaDependencyOptions;
import be.kuleuven.cs.distrinet.jnome.tool.JavaDeclarationDecomposer;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.contract.Contracts;
import be.kuleuven.cs.distrinet.rejuse.function.Function;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class JavaDependencyAnalyzer extends DependencyAnalyzer<Type> {

  public JavaDependencyAnalyzer(Project project, 
      UniversalPredicate<Type,Nothing> sourcePredicate, 
      UniversalPredicate<? super CrossReference<?>,Nothing> crossReferencePredicate,
      UniversalPredicate<Type,Nothing> targetPredicate) {
    super(project);
    Contracts.notNull(sourcePredicate, "The source predicate cannot be null");
    Contracts.notNull(crossReferencePredicate, "The cross reference predicate cannot be null");
    _sourcePredicate = sourcePredicate;
    _crossReferencePredicate = crossReferencePredicate;
    _targetPredicate = targetPredicate;
  }

  @Override
  protected UniversalPredicate<? super CrossReference<?>, Nothing> crossReferencePredicate() {
    return _crossReferencePredicate;
  }

  @Override
  protected Function<Declaration, List<Declaration>,Nothing> createMapper() {
    return new JavaDeclarationDecomposer();
  }

  protected DOTExporter<Element, DefaultEdge> createExporter() {
    return new DOTExporter<Element,DefaultEdge>(new VertexNameProvider<Element>() {

      @Override
      public String getVertexName(Element arg0) {
        if(arg0 instanceof Type) {
          String result = ((Type)arg0).getFullyQualifiedName().replace('.', '_');
          result = result.replace(',', '_');
          result = result.replace(' ', '_');
          return result;
        } else {
          throw new IllegalArgumentException();
        }
      }
    }, new VertexNameProvider<Element>() {

      @Override
      public String getVertexName(Element arg0) {
        if(arg0 instanceof Type) {
          String result = ((Type)arg0).name().replace('.', '_');
          result = result.replace(',', '_');
          result = result.replace(' ', '_');
          return result;
        } else {
          throw new IllegalArgumentException();
        }
      }
    }, new EdgeNameProvider<DefaultEdge>() {

      @Override
      public String getEdgeName(DefaultEdge arg0) {
        return "";
      }
    }, new ComponentAttributeProvider<Element>() {

      @Override
      public Map<String, String> getComponentAttributes(Element arg0) {
        Map<String,String> result = new HashMap<>();
        result.put("shape", "box");
        return result;
      }
    }, new ComponentAttributeProvider<DefaultEdge>() {

      @Override
      public Map<String, String> getComponentAttributes(DefaultEdge arg0) {
        Map<String,String> result = new HashMap<>();
        return result;
      }
    }


        );
  }

  public void visualize(Writer writer) throws InputException {
    ListenableDirectedGraph<Element, DefaultEdge> graph = buildDependencyGraph();
    DOTExporter<Element,DefaultEdge> exporter = createExporter();
    exporter.export(writer, graph);
  }

  private GraphBuilder<Element> createGraphBuilder(final ListenableDirectedGraph<Element, DefaultEdge> graph) {
    return new GraphBuilder<Element>() {

      @Override
      public void addVertex(Element v) {
        graph.addVertex(v);
      }

      @Override
      public void addEdge(Element first, Element second) {
        addVertex(first);
        addVertex(second);
        graph.addEdge(first, second);
      }
    };
  }

  private ListenableDirectedGraph<Element, DefaultEdge> buildDependencyGraph() throws InputException {
    ListenableDirectedGraph<Element, DefaultEdge> graph = new ListenableDirectedGraph<>(DefaultEdge.class);
    GraphBuilder<Element> builder = createGraphBuilder(graph);
    buildGraph(builder);
    return graph;
  }

  @Override
  protected UniversalPredicate<Type, Nothing> sourcePredicate() {
    return _sourcePredicate;
  }

  @Override
  protected UniversalPredicate<Type, Nothing> declarationPredicate() {
    return _targetPredicate;
  }

  private UniversalPredicate<Type,Nothing> _sourcePredicate;
  private UniversalPredicate<Type,Nothing> _targetPredicate;
  private UniversalPredicate<? super CrossReference<?>,Nothing> _crossReferencePredicate;

  @Override
  protected HistoryFilter<Type, Type> historyFilter() {
    return (HistoryFilter)JavaDependencyOptions.REDUNDANT_INHERITED_DEPENDENCY_FILTER;
  }

}
