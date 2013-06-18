package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.view.ObjectOrientedView;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.eclipse.AnalysisTool;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.And;
import be.kuleuven.cs.distrinet.rejuse.predicate.False;
import be.kuleuven.cs.distrinet.rejuse.predicate.GlobPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.Or;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;

import com.lexicalscope.jewel.cli.Option;

public class DependencyAnalysisTool extends AnalysisTool {

	public final static String NAME = "DependencyAnalysis";
	
	public DependencyAnalysisTool() {
		super(NAME);
	}
	
	
	/**
	 * Add a comment before writing the project info. Otherwise the DOT file is corrupt.
	 */
	@Override
	protected void writeProjectInfo(File root, OutputStreamWriter writer) throws IOException {
		writer.write("//");
		super.writeProjectInfo(root, writer);
	}

	@Override
	protected void check(Project project, OutputStreamWriter writer, AnalysisOptions options) throws LookupException, InputException, IOException {
		ObjectOrientedView view = (ObjectOrientedView) project.views().get(0);
		Predicate<Pair<Type, Set<Type>>> filter = 
				new And<> (hierarchyFilter(options, view), annotationFilter(options, view));
		filter = new And<> (filter, annonymousTypeFilter(options,view));
		filter = new And<> (filter, packageFilter(options,view));
		Predicate<CrossReference> crossReferenceFilter =
				new And<>(crossReferenceSourceFilter(),crossReferenceNonInheritanceFilter());
				crossReferenceFilter = new And<>(crossReferenceFilter,crossReferenceHierarchyFilter(options,view));
		new DependencyAnalyzer(project,filter,crossReferenceFilter).visualize(writer);
	}

	protected Predicate<CrossReference> crossReferenceSourceFilter() {
//		return new True<>();
		return new SafePredicate<CrossReference>() {

			@Override
			public boolean eval(CrossReference object) {
				Declaration d;
				try {
					d = object.getElement();
					return d.view().isSource(d);
				} catch (LookupException e) {
					return true;
				}
			}
		};
	
	}

	protected Predicate<CrossReference> crossReferenceNonInheritanceFilter() {
	return new SafePredicate<CrossReference>() {

		@Override
		public boolean eval(CrossReference object) {
			return object.nearestAncestor(InheritanceRelation.class) == null;
		}
	};

}

	protected Predicate<Pair<Type, Set<Type>>> annonymousTypeFilter(AnalysisOptions options, ObjectOrientedView view) {
		return new SafePredicate<Pair<Type, Set<Type>>>() {

			@Override
			public boolean eval(Pair<Type, Set<Type>> object)  {
				return object.first().nearestAncestorOrSelf(AnonymousType.class) == null;
			}

		};
	}
	
	protected Predicate<Pair<Type, Set<Type>>> annotationFilter(AnalysisOptions options, ObjectOrientedView view) {
		List<String> ignoredAnnotation = ignoredAnnotationTypes((DependencyOptions) options);
		Predicate<Pair<Type,Set<Type>>> filter = new True<>();
		for(String fqn: ignoredAnnotation) {
			try {
				Type type = view.findType(fqn);
				filter = new And<Pair<Type, Set<Type>>>(filter, new NoAnnotationOfType(type));
			} catch (LookupException e) {
			}
		}
		return filter;
	}	
	
	protected Predicate<Pair<Type, Set<Type>>> packageFilter(final AnalysisOptions options, ObjectOrientedView view) {
		return new AbstractPredicate<Pair<Type,Set<Type>>>() {

			@Override
			public boolean eval(Pair<Type, Set<Type>> object) throws Exception {
				return packagePredicate(options).eval(object.first());
			}
		};
	}
	
//	protected Predicate<CrossReference> packageFilter(final AnalysisOptions options) {
//		return new AbstractPredicate<CrossReference>() {
//
//			@Override
//			public boolean eval(CrossReference object) throws Exception {
//				return packagePredicate(options).eval(object.getElement());
//			}
//		};
//	}
	
