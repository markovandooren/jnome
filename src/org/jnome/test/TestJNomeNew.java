package org.jnome.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jnome.input.JavaMetaModelFactory;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import chameleon.core.MetamodelException;
import chameleon.core.expression.VariableReference;
import chameleon.core.method.Method;
import chameleon.core.method.RegularImplementation;
import chameleon.core.namespace.Namespace;
import chameleon.core.statement.Block;
import chameleon.core.type.Type;
import chameleon.core.variable.LocalVariable;
import chameleon.core.variable.MemberVariable;
import chameleon.core.variable.Variable;
import chameleon.support.modifier.Private;
import chameleon.support.statement.LocalVariableDeclarationStatement;


/**
 * @author Tim Laeremans
 */
public class TestJNomeNew {
	//protected FileSet _files = new FileSet();
	Namespace unnamed;
	
//    public void include(String dirName, String pattern) {
//        _files.include(new PatternPredicate(new File(dirName), new FileNamePattern(pattern)));
//      }
    
    public TestJNomeNew() throws TokenStreamException, RecognitionException, MalformedURLException, FileNotFoundException, IOException, MetamodelException, Exception{
    	ArrayList al = new ArrayList();
		
		//String path1 = "test\\TestTim.java";
    	//String path1 = "test\\Simple.java";
		String path1 = "test\\A.java";
		//String path2 = "testsource\\gen\\java\\lang\\Object.java";
		String path2 = "C:\\Chameleon\\testsource\\gen\\";
		String path3 = "C:\\Chameleon\\testsource\\antlr-2.7.2\\antlr";
		String path4 = "testsource\\jregex\\";
		String path5 = "testsource\\unit\\";
		String path6 = "C:\\test\\";
		
		
		al.add(path1);
		al.add(path2);
		//al.add(path3);
		//al.add(path3);//antlr: geeft statement-error
		//al.add(path4);//jregex: geeft statement-error
		//al.add(path5);//unit: geeft statement-error
		JavaMetaModelFactory mmf = new JavaMetaModelFactory();
		
		Set files = JavaMetaModelFactory.loadFiles(al, ".java", true);
		System.out.println("-- Got all files ...  --");
		unnamed = new  JavaMetaModelFactory().getMetaModel(new DummyLinkage(),files);
//		
		
		
//		testPackage();
//		testVariableLookup();
//		testMethodM();
		
		Set types = unnamed.getAllTypes();
		Iterator i = types.iterator();
//		while(i.hasNext()){
//			Type t = (Type)i.next();
//			if(t.getName().equals("TestTim")){
//				System.out.println("Type: " + (t).getName());
//				List methods = t.getMethods();
//				Iterator mi = methods.iterator();
//				while(mi.hasNext()){
//					Method m = (Method) mi.next();
//					System.out.println("Method: " + m.getName() + " nb args: " + m.getNbParameters());
//					
//					List statements = m.getAllExpressions();
//					Iterator si = statements.iterator();
//					while(si.hasNext()){
//						Expression s = (Expression) si.next();
//						System.out.println("Statement: " + s.toString());
//					}
//				}
//				try {
//					List vars = t.getVariables();
//					Iterator vi = vars.iterator();
//					while(vi.hasNext()){
//						Variable v = (Variable) vi.next();
//						String smodifiers = v.getModifiers().toArray().toString();
//						Type tt = v.getType();
//						String stype = tt.getFullyQualifiedName();
//						String sname = v.getName();
//						System.out.println("Var: " + smodifiers + " " + stype + " " + sname);
//					}
//				} catch (MetamodelException e) {
//					e.printStackTrace();
//				}
//				
//			}
//		}
		System.out.println("-- FINISHED --");
		System.out.println("\n");
    }
    
    public void testPackage() throws MetamodelException {
        Type A = (Type)unnamed.findTypeLocally("A");
        if(A.getFullyQualifiedName().equals("A"))
        	System.out.println("testPackage: OK1");
        if(A.getNamespace().equals(unnamed))
        	System.out.println("testPackage: OK2");
      }
    
    public void testVariableLookup() throws MetamodelException {
        Type A = (Type)unnamed.findTypeLocally("A");
        MemberVariable var = A.getVariable("a");
        if(var.is(new Private()))
        	System.out.println("testVariableLookup: OK1");
        if(var.modifiers().size() == 1)
        	System.out.println("testVariableLookup: OK2");
      }
    
    public void testMethodM() throws MetamodelException {
        Type A = (Type)unnamed.findTypeLocally("A");
        Method method = A.getApplicableRegularMethod("m", new ArrayList());
        RegularImplementation implementation = (RegularImplementation)method.getImplementation();
        Block block = implementation.getBody();
        List statements = block.getStatements();
        if(implementation.getAllStatements().size() == 3)
        	System.out.println("testMethodM: OK1");
        //System.out.println(implementation.getAllExpressions().size());
        if(implementation.getAllExpressions().size() == 2)
        	System.out.println("testMethodM: OK2");
        
        if(statements.size() == 2)
        	System.out.println("testMethodM: OK3");
        LocalVariableDeclarationStatement aDecl = (LocalVariableDeclarationStatement)statements.get(0);
        List varsA = aDecl.getVariables();
        if(varsA.size() == 1)
        	System.out.println("testMethodM: OK4");
        LocalVariable varA = (LocalVariable)varsA.get(0);
        if(varA.getName().equals("a"))
        	System.out.println("testMethodM: OK5");
        
        Type type = (Type)varA.getType();
        Type javaLangObject = (Type)unnamed.findType("java.lang.Object");
        if(type == javaLangObject)
        	System.out.println("testMethodM: OK6");
        LocalVariableDeclarationStatement bDecl = (LocalVariableDeclarationStatement)statements.get(1);
        List varsB = bDecl.getVariables();
        LocalVariable varB = (LocalVariable)varsB.get(0);
        if(varB.getName().equals("b"))
        	System.out.println("testMethodM: OK7");
        type = (Type)varB.getType();
        if(type == javaLangObject)
        	System.out.println("testMethodM: OK8");
        
        VariableReference ref = (VariableReference)varB.getInitialization();
        Variable var = ref.getVariable();
        MemberVariable memberVar = A.getVariable("a");
        if(var == memberVar)
        	System.out.println("testMethodM: OK9");
        Variable findA = bDecl.lexicalContext().findVariable("a"); 
        if(findA == varA)
			System.out.println("testMethodM: OK10");
      }

	public static void main(String[] args) throws Exception {		
		new TestJNomeNew();
	}
}
