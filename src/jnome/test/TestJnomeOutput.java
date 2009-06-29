package jnome.test;

public class TestJnomeOutput extends OutputTest {

	public TestJnomeOutput() {
		super(".java");
	}

	@Override
	public void addTestFiles() {
		include("testsource"+getSeparator()+"gen");
		include("testsource"+getSeparator()+"jnome"+getSeparator()+"src"+getSeparator());
	}

}
