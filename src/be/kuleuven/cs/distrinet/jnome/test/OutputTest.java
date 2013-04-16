package be.kuleuven.cs.distrinet.jnome.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.jnome.input.EagerJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

import org.junit.Before;
import org.junit.Test;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.RegularNamespaceFactory;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.input.ParseException;
import be.kuleuven.cs.distrinet.chameleon.support.tool.Arguments;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;

/**
 * This is an abstract class to facilitate testing of both the output and the input. These tests output a model,
 * and compile the result. If elements are not added to the model by the input, or if the output is faulty,
 * the resulting code can probably not be compiled.
 * 
 * In the future this should also involve running and available tests on the generated code.
 * 
 * @author Marko
 */
public abstract class OutputTest {

	public OutputTest(String extension, String output) {
		_extension = extension;
		setOutput(output);
	}

	
	private Arguments _args;
	
	private final String _extension;

	private String _outputDir;
	
	public String getExtension() {
		return _extension;
	}
	
	private String[] arguments() {
		List<String> inputs = inputs();
		if(hasOutput()) {
		  inputs.add(0, output());
		}
		return inputs.toArray(new String[0]);
	}
	
	public void setOutput(String outputDir) {
		_outputDir = outputDir;
	}
	
	public String output() {
		return _outputDir;
	}
	
	
	private boolean hasOutput() {
		return _outputDir != null;
	}
	
	
	private List<String> _inputs = new ArrayList<String>();
	
	public List<String> inputs() {
		return new ArrayList<String>(_inputs);
	}
	
	public void include(String input) {
		_inputs.add(input);
	}
	
	public abstract void addTestFiles();
	
	@Before
	public void setUp() throws MalformedURLException, FileNotFoundException, ParseException, IOException, Exception {
		addTestFiles();
		Java language = new JavaLanguageFactory().create();
		Project project = new Project("output test", new JavaView(new RootNamespace(new RegularNamespaceFactory()),language), new File("."));
		EagerJavaFileInputSourceFactory inputSourceFactory = new EagerJavaFileInputSourceFactory();
		//FIXME I think this entire test class has to be deleted.
		throw new Error();
//		_args = new ArgumentParser(project,true).parse(arguments(), _extension,inputSourceFactory);
	}

	@Test
	public void testOutput() {
		
	}
	
	public String getSeparator() {
	  return File.separator;
	}

}