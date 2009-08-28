package jnome.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import jnome.core.language.Java;
import jnome.input.JavaModelFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import chameleon.core.language.Language;
import chameleon.core.namespace.Namespace;
import chameleon.input.ModelFactory;

/**
 * @author marko
 */
public abstract class MetaModelTest {
	
	private static Logger _logger = Logger.getLogger("chameleon.test");
	static {
  	BasicConfigurator.configure();
	}
	
	public static Logger getLogger() {
		return _logger;
	}

	public MetaModelTest() {
		this(null);
	}
	
	public MetaModelTest(String arg0) {
//		super(arg0);
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

    @Before
    public void setUp() throws Exception {
    	setLogLevels();
      _language = new Java();
      _mm = _language.defaultNamespace();
      new JavaModelFactory(_language);
      //_mm = getMetaModelFactory().getMetaModel(_files.getFiles());
      Set s = JavaModelFactory.loadFiles(_files, ".java", true);
      System.out.println("Found "+s.size()+" files.");
      modelFactory().init(s);
    }
    
    @After
    public void tearDown() {
    	_mm = null;
    	_files = null;
    }
    
    public JavaModelFactory modelFactory() {
      return (JavaModelFactory) language().connector(ModelFactory.class);
    }
    
    public Language language() {
    	return _mm.language();
    }
  
    /**
		 *
		 */
		public String getSeparator() {
		  return File.separator;
		}

		private String _srcDirName = "testsource/gen"; // <-- Change to directory of generated skeleton files.


    protected ArrayList<String> _files;

    protected Java _language;
    
	  protected Namespace _mm;

}
