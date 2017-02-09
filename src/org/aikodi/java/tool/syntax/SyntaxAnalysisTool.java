package org.aikodi.java.tool.syntax;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.java.tool.AnalysisTool;

public class SyntaxAnalysisTool extends AnalysisTool {

	public SyntaxAnalysisTool() {
		super("SyntaxAnalysis");
	}

	@Override
	protected void check(Project project, OutputStreamWriter writer, AnalysisOptions options) throws LookupException, InputException, IOException {
		new SyntaxAnalyzer(project).analyseSyntax(writer);
	}

  /**
   * @{inheritDoc}
   */
  @Override
  protected void computeStats(Project project, OutputStreamWriter writer,OutputStreamWriter cycleWriter,  AnalysisOptions options)
      throws LookupException, InputException, IOException {
    
  }

}
