package jnome.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import jnome.input.JavaMetaModelFactory;
import junit.framework.TestCase;
import chameleon.core.namespace.Namespace;

/**
 * @author marko
 */
public abstract class MetaModelTest extends TestCase {
	
	private static Logger _logger = Logger.getLogger("chameleon.test");
	
	public static Logger getLogger() {
		return _logger;
	}

	public MetaModelTest(String arg0) {
		super(arg0);
		_files = new ArrayList<String>();
     addTestFiles();
	}
    
//    public void include(String dirName, String pattern) {
//      _files.include(new PatternPredicate(new File(dirName), new FileNamePattern(pattern)));
//    }
	
    public void include(String dirName) {
    	_files.add(dirName);
    }
    
    public abstract void addTestFiles();

    /**
     * This method is invoked during setup to set the levels of the loggers.
     * It allows subclasses to easily changes those levels if tests fail, without
     * having to change this class.
     * 
     * The default behavior is to leave log levels untouched. They are DEBUG by default.
     */
    public void setLogLevels() {
    	// do nothing by default
    }

    public void setUp() throws Exception {
    	BasicConfigurator.configure();
    	setLogLevels();
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
