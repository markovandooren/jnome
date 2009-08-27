package jnome.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.rejuse.java.collections.RobustVisitor;
import org.rejuse.java.collections.Visitor;

import chameleon.util.Util;

/**
 * @author marko
 */
public class Extractor {

	/**
	 * 
	 */
	public Extractor() {
	}

  public String getCompilationUnit(String fqn) throws ClassNotFoundException {
    Class clazz = Class.forName(fqn);
    final StringBuffer result = new StringBuffer();
    
    // PACKAGE
    result.append("package " + clazz.getPackage().getName() + ";\n");
    result.append(getType(clazz,""));
    return result.toString(); 
  }
  
  public String getType(Class clazz, final String indent) {
     final StringBuffer result = new StringBuffer();
     
     result.append(indent);
     
     result.append(getModifiers(clazz));
     
     if(! clazz.isInterface()) {
       result.append("class ");
     }
     result.append(Util.getLastPart(getClassName(clazz.getName())));
     
     if(clazz.getSuperclass() != null) {
       result.append(" extends "+ getClassName(clazz.getSuperclass().getName()));
     }
     
     Class[] interfaces = clazz.getInterfaces();
     if(interfaces.length > 0) {
       if(clazz.isInterface()) {
         result.append(" extends ");
       }
       else {
         result.append(" implements ");
       }
     }
     for(int i = 0; i < interfaces.length; i++) {
       if(i > 0) {
         result.append(", ");
       }
       result.append(getClassName(interfaces[i].getName()));
     }
     result.append(" {\n");
     
     Constructor[] constructors = clazz.getDeclaredConstructors();
     
     new Visitor() {
            public void visit(Object element) {
          Constructor constructor = (Constructor) element;
          StringBuffer cons = new StringBuffer();
          cons.append(indent + "  ");
          cons.append(getModifiers(constructor));
          cons.append(Util.getLastPart(getClassName(constructor.getName())));
          cons.append("(");
          Class[] args = constructor.getParameterTypes();
          boolean valid = true;
          for(int i = 0; i < args.length; i++) {
             if(i > 0) {
               cons.append(", ");
             }
             String temp = getClassName(args[i].getName());
             cons.append(temp);
             if((Util.getLastPart(temp) != null) && (Character.getType(Util.getLastPart(temp).charAt(0)) == Character.DECIMAL_DIGIT_NUMBER)) {
                 valid = false;
             }
             cons.append(" a_r_g_u_m_e_n_t_"+i);
          }
          
          cons.append(") {}\n");
          if(valid) {
              result.append(cons.toString());
          }
          
            }
        }.applyTo(constructors);
     
     Method[] methods = clazz.getDeclaredMethods();
     
     new Visitor() {
		    public void visit(Object element) {
          Method method = (Method) element;
          result.append(indent + "  ");
          result.append(getModifiers(method));
          if((! Modifier.isAbstract(method.getModifiers())) && (! Modifier.isNative(method.getModifiers()))){
            result.append("native ");
          }
          result.append(getClassName(method.getReturnType().getName()));
          result.append(" ");
          result.append(method.getName());
          result.append("(");
          Class[] args = method.getParameterTypes();
          for(int i = 0; i < args.length; i++) {
             if(i > 0) {
               result.append(", ");
             }
             result.append(getClassName(args[i].getName()));
             result.append(" a_r_g_u_m_e_n_t_"+i);
          }
          
          result.append(");\n");
		    }
		}.applyTo(methods);
     
    Field[] vars = clazz.getDeclaredFields();
    
    new Visitor() {
      public void visit(Object element) {
        Field field = (Field) element;
        result.append(indent + "  ");
        result.append(getModifiers(field));
        result.append(getClassName(field.getType().getName()));
        result.append(" ");
        result.append(field.getName());
        result.append(";\n");
      }
    }.applyTo(vars);
    
    Class[] inners = clazz.getDeclaredClasses();
    new Visitor() {
		    public void visit(Object element) {
          Class inner = (Class)element;
          String innerName = Util.getLastPart(getClassName(inner.getName()));
          if(Character.getType(innerName.charAt(0)) != Character.DECIMAL_DIGIT_NUMBER ) {
            result.append(getType(inner, indent + "  "));
            result.append("\n");
          }
		    }
		}.applyTo(inners);
    
    result.append(indent + "}");
    
    return result.toString();
  }
  
  public String getClassName(String string) {
    return correctType(string.replace('$','.')); 
  }
  
  public String getModifiers(Class clazz) {
    return getModifiers(clazz.getModifiers());
  }
  
