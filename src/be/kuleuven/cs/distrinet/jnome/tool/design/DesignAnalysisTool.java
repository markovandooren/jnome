package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

import be.kuleuven.cs.distrinet.jnome.tool.AnalysisTool;
import be.kuleuven.cs.distrinet.jnome.tool.AnalysisTool.AnalysisOptions;

public class DesignAnalysisTool extends AnalysisTool {

	public DesignAnalysisTool() {
		super("DesignChecker");
	}
	
	private MessageFormatter formatter;

	@Override
	protected void check(Project project, OutputStreamWriter writer, AnalysisOptions options) throws LookupException, InputException, IOException {
		new DesignAnalyzer(project).analyze(writer, formatter);
	}
	
	/**
	* @{inheritDoc}
	*/
	@Override
	public void execute(String[] args) {
	  DesignAnalysisOptions options = (DesignAnalysisOptions) parseArguments(args);
	  if(options.isFormatter()) {
	    String formatterName = options.getFormatter();
	    if(formatterName.equals("bbcode")) {
	      formatter = new BBCodeFormatter();
	    } else {
	      formatter = new DefaultFormatter();
	    }
	  } else {
	    formatter = new DefaultFormatter();
	  }
	  super.execute(args);
	}

  /**
   * @{inheritDoc}
   */
  @Override
  protected void computeStats(Project project, OutputStreamWriter writer, OutputStreamWriter cycleWriter, AnalysisOptions options)
      throws LookupException, InputException, IOException {
    new DesignAnalyzer(project).analyze(writer, formatter);
  }

  /**
  * @{inheritDoc}
  */
  @Override
  protected Class<? extends AnalysisOptions> optionsClass() {
    return DesignAnalysisOptions.class;
  }
  
  @CommandLineInterface(application="Analysis")
  public static interface DesignAnalysisOptions extends AnalysisOptions {
    @Option(description="The formatter that is used to format the error messages.") 
    String getFormatter();
    boolean isFormatter();

  }
}
