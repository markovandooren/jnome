package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;

import be.kuleuven.cs.distrinet.jnome.eclipse.AnalysisTool;

public class DesignAnalysisTool extends AnalysisTool {

	public DesignAnalysisTool() {
		super("DesignChecker");
	}

	@Override
	protected void check(Project project, OutputStreamWriter writer, AnalysisOptions options) throws LookupException, InputException, IOException {
		new DesignAnalyzer(project).analyze(writer);
	}

}
