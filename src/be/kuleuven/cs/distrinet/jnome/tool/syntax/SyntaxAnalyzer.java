package be.kuleuven.cs.distrinet.jnome.tool.syntax;

import java.io.IOException;
import java.io.OutputStreamWriter;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analyzer;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.tool.syntax.ChainLength.LengthResult;

public class SyntaxAnalyzer extends Analyzer {

	public SyntaxAnalyzer(Project project) {
		super(project);
	}


	public void analyseSyntax(OutputStreamWriter writer) throws InputException, IOException {
		LengthResult result = analysisResult(new ChainLength());
		writer.write(result.message());
	}
	

}
