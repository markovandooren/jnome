/*
 * Copyright 2000-2004 the Jnome development team.
 *
 * @author Marko van Dooren
 * @author Nele Smeets
 * @author Kristof Mertens
 * @author Jan Dockx
 *
 * This file is part of Jnome.
 *
 * Jnome is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Jnome is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Jnome; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.jnome.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Set;

import junit.framework.TestCase;

import org.jnome.input.JavaMetaModelFactory;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import chameleon.core.namespace.Namespace;

/**
 * @author marko
 */
public abstract class MetaModelTest extends TestCase {

	public MetaModelTest(String arg0) {
		super(arg0);
        //_files = new FileSet();
		_files = new ArrayList<String>();
        //include(_srcDirName, "**/*.java");
        addTestFiles();
	}
    
//    public void include(String dirName, String pattern) {
//      _files.include(new PatternPredicate(new File(dirName), new FileNamePattern(pattern)));
//    }
    public void include(String dirName) {
    	_files.add(dirName);
      }
    
    public abstract void addTestFiles();
  

    public void setUp() throws TokenStreamException, RecognitionException, MalformedURLException, FileNotFoundException, IOException, Exception {
      if(_mm == null) {
        //_mm = getMetaModelFactory().getMetaModel(_files.getFiles());
      	Set s = JavaMetaModelFactory.loadFiles(_files, ".java", true);
      	System.out.println("Found "+s.size()+" files.");
      	_mm = getMetaModelFactory().getMetaModel(new DummyLinkage(),s);
      }
    }
    
    public JavaMetaModelFactory getMetaModelFactory() {
      return new JavaMetaModelFactory();
    }
  
    /**
		 *
		 */
		public String getSeparator() {
		  return File.separator;
		}

		private String _srcDirName = "testsource/gen"; // <-- Change to directory of generated skeleton files.


    protected ArrayList<String> _files;

	protected Namespace _mm;

}
