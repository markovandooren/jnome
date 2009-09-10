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

import chameleon.core.Config;
import chameleon.core.language.Language;
import chameleon.core.namespace.Namespace;
import chameleon.input.ModelFactory;
import chameleon.test.ModelProvider;

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

	 /**
	  * Create a new test that uses the given provider to create models
	  * for testing.
	  */
	/*@
	  @ public behavior
	  @
	  @ post provider() == provider;
    @ post baseRecursive();
    @ post customRecursive();
	  @*/
	 public MetaModelTest(ModelProvider provider) {
     Config.setCaching(true);
     _provider = provider;
	 }
	 
	 /**
	  * Return the model provider for this test.
	  */
	/*@
	  @ public behavior
	  @
	  @ post \result != null;
	  @*/
	 public ModelProvider provider() {
		 return _provider;
	 }
	 
	 private ModelProvider _provider;
	
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

   /**
    * Use the model provider to create a model, and store its language object
    * in this test.
    * 
    * This method also invokes setLogLevels() to set the log levels.
    * @throws Exception
    */
  /*@
    @ public behavior
    @
    @ // not quite correct
    @ post language() == provider().model();
    @*/
   @Before
   public void setUp() throws Exception {
    	setLogLevels();
      _language = provider().model();
    }
    
   @After
   public void tearDown() {
   	 _language = null;
   }
   
   /**
    * Return the language object of the model being tested.
    */
  /*@
    @ public behavior
    @
    @ post \result != null;
    @*/
   public Language language() {
   	 return _language;
   }
  
   private Language _language;
    
}
