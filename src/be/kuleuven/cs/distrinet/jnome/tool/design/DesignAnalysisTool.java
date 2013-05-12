package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.io.IOException;
import java.io.OutputStreamWriter;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.eclipse.AnalysisTool;

public class DesignAnalysisTool extends AnalysisTool {

	public DesignAnalysisTool() {
		super("DesignChecker");
	}

	protected void check(Project project, OutputStreamWriter writer) throws LookupException, InputException, IOException {
		new DesignAnalyzer(project).analyze(writer);
	}

}