	protected Predicate<Type> packagePredicate(AnalysisOptions options) {
		List<String> packageNames = packageNames((DependencyOptions) options);
		Predicate<Type> result;
		if(packageNames.isEmpty()) {
			result = new True<>();
		} else {
			result = new False<>();
			for(final String string: packageNames) {
				result = new Or<>(result,
						new SafePredicate<Type>() {

					@Override
					public boolean eval(Type object) {
						return new GlobPredicate(string,'.').eval(object.namespace().getFullyQualifiedName());
					}

				});
			}
		}
		return result;
	}
	
	protected Predicate<Pair<Type, Set<Type>>> hierarchyFilter(final AnalysisOptions options, final ObjectOrientedView view) {
		return new AbstractPredicate<Pair<Type,Set<Type>>>() {

			@Override
			public boolean eval(Pair<Type, Set<Type>> object) throws Exception {
				return hierarchyPredicate(options, view).eval(object.first());
			}
		};
	}
	protected Predicate<CrossReference> crossReferenceHierarchyFilter(final AnalysisOptions options, final ObjectOrientedView view) {
		return new AbstractPredicate<CrossReference>() {

			@Override
			public boolean eval(CrossReference object) throws Exception {
				Declaration element = object.getElement();
				Type t = element.nearestAncestorOrSelf(Type.class);
				if(t != null) {
					return hierarchyPredicate(options, view).eval(t);
				} else {
					return true;
				}
			}
		};
	}

	
	protected Predicate<Type> hierarchyPredicate(AnalysisOptions options, ObjectOrientedView view) {
		Predicate<Type> filter = new True<>();
		List<String> ignored = ignoredHierarchies((DependencyOptions) options);
		for(String fqn: ignored) {
			try {
				Type type = view.findType(fqn);
				filter = new And<>(filter,new NoSubtypeOf(type));
			} catch(LookupException exc) {
			}
		}
		filter = new And<>(new SourceType(),filter);
		return filter;
	}

	protected static class SourceType extends SafePredicate<Type> {

		@Override
		public boolean eval(Type type) {
			return type.view().isSource(type);
		}
		
	}
	
	protected List<String> ignoredAnnotationTypes(DependencyOptions options) {
		List<String> result = new ArrayList<String>();
		if(options.isIgnoreAnnotations()) {
			File file = options.getIgnoreAnnotations();
			result = readLines(file);
		}
		return result;

	}

	protected List<String> ignoredHierarchies(DependencyOptions options) {
		List<String> result = new ArrayList<String>();
		if(options.isIgnoreHierarchies()) {
			File file = options.getIgnoreHierarchies();
			result = readLines(file);
		}
		return result;
	}

	protected List<String> packageNames(DependencyOptions options) {
		List<String> result = new ArrayList<String>();
		if(options.isPackages()) {
			File file = options.getPackages();
			result = readLines(file);
		}
		return result;
	}



	protected List<String> readLines(File file) {
		List<String> result = new ArrayList<String>();
	BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			try {
				String line;
				while((line = reader.readLine()) != null) {
					result.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				reader.close();					
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return result;
	}
	
	@Override
	protected Class<? extends AnalysisOptions> optionsClass() {
		return DependencyOptions.class;
	}
	
	public static interface DependencyOptions extends AnalysisOptions {
		
		@Option(description="A file that contains the fully qualified name of types whose entire hierarchies should be ignored.") 
		File getIgnoreHierarchies();
		boolean isIgnoreHierarchies();

		@Option(description="A file that contains the fully qualified name of annotation types that cause a container to be ignored.") 
		File getIgnoreAnnotations();
		boolean isIgnoreAnnotations();

		@Option(description="A file that contains the fully qualified names of the packages that must be processed.") 
		File getPackages();
		boolean isPackages();
		
	}
}
