package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analyzer;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

public class DependencyAnalyzer extends Analyzer {

	private Predicate<Type> _filter;

	public DependencyAnalyzer(Project project, Predicate<Type> filter) {
		super(project);
		_filter = filter;
	}
	
	public void visualize(Writer writer) throws InputException {
		DependencyResult result = analysisResult(new DependencyAnalysis());
		Map<Type,Set<Type>> deps = result.dependencies();
		ListenableDirectedGraph<Type, DefaultEdge> graph = new ListenableDirectedGraph<>(DefaultEdge.class);
		for(Map.Entry<Type,Set<Type>> dependencies: deps.entrySet()) {
			Type origin = dependencies.getKey();
			try {
				if(_filter.eval(origin)) {
					Set<Type> value = dependencies.getValue();
					//			if(! value.isEmpty()) {
					graph.addVertex(origin);
					//			}
					for(Type dependency: value) {
						boolean add = true;
						add = _filter.eval(dependency); 
						if(add) {
							graph.addVertex(dependency);
							graph.addEdge(origin, dependency);
						}
					}
				}
			} catch (Exception e) {
			}
		}
		DOTExporter<Type,DefaultEdge> exporter = createExporter();
		exporter.export(writer, graph);
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
				return arg0.getFullyQualifiedName().replace('.', '_');
			}
		}, new EdgeNameProvider<DefaultEdge>() {

			@Override
			public String getEdgeName(DefaultEdge arg0) {
				return "";
			}
		});
	}
}
