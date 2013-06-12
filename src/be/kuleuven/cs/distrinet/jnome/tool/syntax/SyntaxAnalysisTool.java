package be.kuleuven.cs.distrinet.jnome.tool.syntax;

import java.io.IOException;
import java.io.OutputStreamWriter;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.eclipse.AnalysisTool;

public class SyntaxAnalysisTool extends AnalysisTool {

	public SyntaxAnalysisTool() {
		super("SyntaxAnalysis");
	}

	@Override
	protected void check(Project project, OutputStreamWriter writer, AnalysisOptions options) throws LookupException, InputException, IOException {
		new SyntaxAnalyzer(project).analyseSyntax(writer);
	}

}
