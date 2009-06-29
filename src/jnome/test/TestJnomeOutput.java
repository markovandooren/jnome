package jnome.test;

import java.io.File;

public class TestJnomeOutput extends OutputTest {

	public TestJnomeOutput() {
		super(".java","output"+File.separator);
	}

	@Override
	public void addTestFiles() {
		include("testsource"+getSeparator()+"gen"+getSeparator());
		include("testsource"+getSeparator()+"jnome"+getSeparator()+"src"+getSeparator());
	}

}
