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
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalParameterType;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.action.SafeAction;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

import com.google.common.base.Function;

public class DependencyAnalyzer extends Analyzer {

	private Predicate<Pair<Type,Set<Type>>> _originPredicate;
	private Predicate<CrossReference> _crossReferencePredicate;

	public DependencyAnalyzer(Project project, Predicate<Pair<Type,Set<Type>>> filter, Predicate<CrossReference> crossReferencePredicate) {
		super(project);
		if(filter == null) {
			throw new IllegalArgumentException("The declaration predicate cannot be null");
		}
		if(crossReferencePredicate == null) {
			throw new IllegalArgumentException("The cross reference predicate cannot be null");
		}
		_originPredicate = filter;
		_crossReferencePredicate = crossReferencePredicate;
	}
	
//	public class RemoveSuperTypeDependencies extends SafeAction<Pair<Pair<Type,Set<Type>>,Pair<Type,Set<Type>>>> {
//
//		public RemoveSuperTypeDependencies(Class<Pair<Pair<Type, Set<Type>>, Pair<Type, Set<Type>>>> type) {
//			super(Pair.class);
//		}
//
//		@Override
//		public void perform(Pair<Pair<Type, Set<Type>>, Pair<Type, Set<Type>>> pair) throws Nothing {
//			try {
//				if(pair.first().first().subTypeOf(pair.second().first())) {
//					pair.first().second().removeAll(pair.second().second());
//				}
//			} catch (LookupException e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}
	
	public void filter(DependencyResult result) {
		Map<Type,Set<Type>> deps = result.dependencies();
		for(final Map.Entry<Type, Set<Type>> first : deps.entrySet()) {
			// if a type depends on S and T and S <: T then remove T to make the graph cleaner
			// might give a deceptive view though.
			new SafePredicate<Type>() {
				@Override
				public boolean eval(final Type o1) {
					return new SafePredicate<Type>() {

						@Override
						public boolean eval(Type o2) {
							try {
								return o1 == o2 || (! o2.subTypeOf(o1));
							} catch (LookupException e) {
								return true;
							} 
						}
					}.forAll(first.getValue());
				}
			}.filter(first.getValue());
			
			
			
			for(Map.Entry<Type, Set<Type>> second : deps.entrySet()) {
				try {
					Type t1 = first.getKey();
					Type t2 = second.getKey();
					if(t1 != t2 && t1.subTypeOf(t2)) {
						first.getValue().removeAll(second.getValue());
					}
				} catch (LookupException e) {
				}
			}
		}
	}
	
	public void visualize(Writer writer) throws InputException {
		
		Function<Type,Type> function = createMapper();
		
		DependencyResult result = analysisResult(new DependencyAnalysis<Type>(Type.class, _originPredicate, _crossReferencePredicate, function));
		filter(result);
		Map<Type,Set<Type>> deps = result.dependencies();
		ListenableDirectedGraph<Type, DefaultEdge> graph = new ListenableDirectedGraph<>(DefaultEdge.class);
		for(Map.Entry<Type,Set<Type>> dependencies: deps.entrySet()) {
			Type origin = dependencies.getKey();
			graph.addVertex(origin);
			for(Type dependency: dependencies.getValue()) {
				graph.addVertex(dependency);
				graph.addEdge(origin, dependency);
			}
		}
		DOTExporter<Type,DefaultEdge> exporter = createExporter();
		exporter.export(writer, graph);
	}

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
