package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyAnalyzer;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalParameterType;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

import com.google.common.base.Function;

public class JavaDependencyAnalyzer extends DependencyAnalyzer<Type> {

	public JavaDependencyAnalyzer(Project project, Predicate<Pair<Type,Type>> filter, Predicate<CrossReference<?>> crossReferencePredicate) {
		super(project,Type.class);
		if(filter == null) {
			throw new IllegalArgumentException("The declaration predicate cannot be null");
		}
		if(crossReferencePredicate == null) {
			throw new IllegalArgumentException("The cross reference predicate cannot be null");
		}
		_originPredicate = filter;
		_crossReferencePredicate = crossReferencePredicate;
	}
	
	@Override
	protected Function<Type, Type> createMapper() {
		return new Function<Type,Type> (){

			@Override
			public Type apply(Type type) {
				while(type instanceof ArrayType) {
					type = ((ArrayType)type).elementType();
				}
				while(type instanceof DerivedType) {
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

	protected DOTExporter<Type, DefaultEdge> createExporter() {
		return new DOTExporter<Type,DefaultEdge>(new VertexNameProvider<Type>() {

			@Override
			public String getVertexName(Type arg0) {
				return arg0.getFullyQualifiedName().replace('.', '_');
			}
		}, new VertexNameProvider<Type>() {

			@Override
			public String getVertexName(Type arg0) {
//				return arg0.getFullyQualifiedName().replace('.', '_');
				return arg0.name().replace('.', '_');
			}
		}, new EdgeNameProvider<DefaultEdge>() {

			@Override
			public String getEdgeName(DefaultEdge arg0) {
				return "";
			}
		}, new ComponentAttributeProvider<Type>() {

			@Override
			public Map<String, String> getComponentAttributes(Type arg0) {
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
		ListenableDirectedGraph<Type, DefaultEdge> graph = buildDependencyGraph();
		DOTExporter<Type,DefaultEdge> exporter = createExporter();
		exporter.export(writer, graph);
	}
	
	private GraphBuilder<Type> createGraphBuilder(final ListenableDirectedGraph<Type, DefaultEdge> graph) {
		return new GraphBuilder<Type>() {

			@Override
			public void addVertex(Type v) {
				graph.addVertex(v);
			}

			@Override
			public void addEdge(Type first, Type second) {
				graph.addEdge(first, second);
			}
		};
	}
	


	private ListenableDirectedGraph<Type, DefaultEdge> buildDependencyGraph()
			throws InputException {
		ListenableDirectedGraph<Type, DefaultEdge> graph = new ListenableDirectedGraph<>(DefaultEdge.class);
		GraphBuilder<Type> builder = createGraphBuilder(graph);
		buildGraph(builder);
		return graph;
	}

}
