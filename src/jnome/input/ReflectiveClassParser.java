package jnome.input;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.modifier.StrictFP;
import jnome.core.modifier.Synchronized;
import jnome.core.modifier.Transient;
import jnome.core.modifier.Volatile;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.PureWildcard;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.document.Document;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.oo.member.Member;
import chameleon.oo.method.Method;
import chameleon.oo.method.SimpleNameMethodHeader;
import chameleon.oo.method.exception.ExceptionClause;
import chameleon.oo.method.exception.TypeExceptionDeclaration;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.UnionTypeReference;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.ExtendsConstraint;
import chameleon.oo.type.generics.ExtendsWildcard;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.SuperWildcard;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.variable.FormalParameter;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Interface;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Protected;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.util.Util;

public class ReflectiveClassParser {

	private JavaFactory _factory;
	
	private Java _language;
	
	public Java language() {
		return _language;
	}

	public ReflectiveClassParser(Java language) {
		_language = language;
		_factory = language.plugin(JavaFactory.class); 
	}
	
	public Document read(Class clazz, RootNamespace root) throws LookupException {
		Document doc = new Document();
		String packageName = clazz.getPackage().getName();
		NamespaceDeclaration nsd = new NamespaceDeclaration(root.getOrCreateNamespace(packageName));
		doc.add(nsd);
		Java language = language();
		Type type = _factory.createRegularType(new SimpleNameSignature(clazz.getSimpleName()));
		nsd.add(type);
		
		determineInterface(clazz,type);
		
		type.addModifiers(getModifiers(clazz));
		
		processConstructors(clazz,type);
		
		// Check whether the class is an interface
    processMethods(clazz, type);
    
    processFields(clazz, type);
   
    processInnerClasses(clazz, type);
		
		return doc;
	}
	
	protected List<chameleon.core.modifier.Modifier> getModifiers(Class clazz) {
		return getModifiers(clazz.getModifiers());
	}

  public List<chameleon.core.modifier.Modifier> getModifiers(int modifiers) {
  	List<chameleon.core.modifier.Modifier> result = new ArrayList<chameleon.core.modifier.Modifier>();
    if(Modifier.isPublic(modifiers)) {
      result.add(new Public()); 
    }
    if(Modifier.isProtected(modifiers)) {
      result.add(new Protected()); 
    }
    if(Modifier.isPrivate(modifiers)) {
      result.add(new Private()); 
    }
    if(Modifier.isStatic(modifiers)) {
      result.add(new Static()); 
    }
    if(Modifier.isFinal(modifiers)) {
      result.add(new Final()); 
    }
    if(Modifier.isStrict(modifiers)) {
      result.add(new StrictFP()); 
    }
    if(Modifier.isAbstract(modifiers)) {
      result.add(new Abstract()); 
    }
    if(Modifier.isSynchronized(modifiers)) {
      result.add(new Synchronized()); 
    }
    if(Modifier.isInterface(modifiers)) {
      result.add(new Interface()); 
    }
    if(Modifier.isNative(modifiers)) {
      result.add(new Native()); 
    }
    if(Modifier.isTransient(modifiers)) {
      result.add(new Transient()); 
    }
    if(Modifier.isVolatile(modifiers)) {
      result.add(new Volatile()); 
    }
    
    return result;
    
  }

	// java.lang.reflect has no interface for both methods and constructors (other than the generic Member interface).
  
  protected abstract class MemberFactory<T> {

  	boolean isPrivate(T t) {
  		return Modifier.isPrivate(getModifiers(t));
  	}
  	
  	abstract int getModifiers(T t);

  	void addCustomModifiers() {}

  	abstract TypeVariable[] getTypeParameters(T t);
  	
  	abstract java.lang.reflect.Type[] getGenericParameterTypes(T t);
  	
  	abstract java.lang.reflect.Type[] getGenericExceptionTypes(T t);
  	
  	abstract String getName(T t);
  	
  	void createMember(T t, Type type) {
  		if(! isPrivate(t)) {
  			// Both the name and the return type of a constructor are equal to the class name.
  			String methodName = Util.getLastPart(getClassName(getName(t)));
  			BasicJavaTypeReference returnType = language().createTypeReference(methodName);

  			// Create the method
  			SimpleNameMethodHeader header = new SimpleNameMethodHeader(methodName, returnType);
  			Method method = new NormalMethod(header);

  			// Process the modifiers
  			method.addModifiers(ReflectiveClassParser.this.getModifiers(getModifiers(t)));
  			addCustomModifiers();

  			// Process method type parameters.
  			TypeVariable[] var = getTypeParameters(t);
  			header.addAllTypeParameters(ReflectiveClassParser.this.getTypeParameters(var));

  			// 
  			java.lang.reflect.Type[] args = getGenericParameterTypes(t);
  			boolean valid = true;
  			for(int i = 0; i < args.length; i++) {
  				TypeReference tref = toRef(args[i]);

  				//					if((Util.getLastPart(temp) != null) && (Character.getType(Util.getLastPart(temp).charAt(0)) == Character.DECIMAL_DIGIT_NUMBER)) {
  				//						valid = false;
  				//					}
  				header.addFormalParameter(new FormalParameter(new SimpleNameSignature("a_r_g_u_m_e_n_t_"+i), tref));
  			}


  			java.lang.reflect.Type[] exceptions = getGenericExceptionTypes(t);

  			ExceptionClause clause = new ExceptionClause();
  			method.setExceptionClause(clause);
  			for(java.lang.reflect.Type exception:exceptions) {
  				clause.add(new TypeExceptionDeclaration(toRef(exception)));
  			}
  			type.add(method);
//  			if(valid) {
//  				output.add(method);
//  			}
  		}
  	}
  }
  
