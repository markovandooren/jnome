package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.aikodi.chameleon.analysis.dependency.DependencyAnalyzer;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.oo.type.TypeInstantiation;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.FormalParameterType;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.contract.Contracts;
import be.kuleuven.cs.distrinet.rejuse.function.Function;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class JavaDependencyAnalyzer extends DependencyAnalyzer<Type> {

	public JavaDependencyAnalyzer(Project project, 
			                      UniversalPredicate<Type,Nothing> elementPredicate, 
			                      UniversalPredicate<? super CrossReference<?>,Nothing> crossReferencePredicate,
			                      UniversalPredicate<Type,Nothing> declarationPredicate) {
		super(project);
		Contracts.notNull(elementPredicate, "The declaration predicate cannot be null");
		Contracts.notNull(crossReferencePredicate, "The cross reference predicate cannot be null");
		_elementPredicate = elementPredicate;
		_crossReferencePredicate = crossReferencePredicate;
		_declarationPredicate = declarationPredicate;
	}
	
	@Override
	protected UniversalPredicate<? super CrossReference<?>, Nothing> crossReferencePredicate() {
		return _crossReferencePredicate;
	}
	
	@Override
	protected Function<Type, Type,Nothing> createMapper() {
		return new Function<Type,Type,Nothing> (){

			@Override
			public Type apply(Type type) {
				while(type instanceof ArrayType) {
					type = ((ArrayType)type).elementType();
				}
				while(type instanceof TypeInstantiation) {
					type = type.baseType();
				}
				while(type instanceof FormalParameterType) {
					type = type.nearestAncestor(Type.class);
				}
				AnonymousType anon = type.farthestAncestorOrSelf(AnonymousType.class);
				if(anon != null) {
					type = anon.nearestAncestor(Type.class);
				}
				return type;
			}
		};
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
	protected UniversalPredicate<Type, Nothing> elementPredicate() {
		return _elementPredicate;
	}

	@Override
	protected UniversalPredicate<Type, Nothing> declarationPredicate() {
		return _declarationPredicate;
	}

	private UniversalPredicate<Type,Nothing> _elementPredicate;
	private UniversalPredicate<Type,Nothing> _declarationPredicate;
	private UniversalPredicate<? super CrossReference<?>,Nothing> _crossReferencePredicate;


}
