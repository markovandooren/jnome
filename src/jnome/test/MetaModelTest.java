package jnome.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Set;

import jnome.input.JavaMetaModelFactory;
import junit.framework.TestCase;

import org.antlr.runtime.RecognitionException;

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
  

    public void setUp() throws Exception {
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
