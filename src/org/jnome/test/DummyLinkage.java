package org.jnome.test;

import java.io.File;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.linkage.ILinkage;
import chameleon.linkage.ILinkageFactory;
import chameleon.linkage.IParseErrorHandler;

public class DummyLinkage implements ILinkageFactory{

	public ILinkage createLinkage(File file) {
		return new ILinkage(){

			public IParseErrorHandler getParseErrorHandler() {
				return null;
			}

			public String getSource() {
				return null;
			}

			public void decoratePosition(int offset, int length, String dectype, Element el) {
			}

			public int getLineOffset(int i) {
				return 0;
			}

			public void addCompilationUnit(CompilationUnit cu) {
				
			}};
	}
}
