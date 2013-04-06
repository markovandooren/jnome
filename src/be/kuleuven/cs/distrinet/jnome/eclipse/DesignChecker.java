package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.tool.DesignAnalyser;
import be.kuleuven.cs.distrinet.jnome.tool.Tool;

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
	    System.out.println("Checking project in "+root.getAbsolutePath());
	    Map containerConfiguration = getContainerConfiguration(result);
			Project project = new JavaEclipseProjectConfig(root, containerConfiguration).project();
			new DesignAnalyser(project).findViolations();
	  }
	  catch(ArgumentValidationException e) {
	    throw new IllegalArgumentException("The arguments could not be processed.");
	  }
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Map getContainerConfiguration(DesignCheckerOptions result) {
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
		}
		return containerConfiguration;
	}
	
	public static interface DesignCheckerOptions {
		
		@Option(shortName="r", defaultValue="./") String getRoot();
		@Option(shortName="c") String getContainers();
	}
}
