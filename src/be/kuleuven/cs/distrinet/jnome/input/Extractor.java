package be.kuleuven.cs.distrinet.jnome.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.aikodi.chameleon.util.Util;
import org.aikodi.rejuse.java.collections.Visitor;

/**
 * @author Marko van Dooren
 */
public class Extractor {

	/**
	 * 
	 */
	public Extractor(List<String> classes) {
		_classes = new ArrayList<String>(classes);
	}
	
	private URL _url;
	
	public void setJar(String pathName) throws MalformedURLException {
		_url = new File(pathName).toURI().toURL();
		_loader = URLClassLoader.newInstance(
		    new URL[] {_url},
		    getClass().getClassLoader()
		);

	}

	private ClassLoader _loader;
	
  public String getCompilationUnit(String fqn) throws ClassNotFoundException {
    Class clazz = getClass(fqn);
    final StringBuffer result = new StringBuffer();
    
    // PACKAGE
    result.append("package " + clazz.getPackage().getName() + ";\n");
    result.append(getType(clazz,""));
    return result.toString(); 
  }

	private Class<?> getClass(String fqn) throws ClassNotFoundException {
		if(_url == null) {
			return Class.forName(fqn);
		} else {
			return loadFromJar(fqn);
		}
	}
	
	private Class<?> loadFromJar(String fqn) throws ClassNotFoundException {
		return Class.forName(fqn, true, _loader);
	}
  
  public String toString(TypeVariable var) {
  	StringBuffer result = new StringBuffer();
  	result.append(var.getName());
  	Type[] bounds = var.getBounds();
  	if(bounds.length > 0) {
  		result.append(" extends ");
  		toStringBounds(result, bounds);
  	}
  	return result.toString();
  }
  
  public String getClassName(Type type) {
  	String result;
  	if(type instanceof Class) {
  		result = getClassName(((Class)type).getName());
  	} else {
  		result = toString(type);
  	}
  	return result.replace('$','.');
  }
  
  public String toString(Type type) {
  	if(type instanceof Class) {
  		return ((Class)type).getName();
  	} else if (type instanceof ParameterizedType) {
  		StringBuffer result = new StringBuffer();
  		ParameterizedType parameterizedType = (ParameterizedType)type;
			result.append(toString(parameterizedType.getRawType()));
  		Type[] args = parameterizedType.getActualTypeArguments();
  		if(args.length > 0) {
  		result.append("<");
  		for(int i = 0; i<args.length; i++) {
  			result.append(toString(args[i]));
  			if(i < args.length - 1) {
  				result.append(",");
  			}
  		}
  		result.append(">");
  		}
  		return result.toString();
  	} else if (type instanceof TypeVariable) {
  		return ((TypeVariable)type).getName();
  	} else if (type instanceof WildcardType) {
  		StringBuffer result = new StringBuffer();
  		result.append("?");
  		WildcardType wild = (WildcardType) type;
  		Type[] lower = wild.getLowerBounds();
  		if(lower.length > 0) {
  			result.append(" super ");
  			toStringBounds(result, lower);
  		} else {
  			Type[] upper = wild.getUpperBounds();
    		if(upper.length > 0) {
    			result.append(" extends ");
    			toStringBounds(result, upper);
    		}
  		}
  		return result.toString();
  	} else if (type instanceof GenericArrayType) {
  		GenericArrayType arrayType = (GenericArrayType) type;
  		return toString(arrayType.getGenericComponentType())+"[]";
  	}
		else {
  		throw new RuntimeException("Type of given type not supported: "+type.getClass());
  	}
  }

	public void appendExceptionTypes(StringBuffer cons, Type[] exceptions) {
		if(exceptions.length > 0) {
			cons.append(" throws ");
			for(int i=0; i<exceptions.length;i++) {
				cons.append(toString(exceptions[i]));
				if(i<exceptions.length - 1) {
					cons.append(", ");
				}
			}
		}
	}

	private void toStringBounds(StringBuffer result, Type[] lower) {
		for(int i = 0; i< lower.length; i++) {
			result.append(toString(lower[i]));
			if(i<lower.length-1) {
				result.append(" & ");
			}
		}
	}
  
