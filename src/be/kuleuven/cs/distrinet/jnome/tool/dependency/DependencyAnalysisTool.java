package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.oo.analysis.dependency.NoSubtypeOf;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.oo.view.ObjectOrientedView;
import org.aikodi.chameleon.util.Pair;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;

import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.eclipse.AnalysisTool;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.False;
import be.kuleuven.cs.distrinet.rejuse.predicate.GlobPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

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
		UniversalPredicate<Type,Nothing> filter = hierarchyFilter(options, view)
			.and(annotationFilter(options, view))
			.and(anonymousTypeFilter(options,view))
      .and(packageFilter(options,view));
		UniversalPredicate<CrossReference<?>,Nothing> crossReferenceFilter =
				crossReferenceSourceFilter()
				.and(crossReferenceNonInheritanceFilter())
				.and(crossReferenceHierarchyFilter(options,view));
		new JavaDependencyAnalyzer(project,filter,crossReferenceFilter).visualize(writer);
	}

	protected UniversalPredicate<CrossReference<?>,Nothing> crossReferenceSourceFilter() {
//		return new True<>();
		return (UniversalPredicate) new IsSource();
	
	}

	protected UniversalPredicate<CrossReference<?>,Nothing> crossReferenceNonInheritanceFilter() {
	return new UniversalPredicate(CrossReference.class) {

		@Override
		public boolean uncheckedEval(Object object) {
			return ((CrossReference)object).nearestAncestor(InheritanceRelation.class) == null;
		}
	};

}

	protected UniversalPredicate<Type,Nothing> anonymousTypeFilter(AnalysisOptions options, ObjectOrientedView view) {
		return new UniversalPredicate<Type,Nothing>(Type.class) {

			@Override
			public boolean uncheckedEval(Type type)  {
				return type.nearestAncestorOrSelf(AnonymousType.class) == null;
			}

		};
	}
	
	protected UniversalPredicate<? super Type,Nothing> annotationFilter(AnalysisOptions options, ObjectOrientedView view) {
		List<String> ignoredAnnotation = ignoredAnnotationTypes((DependencyOptions) options);
		UniversalPredicate<? super Type,Nothing> filter = new True();
		for(String fqn: ignoredAnnotation) {
			try {
				Type type = view.findType(fqn);
				filter = filter.and(new NoAnnotationOfType(type)).makeUniversal(Type.class);
			} catch (LookupException e) {
			}
		}
		return filter;
	}	
	
	protected UniversalPredicate<Type,Nothing> packageFilter(final AnalysisOptions options, ObjectOrientedView view) {
		return new UniversalPredicate<Type,Nothing>(Type.class) {

			@Override
			public boolean uncheckedEval(Type type) {
				return packagePredicate(options).eval(type);
			}
		};
	}
	
	protected UniversalPredicate<? super Type,Nothing> packagePredicate(AnalysisOptions options) {
		List<String> packageNames = packageNames((DependencyOptions) options);
		UniversalPredicate<? super Type,Nothing> result;
		if(packageNames.isEmpty()) {
			result = new True();
		} else {
			result = new False();
			for(final String string: packageNames) {
				result = result.or(
						new UniversalPredicate<Type,Nothing>(Type.class) {

					@Override
					public boolean uncheckedEval(Type object) {
						return new GlobPredicate(string,'.').eval(object.namespace().fullyQualifiedName());
					}

				}).makeUniversal(Type.class);
			}
		}
		return result;
	}
	
	protected UniversalPredicate<Type,Nothing> hierarchyFilter(final AnalysisOptions options, final ObjectOrientedView view) throws LookupException {
		final UniversalPredicate<Type, Nothing> hierarchyPredicate = hierarchyPredicate(options, view);
		return new UniversalPredicate<Type,Nothing>(Type.class) {

			@Override
			public boolean uncheckedEval(Type type)  {
				return hierarchyPredicate.eval(type);
			}
		};
	}
	
	protected UniversalPredicate<CrossReference,Nothing> crossReferenceHierarchyFilter(final AnalysisOptions options, final ObjectOrientedView view) throws LookupException {
		final UniversalPredicate<Type, Nothing> hierarchyPredicate = hierarchyPredicate(options, view);
		UniversalPredicate<CrossReference,LookupException> unguarded = new UniversalPredicate<CrossReference,LookupException>(CrossReference.class) {

			@Override
			public boolean uncheckedEval(CrossReference object) throws LookupException{
				Declaration element = object.getElement();
				Type t = element.nearestAncestorOrSelf(Type.class);
				if(t != null) {
					return hierarchyPredicate.eval(t);
				} else {
					return true;
				}
			}
		};
		UniversalPredicate<CrossReference, Nothing> result = unguarded.guard(true);
		return result;
	}

	
	protected UniversalPredicate<Type,Nothing> hierarchyPredicate(AnalysisOptions options, ObjectOrientedView view) throws LookupException {
		UniversalPredicate<Type,Nothing> filter = SOURCE_TYPE;
		List<String> ignored = ignoredHierarchies((DependencyOptions) options);
		for(String fqn: ignored) {
				Type type = view.findType(fqn);
				filter = filter.and(new NoSubtypeOf(type));
		}
		return filter;
	}

	protected static class IsSource extends UniversalPredicate<CrossReference,Nothing> {
		protected IsSource() {
			super(CrossReference.class);
		}

		@Override
		public boolean uncheckedEval(CrossReference object) {
			Declaration d;
			try {
				d = ((CrossReference<?>)object).getElement();
				return d.view().isSource(d);
			} catch(Exception e) {
				e.printStackTrace();
				return true;
			}
		}
	}

	public final static UniversalPredicate<Type,Nothing> SOURCE_TYPE = UniversalPredicate.of(Type.class, t -> t.view().isSource(t));
	
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
