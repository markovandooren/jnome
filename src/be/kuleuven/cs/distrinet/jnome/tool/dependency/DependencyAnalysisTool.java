package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.view.ObjectOrientedView;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.eclipse.AnalysisTool;
import be.kuleuven.cs.distrinet.rejuse.predicate.And;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;

import com.lexicalscope.jewel.cli.Option;

public class DependencyAnalysisTool extends AnalysisTool {

	public final static String NAME = "DependencyAnalysis";
	
	public DependencyAnalysisTool() {
		super(NAME);
	}
	
	
	
	@Override
	protected void writeProjectInfo(File root, OutputStreamWriter writer) throws IOException {
		
	}

	@Override
	protected void check(Project project, OutputStreamWriter writer, AnalysisOptions options) throws LookupException, InputException, IOException {
		ObjectOrientedView view = (ObjectOrientedView) project.views().get(0);
		Predicate<Type> filter = new True<>();
		List<String> ignored = ignored((DependencyOptions) options);
		for(String fqn: ignored) {
			try {
				Type testCase = view.findType(fqn);
				filter = new And(filter,new NoSubtypeOf(testCase));
			} catch(LookupException exc) {
			}
		}
		new DependencyAnalyzer(project,new And(new SourceType(),filter)).visualize(writer);
	}
	
	protected static class SourceType extends SafePredicate<Type> {

		@Override
		public boolean eval(Type type) {
			return type.view().isSource(type);
		}
		
	}

	protected List<String> ignored(DependencyOptions options) {
		List<String> result = new ArrayList<String>();
		if(options.isIgnoreHierarchies()) {
			File file = options.getIgnoreHierarchies();
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

		
	}
}