  public String getType(Class clazz, final String indent) {
     final StringBuffer result = new StringBuffer();
     
     result.append(indent);
     
     result.append(getModifiers(clazz));
     
     if(! clazz.isInterface()) {
       result.append("class ");
     }
     result.append(Util.getLastPart(getClassName(clazz.getName())));
     // FORMAL TYPE PARAMETERS
     TypeVariable[] var = clazz.getTypeParameters();
     toStringTypeParameters(result, var);
     if(clazz.getSuperclass() != null) {
       result.append(" extends "+ getClassName(toString(clazz.getGenericSuperclass())));
     }
     
     List<Type> interfaces = new ArrayList<Type>(Arrays.asList(clazz.getGenericInterfaces()));
     Iterator<Type> iter = interfaces.iterator();
     while(iter.hasNext()) {
    	 String className = getClassName(toString(iter.next()));
    	 if(!contains(getTypeConstructorName(className))) {
    		 iter.remove();
    	 }
     }
     int newSize = interfaces.size();
     if(newSize > 0) {
       if(clazz.isInterface()) {
         result.append(" extends ");
       }
       else {
         result.append(" implements ");
       }
     }
     for(int i = 0 ; i < newSize; i++) {
    	 String className = getClassName(toString(interfaces.get(i)));
    	 if(i > 0) {
    		 result.append(", ");
    	 }
    	 result.append(className);
     }
     result.append(" {\n");
     
     toStringConstructors(clazz, indent, result);
     
     toCodeMethods(clazz, indent, result);
     
    toCodeFields(indent, result, clazz.getDeclaredFields());
    
    toCodeInnerClasses(clazz, indent, result);
    
    result.append(indent + "}");
    
    return result.toString();
  }
  
  private String getTypeConstructorName(String name) {
  	int indexOf = name.indexOf("<");
  	if(indexOf >=0) {
		  return name.substring(0, indexOf);
  	} else {
  		return name;
  	}
  }
  
  private boolean contains(String name) {
  	String cname = getTypeConstructorName(name);
  	while (cname != null) {
  		if(_classes.contains(cname)) {
  			return true;
  		} else {
  			int lastIndexOf = cname.lastIndexOf(".");
  			if(lastIndexOf >= 0) {
  				cname = cname.substring(0,lastIndexOf);
  			} else {
  				cname = null;
  			}
  		}
  	}
  	return false;
  }

	private void toCodeInnerClasses(Class clazz, final String indent, final StringBuffer result) {
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
	}

	private void toCodeFields(final String indent, final StringBuffer result, Field[] vars) {
		new Visitor() {
      public void visit(Object element) {
        Field field = (Field) element;
        if(! Modifier.isPrivate(field.getModifiers())) {
        result.append(indent + "  ");
        result.append(getModifiers(field));
        result.append(getClassName(field.getGenericType()));
        result.append(" ");
        result.append(field.getName());
        result.append(";\n");
        }
      }
    }.applyTo(vars);
	}

	public void toCodeMethods(Class clazz, final String indent, final StringBuffer result) {
		Method[] methods = clazz.getDeclaredMethods();
     
     new Visitor() {
    	 public void visit(Object element) {
    		 Method method = (Method) element;
    		 if(! Modifier.isPrivate(method.getModifiers()) && ! method.isSynthetic()) {
    			 result.append(indent + "  ");
    			 result.append(getModifiers(method));
    			 if((! Modifier.isAbstract(method.getModifiers())) && (! Modifier.isNative(method.getModifiers()))){
    				 result.append("native ");
    			 }
    			 TypeVariable[] var = method.getTypeParameters();
    			 toStringTypeParameters(result, var);
    			 result.append(getClassName(method.getGenericReturnType()));
    			 result.append(" ");
    			 result.append(method.getName());
    			 result.append("(");
    			 Type[] args = method.getGenericParameterTypes();
    			 for(int i = 0; i < args.length; i++) {
    				 if(i > 0) {
    					 result.append(", ");
    				 }
    				 String className = getClassName(args[i]);
    				 if((i < args.length -1) || ! method.isVarArgs()) {
    					 result.append(className);
    				 } else {
    					 // method.isVarArgs()
    					 result.append(className.substring(0, className.length()-2));
    					 result.append("...");
    				 }
    				 result.append(" a_r_g_u_m_e_n_t_"+i);
    			 }

    			 result.append(") ");
    			 Type[] exceptions = method.getGenericExceptionTypes();
    			 appendExceptionTypes(result, exceptions);
    			 result.append(";\n");

    		 }
    	 }

     }.applyTo(methods);
	}

	
	public void toStringTypeParameters(final StringBuffer result, TypeVariable[] var) {
		if(var.length > 0) {
   	 result.append("<");
   	 for(int i=0; i < var.length; i++) {
   		 result.append(Extractor.this.toString(var[i]));
   		 if(i < var.length - 1) {
   			 result.append(", ");
   		 }
   	 }
   	 result.append(">");
    }
	}