  protected class ConstructorBridge extends MemberFactory<Constructor> {

		@Override
		public String getName(Constructor constructor) {
			return constructor.getName();
		}

		@Override
		public int getModifiers(Constructor constructor) {
			return constructor.getModifiers();
		}

		@Override
		TypeVariable[] getTypeParameters(Constructor constructor) {
			return constructor.getTypeParameters();
		}

		@Override
		java.lang.reflect.Type[] getGenericParameterTypes(Constructor constructor) {
			return constructor.getGenericParameterTypes();
		}
  	
		@Override
		java.lang.reflect.Type[] getGenericExceptionTypes(Constructor constructor) {
			return constructor.getGenericExceptionTypes();
		}
  	
  }

	protected void processConstructors(final Class clazz, Type output) {
		ConstructorBridge constructorBridge = new ConstructorBridge();
		for(Constructor constructor: clazz.getDeclaredConstructors()) {
			constructorBridge.createMember(constructor,output);
		}
	}

  protected class MethodBridge extends MemberFactory<java.lang.reflect.Method> {

		@Override
		public String getName(java.lang.reflect.Method constructor) {
			return constructor.getName();
		}

		@Override
		public int getModifiers(java.lang.reflect.Method constructor) {
			return constructor.getModifiers();
		}

		@Override
		TypeVariable[] getTypeParameters(java.lang.reflect.Method constructor) {
			return constructor.getTypeParameters();
		}

		@Override
		java.lang.reflect.Type[] getGenericParameterTypes(java.lang.reflect.Method constructor) {
			return constructor.getGenericParameterTypes();
		}
  	
		@Override
		java.lang.reflect.Type[] getGenericExceptionTypes(java.lang.reflect.Method constructor) {
			return constructor.getGenericExceptionTypes();
		}
  	
  }
  
	protected void processMethods(final Class clazz, Type output) {
		MethodBridge methodBridge = new MethodBridge();
		for(java.lang.reflect.Method method: clazz.getDeclaredMethods()) {
			methodBridge.createMember(method,output);
		}
	}



	
	protected List<TypeParameter> getTypeParameters(TypeVariable[] variables) {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		for(TypeVariable typeVariable:variables) {
   	 result.add(getVar(typeVariable));
		}
		return result;
	}
	
	protected TypeParameter getVar(TypeVariable var) {
  	FormalTypeParameter result = new FormalTypeParameter(var.getName());
  	java.lang.reflect.Type[] bounds = var.getBounds();
  	for(java.lang.reflect.Type bound: bounds) {
  		result.addConstraint(new ExtendsConstraint(toRef(bound)));
  	}
  	return result;
  }


	protected String getClassName(String string) {
    return correctType(string.replace('$','.')); 
  }

	private JavaTypeReference toRef(java.lang.reflect.Type type) {
		JavaTypeReference result;
		if(type instanceof Class) {
  		result = language().createTypeReference(((Class)type).getName());
  	} else if (type instanceof ParameterizedType) {
  		ParameterizedType parameterizedType = (ParameterizedType)type;
  		
  		result = (BasicJavaTypeReference) toRef(parameterizedType.getRawType());
  		java.lang.reflect.Type[] args = parameterizedType.getActualTypeArguments();
  		if(args.length > 0) {
  			for(int i = 0; i<args.length; i++) {
  				((BasicJavaTypeReference)result).addArgument(toTypeArg(args[i]));
  			}
  		}
  	} else if (type instanceof TypeVariable) {
  		result = language().createTypeReference(((Class)type).getName());
  	} else if (type instanceof GenericArrayType) {
  		GenericArrayType arrayType = (GenericArrayType) type;
  		result = toRef(arrayType.getGenericComponentType());
  		result = new ArrayTypeReference(result);
  	}
		else {
  		throw new RuntimeException("Type of given type not supported: "+type.getClass());
  	}
		return result;
	}
	
	private ActualTypeArgument toTypeArg(java.lang.reflect.Type type) {
		ActualTypeArgument result;
		if(type instanceof Class || type instanceof TypeVariable) {
  		result = language().createBasicTypeArgument(toRef(type));
  	} else if (type instanceof WildcardType) {
  		WildcardType wild = (WildcardType) type;
  		java.lang.reflect.Type[] lower = wild.getLowerBounds();
  		if(lower.length > 0) {
  			UnionTypeReference union = new UnionTypeReference();
				result = new SuperWildcard(union);
  			for(java.lang.reflect.Type t: lower) {
  				union.add(toRef(t));
  			}
  		} else {
  			java.lang.reflect.Type[] upper = wild.getUpperBounds();
    		if(upper.length > 0) {
    			IntersectionTypeReference intersection = new IntersectionTypeReference();
  				result = new ExtendsWildcard(intersection);
    			for(java.lang.reflect.Type t: lower) {
    				intersection.add(toRef(t));
    			}
    		}
    		else {
    			result = new PureWildcard();
    		}
  		}
  	} else {
  		throw new IllegalArgumentException();
  	}
		return result;
	}

	/**
	 * Check whether the class is an interface and set the Interface modifier if that is the case.
	 * @param clazz
	 * @param type
	 */
	private void determineInterface(Class clazz, Type type) {
		if(clazz.isInterface()) {
			type.addModifier(new Interface());
		}
	}
	
  /**
   * Replaces $ with . and parses those ugly array names
   */
  private String correctType(String name){
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
}
