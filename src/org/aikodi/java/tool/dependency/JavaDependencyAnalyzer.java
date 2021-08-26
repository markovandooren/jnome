package org.aikodi.java.tool.dependency;

import static java.util.stream.DoubleStream.of;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.Writer;

import org.aikodi.chameleon.analysis.dependency.DependencyAnalysis.HistoryFilter;
import org.aikodi.chameleon.analysis.dependency.DependencyAnalyzer;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.java.analysis.dependency.JavaDependencyOptions;
import org.aikodi.java.tool.JavaDeclarationDecomposer;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.contract.Contracts;
import org.aikodi.rejuse.exception.Handler;
import org.aikodi.rejuse.function.Function;
import org.aikodi.rejuse.data.graph.Edge;
import org.aikodi.rejuse.data.graph.Graph;
import org.aikodi.rejuse.data.graph.Node;
import org.aikodi.rejuse.data.graph.Path;
import org.aikodi.rejuse.predicate.UniversalPredicate;

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

//  protected DOTExporter<Element, DefaultEdge> createExporter() {
//    return new DOTExporter<Element,DefaultEdge>(new VertexNameProvider<Element>() {
//
//      @Override
//      public String getVertexName(Element arg0) {
//        if(arg0 instanceof Type) {
//          String result = ((Type)arg0).getFullyQualifiedName().replace('.', '_');
//          result = result.replace(',', '_');
//          result = result.replace(' ', '_');
//          return result;
//        } else {
//          throw new IllegalArgumentException();
//        }
//      }
//    }, new VertexNameProvider<Element>() {
//
//      @Override
//      public String getVertexName(Element arg0) {
//        if(arg0 instanceof Type) {
//          String result = ((Type)arg0).name().replace('.', '_');
//          result = result.replace(',', '_');
//          result = result.replace(' ', '_');
//          return result;
//        } else {
//          throw new IllegalArgumentException();
//        }
//      }
//    }, new EdgeNameProvider<DefaultEdge>() {
//
//      @Override
//      public String getEdgeName(DefaultEdge arg0) {
//        return "";
//      }
//    }, new ComponentAttributeProvider<Element>() {
//
//      @Override
//      public Map<String, String> getComponentAttributes(Element arg0) {
//        Map<String,String> result = new HashMap<>();
//        result.put("shape", "box");
//        return result;
//      }
//    }, new ComponentAttributeProvider<DefaultEdge>() {
//
//      @Override
//      public Map<String, String> getComponentAttributes(DefaultEdge arg0) {
//        Map<String,String> result = new HashMap<>();
//        return result;
//      }
//    }
//
//
//        );
//  }

  public static class DOTWriter {
  	public void write(Graph<Type> graph, Writer stream) throws IOException {
  		List<Path<Type>> simpleCycles = graph.simpleCycles();
  		Set<Type> involvedInCycle = simpleCycles.stream().flatMap(p -> p.nodes().stream().map(x -> x.object())).collect(Collectors.toSet());
//  		Set<Edge<Type>> cycleEdges = simpleCycles.stream().flatMap(p -> p.getEdges().stream()).collect(Collectors.toSet());
  		stream.write("digraph G {\n");
  		writeNodes(graph,stream, involvedInCycle);
  		writeEdges(graph,stream, involvedInCycle);
  		stream.write("}\n");
  	}

		private void writeEdges(Graph<Type> graph, Writer stream, Set<Type> cycleEdges) throws IOException {
			for(Edge<Type> n: graph.edges()) {
				String color ="";
				if(cycleEdges.contains(n.getFirst().object())) {
					color = "color=\"red\"";
				}
				stream.write(name(n.getFirst().object()) + " -> " +name(n.getSecond().object())+ "[ label=\"\" "+color+" ];\n");
			};
		}

		private void writeNodes(Graph<Type> graph, Writer stream,Set<Type> types) throws IOException {
			for(Node<Type> n: graph.nodes()){
				stream.write(name(n.object()) + "[ label=\""+n.object().name() + "\" " + attributes(n.object(), types) +" ];\n");
			};
		}
		
		private String attributes(Type type, Set<Type> types) {
			String standard = "shape=\"box\"";
			if(types.contains(type)) {
			return standard+" color=\"red\" fontcolor=\"red\"";
			} else {
				return standard;
			}
		}

		private String name(Type element) {
      String result = ((Type)element).getFullyQualifiedName().replace('.', '_');
      result = result.replace(',', '_');
      result = result.replace(' ', '_');
      return result;
		}
  }
  
  public void visualize(Writer writer) throws InputException, IOException {
//    ListenableDirectedGraph<Element, DefaultEdge> graph = buildDependencyGraph();
//    DOTExporter<Element,DefaultEdge> exporter = createExporter();
//    exporter.export(writer, graph);
  	Graph graph = dependencyResult(Handler.printer(System.out), Handler.printer(System.out)).graph();
    new DOTWriter().write(graph, writer);
    
  }
  
  public void computeStats(Writer writer, Writer cycleWriter) throws IOException {
    Graph<Element> graph = dependencyResult(Handler.printer(System.out), Handler.printer(System.out)).graph();
    double[] dependencies = graph.nodes().stream().mapToDouble(n -> n.numberOfSuccessorEdges()).toArray();
    double averageDependencies = of(dependencies).average().orElse(0);
    double maxDependencies = of(dependencies).max().orElse(0);
    long start = System.nanoTime();
    List<Path<Element>> simpleCycles = graph.simpleCycles();
    long stop = System.nanoTime();
//    DecimalFormat myFormatter = new DecimalFormat("###,###,###,###.###,###,###");
//    String output = myFormatter.format((stop-start)/1000000000.0);
    System.out.println("Computing simple cycles took "+(stop-start)/1000000000.0+" seconds");
//    List<Path<Element>> dumbCycles = graph.simpleCycles();
    int nbSimpleCycles = simpleCycles.size();
//    int nbDumbCycles = dumbCycles.size();
//    Util.debug(nbSimpleCycles != nbDumbCycles);
    writer.write("Average dependencies: "+averageDependencies+"\n");
    writer.write("Max dependencies: "+maxDependencies+"\n");
    writer.write("Number of simple cycles: "+nbSimpleCycles+"\n");
    writer.close();
    for(Path<Element> cycle: simpleCycles) {
    	String out = cycle.toString();//cycle.stream().map(e -> e.toString()).collect(Collectors.joining(" -> "));
      cycleWriter.write(out);
      cycleWriter.write("\n");
    }
    cycleWriter.close();
  }

//  private GraphBuilder<Element> createGraphBuilder(final ListenableDirectedGraph<Element, DefaultEdge> graph) {
//    return new GraphBuilder<Element>() {
//
//      @Override
//      public void addVertex(Element v) {
//        graph.addVertex(v);
//      }
//
//      @Override
//      public void addEdge(Element first, Element second) {
//        addVertex(first);
//        addVertex(second);
//        graph.addEdge(first, second);
//      }
//    };
//  }

//  private ListenableDirectedGraph<Element, DefaultEdge> buildDependencyGraph() throws InputException {
//    ListenableDirectedGraph<Element, DefaultEdge> graph = new ListenableDirectedGraph<>(DefaultEdge.class);
//    GraphBuilder<Element> builder = createGraphBuilder(graph);
//    buildGraph(builder, Handler.printer(System.out), Handler.printer(System.out));
//    return graph;
//  }

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