	public void toStringConstructors(final Class clazz, final String indent, final StringBuffer result) {
		Constructor[] constructors = clazz.getDeclaredConstructors();
     
     new Visitor() {
            public void visit(Object element) {
          Constructor constructor = (Constructor) element;
          if(! Modifier.isPrivate(constructor.getModifiers())) {
          StringBuffer cons = new StringBuffer();
          cons.append(indent + "  ");
          cons.append(" ");
          cons.append(getModifiers(constructor));
          TypeVariable[] var = constructor.getTypeParameters();
          toStringTypeParameters(cons, var);
          cons.append(" ");
          cons.append(Util.getLastPart(getClassName(constructor.getName())));
          cons.append("(");
          Type[] args = constructor.getGenericParameterTypes();
          boolean valid = true;
          for(int i = 0; i < args.length; i++) {
             if(i > 0) {
               cons.append(", ");
             }
             String temp = getClassName(args[i]);
             cons.append(temp);
             if((Util.getLastPart(temp) != null) && (Character.getType(Util.getLastPart(temp).charAt(0)) == Character.DECIMAL_DIGIT_NUMBER)) {
                 valid = false;
             }
             cons.append(" a_r_g_u_m_e_n_t_"+i);
          }
          
          cons.append(")");
          Type[] exceptions = constructor.getGenericExceptionTypes();
          appendExceptionTypes(cons, exceptions);
          cons.append("{}\n");
          if(valid) {
              result.append(cons.toString());
          }
            }
            }
        }.applyTo(constructors);
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
  
  private List<String> _classes;
  
  public void generate(final File root) throws Exception {
  	int count = 0;
  	for(String name: _classes) {
  		File dest = new File(root.getAbsolutePath()+File.separator + getFileName(name));
  		System.out.println(++count + " Writing "+name + " to "+dest.getAbsolutePath());
  		generate(name, dest);
  	}
  }
  
  /**
   * Replaces $ with . and parses those ugly array names
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
  }
  
  public String getFileName(String className) {
    return className.replace('.',File.separatorChar) + ".java"; 
  }
  
  public static void main(String[] args) throws IOException, Exception {
  	Extractor extractor;
  	if(args[0].endsWith(".jar")) {
  		extractor = new Extractor(readClassNamesFromJar(new JarFile(args[0])));
  		extractor.setJar(args[0]);
  	} else {
  		extractor = new Extractor(readClassNames(new File(args[0])));
  	}
    extractor.generate(new File(args[1]));
    //System.out.println(extractor.getCompilationUnit("java.lang.Character"));
    System.out.println("Done");
  }
  
  public static List<String> readClassNamesFromJar(JarFile file) throws IOException {
  	Enumeration<JarEntry> entries = file.entries();
  	List<String> result = new ArrayList<String>();
		while(entries.hasMoreElements()) {
  		JarEntry entry = entries.nextElement();
  		String name = entry.getName();
  		if(name.endsWith(".class")) {
  			String className = name.substring(0, name.length()-6).replace(File.separatorChar, '.');
  			String shortName = Util.getLastPart(className);
  			boolean validClassName = ! shortName.contains("$");
  			try {
  				Integer i = new Integer(shortName);
  			} catch(NumberFormatException e) {
  				validClassName = true;
  			}
  			if(validClassName) {
  				result.add(className);
  			  System.out.println(className);
  			}
  		}
  	}
		return result;
  }
  
  public static List readClassNames(File file) throws IOException {
    BufferedReader buffered = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    LineNumberReader reader = new LineNumberReader(buffered);
    List result = new ArrayList();
    String line = reader.readLine();
    while(line != null) {
      result.add(line); 
      line = reader.readLine();
    }
    reader.close();
    return result;
  }
}