  public String getModifiers(Field field) {
    return getModifiers(field.getModifiers());
  }
  
  public String getModifiers(Constructor constructor) {
    return getModifiers(constructor.getModifiers());
  }
  
  public String getModifiers(Method method) {
    return getModifiers(method.getModifiers()); 
  }
  
  public String getModifiers(int modifiers) {
    StringBuffer result = new StringBuffer();
    if(Modifier.isPublic(modifiers)) {
      result.append("public "); 
    }
    if(Modifier.isProtected(modifiers)) {
      result.append("protected "); 
    }
    if(Modifier.isPrivate(modifiers)) {
      result.append("private "); 
    }
    if(Modifier.isStatic(modifiers)) {
      result.append("static "); 
    }
    if(Modifier.isFinal(modifiers)) {
      result.append("final "); 
    }
    if(Modifier.isStrict(modifiers)) {
      result.append("strictfp "); 
    }
    if(Modifier.isAbstract(modifiers)) {
      result.append("abstract "); 
    }
    if(Modifier.isSynchronized(modifiers)) {
      result.append("synchronized "); 
    }
    if(Modifier.isInterface(modifiers)) {
      result.append("interface "); 
    }
    if(Modifier.isNative(modifiers)) {
      result.append("native "); 
    }
    if(Modifier.isTransient(modifiers)) {
      result.append("transient "); 
    }
    if(Modifier.isVolatile(modifiers)) {
      result.append("volatile "); 
    }
    
    return result.toString();
    
  }
  
  public void generate(List classes, final File root) throws Exception {
    new RobustVisitor() {
        private int count = 0;
		    public Object visit(Object element) throws IOException, ClassNotFoundException {
          String name = (String)element;
          File dest = new File(root.getAbsolutePath()+File.separator + getFileName(name));
          System.out.println(++count + " Writing "+name + " to "+dest.getAbsolutePath());
          
          generate(name, dest);
          return null;
        }
        public void unvisit(Object key, Object element) {
          // NOP 
        }
		}.applyTo(classes);
  }
  
  /**
   * Replaces $ with . and parses those ugle array names
   */
  public String correctType(String name){
    // $ -> .
    String newName=name.replace('$','.');
    //correct array representation
    if((name.length()>0)&&(name.substring(0,1).equals("["))) {
      String temp="";
      int nb = 1;
      while(name.substring(nb,nb+1).equals("[")){
        nb++;
      }
      int count=nb;
      while(count > 0) {
        temp+="[]";
        count--;
      }
      if(name.substring(nb,nb+1).equals("B")){
        temp="byte"+temp;
      }
      if(name.substring(nb,nb+1).equals("C")){
        temp="char"+temp;
      }
      if(name.substring(nb,nb+1).equals("D")){
        temp="double"+temp;
      }
      if(name.substring(nb,nb+1).equals("F")){
        temp="float"+temp;
      }
      if(name.substring(nb,nb+1).equals("I")){
        temp="int"+temp;
      }
      if(name.substring(nb,nb+1).equals("J")){
        temp="long"+temp;
      }
      if(name.substring(nb,nb+1).equals("S")){
        temp="short"+temp;
      }
      if(name.substring(nb,nb+1).equals("Z")){
        temp="boolean"+temp;
      }
      if(name.substring(nb,nb+1).equals("L")){
        temp=name.substring(nb+1,name.length()-1)+temp;
      }

      newName=temp;
    }
    return newName;
  }
  
  public void generate(String name, File file) throws IOException, ClassNotFoundException {
    File parent = file.getParentFile();
    parent.mkdirs();
    file.createNewFile();
    FileWriter fw = new FileWriter(file);
    fw.write(getCompilationUnit(name));
    fw.close();
//    getCompilationUnit(name);
  }
  
  public String getFileName(String className) {
    return className.replace('.',File.separatorChar) + ".java"; 
  }
  
  public static void main(String[] args) throws IOException, Exception {
    Extractor extractor = new Extractor();
    extractor.generate(extractor.readClassNames(new File(args[0])),new File(args[1]));
    //System.out.println(extractor.getCompilationUnit("java.lang.Character"));
    System.out.println("Done");
  }
  
  public List readClassNames(File file) throws IOException {
    BufferedReader buffered = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    LineNumberReader reader = new LineNumberReader(buffered);
    List result = new ArrayList();
    String line = reader.readLine();
    while(line != null) {
      result.add(line); 
      line = reader.readLine();
    }
    return result;
  }
}

