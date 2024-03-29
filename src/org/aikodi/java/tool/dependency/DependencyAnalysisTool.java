package org.aikodi.java.tool.dependency;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.oo.analysis.dependency.NoSubtypeOf;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.oo.view.ObjectOrientedView;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.java.core.type.AnonymousType;
import org.aikodi.java.tool.AnalysisTool;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.predicate.GlobPredicate;
import org.aikodi.rejuse.predicate.UniversalPredicate;

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
	  JavaDependencyAnalyzer analyzer = analyzer(project, options);
    analyzer.visualize(writer);
	}


  /**
   * @param project
   * @param options
   * @return
   * @throws LookupException
   */
  private JavaDependencyAnalyzer analyzer(Project project, AnalysisOptions options) throws LookupException {
    UniversalPredicate<Type,Nothing> troo = UniversalPredicate.of(Type.class, t->true);
		ObjectOrientedView view = (ObjectOrientedView) project.views().get(0);
    UniversalPredicate<Type,Nothing> filter = hierarchyPredicate(options, view)
			.and(annotationFilter(options, view))
			.and(anonymousTypeFilter(options,view))
      .and(packageFilter(options,view));
		UniversalPredicate<CrossReference<?>,Nothing> crossReferenceFilter =
				crossReferenceSourceFilter()
				.and(crossReferenceNonInheritanceFilter())
				.and(crossReferenceHierarchyFilter(options,view))
				.and(crossReferenceTargetType(options,view))
				;
    JavaDependencyAnalyzer analyzer = new JavaDependencyAnalyzer(project,filter,crossReferenceFilter, troo);
    return analyzer;
  }

	  protected void computeStats(Project project, OutputStreamWriter writer, OutputStreamWriter cycleWriter, AnalysisOptions options) throws LookupException, InputException, IOException {
	     JavaDependencyAnalyzer analyzer = analyzer(project, options);
	     analyzer.computeStats(writer, cycleWriter);
	  }

	protected UniversalPredicate<CrossReference<?>,Nothing> crossReferenceSourceFilter() {
//		return new True<>();
		return (UniversalPredicate) new IsSource();
	
	}

	protected UniversalPredicate<CrossReference<?>,Nothing> crossReferenceNonInheritanceFilter() {
	return new UniversalPredicate(CrossReference.class) {

		@Override
		public boolean uncheckedEval(Object object) {
			return ((CrossReference)object).lexical().nearestAncestor(InheritanceRelation.class) == null;
		}
	};

}

	protected UniversalPredicate<Type,Nothing> anonymousTypeFilter(AnalysisOptions options, ObjectOrientedView view) {
		return new UniversalPredicate<Type,Nothing>(Type.class) {

			@Override
			public boolean uncheckedEval(Type type)  {
				return type.lexical().nearestAncestorOrSelf(AnonymousType.class) == null;
			}

		};
	}
	
	protected UniversalPredicate<? super Type,Nothing> annotationFilter(AnalysisOptions options, ObjectOrientedView view) {
		List<String> ignoredAnnotation = ignoredAnnotationTypes((DependencyOptions) options);
		UniversalPredicate<? super Type,Nothing> filter = UniversalPredicate.isTrue();
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
			result = UniversalPredicate.isTrue();
		} else {
			result = UniversalPredicate.isFalse();
			for(final String string: packageNames) {
				result = result.or(
						new UniversalPredicate<Type,Nothing>(Type.class) {

					@Override
					public boolean uncheckedEval(Type object) {
						return new GlobPredicate(string,'.').eval(object.lexical().nearestAncestor(NamespaceDeclaration.class).namespace().fullyQualifiedName());
					}

				}).makeUniversal(Type.class);
			}
		}
		return result;
	}
	
	protected UniversalPredicate<CrossReference,Nothing> crossReferenceHierarchyFilter(final AnalysisOptions options, final ObjectOrientedView view) throws LookupException {
		final UniversalPredicate<Type, Nothing> hierarchyPredicate = hierarchyPredicate(options, view);
		UniversalPredicate<CrossReference,LookupException> unguarded = new UniversalPredicate<CrossReference,LookupException>(CrossReference.class) {

			@Override
			public boolean uncheckedEval(CrossReference object) throws LookupException{
				Declaration element = object.getElement();
				Type t = element.lexical().nearestAncestorOrSelf(Type.class);
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

	protected UniversalPredicate<CrossReference,Nothing> crossReferenceTargetType(final AnalysisOptions options, final ObjectOrientedView view) throws LookupException {
		final UniversalPredicate<Type, Nothing> targetPredicate = targetTypePredicate(options, view);
		UniversalPredicate<CrossReference,LookupException> unguarded = new UniversalPredicate<CrossReference,LookupException>(CrossReference.class) {

			@Override
			public boolean uncheckedEval(CrossReference object) throws LookupException{
				Declaration element = object.getElement();
				Type t = element.lexical().nearestAncestorOrSelf(Type.class);
				if(t != null) {
					return targetPredicate.eval(t);
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

	protected UniversalPredicate<Type,Nothing> targetTypePredicate(AnalysisOptions options, ObjectOrientedView view) throws LookupException {
		UniversalPredicate<Type,Nothing> filter = SOURCE_TYPE;
		List<String> ignored = ignoredTargets((DependencyOptions) options);
		for(String fqn: ignored) {
				Type type = view.findType(fqn);
				filter = filter.and(new UniversalPredicate<Type,Nothing>(Type.class) {

					@Override
					public boolean uncheckedEval(Type t) throws Nothing {
						return ! t.getFullyQualifiedName().equals(fqn);
					}
					
				});
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

	protected List<String> ignoredTargets(DependencyOptions options) {
		List<String> result = new ArrayList<String>();
		if(options.isIgnoreTargets()) {
			File file = options.getIgnoreTargets();
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

    @Option(description="File that contains the fully qualified names of ignored targets.")
    File getIgnoreTargets();
    boolean isIgnoreTargets();

		@Option(description="A file that contains the fully qualified names of the packages that must be processed.") 
		File getPackages();
		boolean isPackages();
		
	}
}
