package jnome.test;

import org.junit.Test;

/**
 * This is an abstract class to facilitate testing of both the output and the input. These tests output a model,
 * and compile the result. If elements are not added to the model by the input, or if the output is faulty,
 * the resulting code can probably not be compiled.
 * 
 * In the future this should also involve running and available tests on the generated code.
 * 
 * @author Marko
 */
public abstract class OutputTest extends MetaModelTest {

	public OutputTest(String name) {
		super(name);
	}


	@Test
	public void testOutput() {
		
	}
}
