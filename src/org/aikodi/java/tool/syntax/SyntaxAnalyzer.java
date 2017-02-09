package org.aikodi.java.tool.syntax;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.aikodi.chameleon.analysis.Analyzer;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.java.tool.syntax.ChainLength.LengthResult;
import org.aikodi.rejuse.exception.Handler;

public class SyntaxAnalyzer extends Analyzer {

  public SyntaxAnalyzer(Project project) {
    super(project);
  }


  public void analyseSyntax(OutputStreamWriter writer) throws InputException, IOException {
    LengthResult result = analysisResult(new ChainLength(), Handler.resume(), Handler.resume());
    writer.write(result.message());
  }

}
