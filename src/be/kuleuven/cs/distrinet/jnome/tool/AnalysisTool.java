package be.kuleuven.cs.distrinet.jnome.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.LanguageRepository;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.Workspace;
import org.aikodi.chameleon.workspace.XMLProjectLoader;
import org.aikodi.rejuse.io.FileUtils;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7LanguageFactory;
import be.kuleuven.cs.distrinet.jnome.input.eclipse.JavaEclipseProjectConfig;

public abstract class AnalysisTool extends Tool {

  /**
   * Create a new analysis tool with the given name.
   * 
   * @param name The name of the analysis.
   */
  protected AnalysisTool(String name) {
    super(name);
  }

  public void execute(String[] args) {
    try
    {
      AnalysisOptions options = parseArguments(args);
      if(options.isHelp()) {
        printHelp();
        System.exit(0);
      }
      LanguageRepository repository = new LanguageRepository();
      repository.add(new Java7LanguageFactory().create());
      Workspace workspace = new Workspace(repository);
      File root = new File(options.getRoot());
      Map<String,String> containerConfiguration = getConfiguration(options.getContainers());
      Map<String,String> environmentConfiguration = getConfiguration(options.getEnvironment());
      Project project;
      if(options.isConfiguration()) {
        if(options.getConfiguration().equals("eclipse")) {
          project= new JavaEclipseProjectConfig(root, containerConfiguration,environmentConfiguration).project();
        } else if(options.getConfiguration().equals("xml")) {
          File xmlFile = new File(root,"project.xml");
          project = new XMLProjectLoader(workspace).project(xmlFile, null);
        } else {
          throw new IllegalArgumentException();
        }
      } else {
        project= new JavaEclipseProjectConfig(root, containerConfiguration,environmentConfiguration).project();
      }
      OutputStream stream;
      if(options.isOut()) {
        File output = new File(options.getOut());
        stream = new FileOutputStream(output);
      } else {
        stream = System.out;
      }
      OutputStreamWriter writer = new OutputStreamWriter(stream);
      OutputStream statStream;
      if(options.isStats()) {
        File output = new File(options.getStats());
        statStream = new FileOutputStream(output);
      } else {
        statStream = System.out;
      }
      OutputStreamWriter statWriter = new OutputStreamWriter(statStream);
      OutputStream cycleStream;
      if(options.isCycles()) {
        File output = new File(options.getCycles());
        cycleStream = new FileOutputStream(output);
      } else {
        cycleStream = System.out;
      }
      OutputStreamWriter cycleWriter = new OutputStreamWriter(cycleStream);
      
      writeProjectInfo(root, writer);
      
      check(project, writer, options);
      
      computeStats(project,statWriter,cycleWriter,options);
      writer.close();
      stream.close();
    }
    catch(ArgumentValidationException e) {
      printHelp();
      System.exit(0);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected AnalysisOptions parseArguments(String[] args) {
    return CliFactory.parseArguments(optionsClass(), args);
  }

  protected Class<? extends AnalysisOptions> optionsClass() {
    return AnalysisOptions.class;
  }

  protected Cli<? extends AnalysisOptions> createCLI() {
    return CliFactory.createCli(optionsClass());
  }

  protected void writeProjectInfo(File root, OutputStreamWriter writer) throws IOException {
    writer.write("Analyzing project in "+root.getAbsolutePath()+"\n");
    writer.flush();
  }

  protected abstract void check(Project project, OutputStreamWriter writer, AnalysisOptions options) throws LookupException, InputException, IOException;

  /**
   * FIXME TODO This is an ugly short-term hack.
   * @param project
   * @param writer
   * @param cycleWriter
   * @param options
   * @throws LookupException
   * @throws InputException
   * @throws IOException
   */
  protected abstract void computeStats(Project project, OutputStreamWriter writer, OutputStreamWriter cycleWriter, AnalysisOptions options) throws LookupException, InputException, IOException;

  private void printHelp() {
    Cli<? extends AnalysisOptions> cli = createCLI();
    System.out.println(cli.getHelpMessage());
  }

  private Map<String,String> getConfiguration(String fileName) {
    Map<String,String> containerConfiguration = new HashMap<String,String>();
		if(fileName != null) {
      File containerConfigFile = new File(fileName);
      Properties properties = new Properties();
      try {
        properties.load(new FileInputStream(containerConfigFile));
        properties.forEach((k,v) -> {containerConfiguration.put((String)k,(String)v);}); 
      } catch (FileNotFoundException e) {
        throw new IllegalArgumentException("The given configuration file is not found: " + fileName);
      } catch (IOException e) {
        throw new IllegalArgumentException("The given container configuration file was found, but could not be read: "+ fileName);
      }
      File parentFile = containerConfigFile.getParentFile();
      if(parentFile == null) {
        parentFile = new File(".");
      }
      makeRelativePathsAbsoluteRelativeToConfigFile(containerConfiguration, parentFile);
    }
    return containerConfiguration;
  }

  private void makeRelativePathsAbsoluteRelativeToConfigFile(Map<String,String> map, File rootForRelativePaths) {
    for(Map.Entry<String,String> entry: map.entrySet()) {
      String path = entry.getValue();
      String key = entry.getKey();
      String newPath = FileUtils.absolutePath(path, rootForRelativePaths);
      map.put(key, newPath);
    }
  }

  @CommandLineInterface(application="Analysis")
  public static interface AnalysisOptions {

    @Option(defaultValue="./", description="The directory that contains the Eclipse .project and .classpath files. Note that the Eclipse project should have no compile errors.") 
    String getRoot();

    @Option(description="Libraries that are built into Eclipse, such as Junit, are marked in the classpath as containers. To function propertly, the tool must be able to find the jar files that correspond to these containers. The given file should be a property file that contains the mapping of Eclipse container names to jar files. Relative paths are resolved relative to the given file.") 
    String getContainers();
    boolean isContainers();

    @Option(description="File that contains the mapping of the environment variables")
    String getEnvironment();
    boolean isEnvironment();
    
    @Option(description="The name of the output file for the dependency graph. If no file is given, the output is written to the standard output stream.") 
    String getOut();
    boolean isOut();

    @Option(description="The name of the output file for the statistics. If no file is given, the output is written to the standard output stream.") 
    String getStats();
    boolean isStats();

    @Option(description="The name of the output file for the dependency cycles. If no file is given, the output is written to the standard output stream.") 
    String getCycles();
    boolean isCycles();

    @Option(description="Display this help and exit.")
    boolean isHelp();
    
    @Option(description="The project configuration method: xml, eclipse") 
    String getConfiguration();
    boolean isConfiguration();
    
    
  }
}
