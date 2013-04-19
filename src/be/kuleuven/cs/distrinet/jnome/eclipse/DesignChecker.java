package be.kuleuven.cs.distrinet.jnome.eclipse;

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

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.tool.DesignAnalyser;
import be.kuleuven.cs.distrinet.jnome.tool.Tool;
import be.kuleuven.cs.distrinet.rejuse.io.FileUtils;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

public class DesignChecker extends Tool {

	public DesignChecker() {
		super("DesignChecker");
	}

	/**
	 * 
	 * @param args argument list containing the root path if it is given explicitly. 
	 *             Otherwise, the directory from which the tool is run is used.
	 * @throws InputException 
	 * @throws ConfigException 
	 * @throws LookupException 
	 */
	public static void main(String[] args) throws LookupException, ConfigException, InputException {
		new DesignChecker().execute(args);
	}
	
	public void execute(String[] args) {
//		for(String arg: args) {
//			System.out.println(arg);
//		}
		try
	  {
			DesignCheckerOptions result = CliFactory.parseArguments(DesignCheckerOptions.class, args);
	    File root = new File(result.getRoot());
	    Map containerConfiguration = getContainerConfiguration(result);
			Project project = new JavaEclipseProjectConfig(root, containerConfiguration).project();
			OutputStream stream;
			if(result.isOut()) {
				File output = new File(result.getOut());
				stream = new FileOutputStream(output);
			} else {
				stream = System.out;
			}
	    OutputStreamWriter writer = new OutputStreamWriter(stream);
			writer.write("Checking project in "+root.getAbsolutePath()+"\n");
			writer.flush();
			new DesignAnalyser(project).findViolations(writer);
			writer.close();
			stream.close();
	  }
	  catch(ArgumentValidationException e) {
	    throw new IllegalArgumentException("The arguments could not be processed.");
	  }
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Map getContainerConfiguration(DesignCheckerOptions result) {
		Map containerConfiguration = new HashMap<String,String>();
		if(result.getContainers() != null) {
		  File containerConfigFile = new File(result.getContainers());
		  Properties properties = new Properties();
		  try {
				properties.load(new FileInputStream(containerConfigFile));
				containerConfiguration = properties;
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("The given container configuration file is not found.");
			} catch (IOException e) {
				throw new IllegalArgumentException("The given container configuration file was found, but could not be read.");
			}
		  File parentFile = containerConfigFile.getParentFile();
		  if(parentFile == null) {
		  	parentFile = new File(".");
		  }
			makeRelativePathsAbsoluteRelativeToConfigFile(containerConfiguration, parentFile);
		}
		return containerConfiguration;
	}
	
	private void makeRelativePathsAbsoluteRelativeToConfigFile(Map<Object,Object> map, File rootForRelativePaths) {
		for(Map.Entry entry: map.entrySet()) {
			String path = (String) entry.getValue();
			String key = (String) entry.getKey();
			String newPath = FileUtils.absolutePath(path, rootForRelativePaths);
			map.put(key, newPath);
		}
	}
	
	public static interface DesignCheckerOptions {
		
		@Option(shortName="r", defaultValue="./") String getRoot();
		@Option(shortName="c") String getContainers();
		@Option(shortName="o") String getOut();
		boolean isOut();
	}
}
