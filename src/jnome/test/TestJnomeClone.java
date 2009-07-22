package jnome.test;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceOrTypeReference;

public class TestJnomeClone extends TestClone {

	@Override
	public List<Namespace> getTestNamespaces() throws LookupException {
		List<Namespace> result = new ArrayList<Namespace>();
		NamespaceOrTypeReference ref = new NamespaceOrTypeReference("org.jnome");
		ref.setUniParent(_mm);
		result.add((Namespace) ref.getNamespaceOrType());
		return result;
	}

	@Override
	public void addTestFiles() {
		include("testsource"+getSeparator()+"gen"+getSeparator());
		include("testsource"+getSeparator()+"jregex"+getSeparator());
		include("testsource"+getSeparator()+"antlr-2.7.2"+getSeparator()+"antlr"+getSeparator());
		include("testsource"+getSeparator()+"jnome"+getSeparator()+"src"+getSeparator());
		include("testsource"+getSeparator()+"jutil"+getSeparator()+"src"+getSeparator());
	  include("testsource"+getSeparator()+"junit3.8.1"+getSeparator()+"src"+getSeparator());
		include("testsource"+getSeparator()+"jakarta-log4j-1.2.8"+getSeparator()+"src"+getSeparator()+"java"+getSeparator());
	}

}
