package jnome.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jnome.input.JavaMetaModelFactory;

import org.junit.Before;
import org.junit.Test;

import chameleon.input.ParseException;
import chameleon.support.tool.ArgumentParser;
import chameleon.support.tool.Arguments;

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

	public OutputTest(String extension) {
		_extension = extension;
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
		_args = new ArgumentParser(new JavaMetaModelFactory(),true).parse(arguments(), _extension);
	}

	@Test
	public void testOutput() {
		
	}
	
	public String getSeparator() {
	  return File.separator;
	}

}
