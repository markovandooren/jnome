package jnome.core.language;


import jnome.core.modifier.PackageProperty;
import jnome.core.type.NullType;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.property.Property;
import org.rejuse.property.PropertyMutex;
import org.rejuse.property.StaticProperty;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.language.ObjectOrientedLanguage;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.property.Defined;
import chameleon.core.relation.EquivalenceRelation;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.modifier.PrivateProperty;
import chameleon.support.modifier.ProtectedProperty;
import chameleon.support.modifier.PublicProperty;
import chameleon.support.rule.member.MemberInheritableByDefault;
import chameleon.support.rule.member.MemberOverridableByDefault;

/**
 * @author Marko van Dooren
 */
public class Java extends ObjectOrientedLanguage {

	public Java() {
		super("Java", new JavaLookupFactory());
		_nullType = new NullType(this);
		new RootNamespace(new SimpleNameSignature(""), this);
		this.defaultNamespace().setNullType();
		STRICTFP = new StaticProperty<Element>("strictfp", this);
		SYNCHRONIZED = new StaticProperty<Element>("synchronized", this);
		TRANSIENT = new StaticProperty<Element>("transient", this);
		VOLATILE = new StaticProperty<Element>("volatile", this);
		PROTECTED = new ProtectedProperty(this, SCOPE_MUTEX);
		PRIVATE = new PrivateProperty(this, SCOPE_MUTEX);
		PUBLIC = new PublicProperty(this, SCOPE_MUTEX);
		PACKAGE_ACCESSIBLE = new PackageProperty(this, SCOPE_MUTEX);

		// In Java, a constructor is a class method
		CONSTRUCTOR.addImplication(CLASS);
		// In Java, constructors are not inheritable
		CONSTRUCTOR.addImplication(INHERITABLE.inverse());
	}
	
	private final class JavaEquivalenceRelation extends EquivalenceRelation<Member> {
		@Override
		public boolean contains(Member first, Member second) throws LookupException {
			return first.equals(second);
		}
	}

	private final class JavaHidesRelation extends StrictPartialOrder<Member> {
		@Override
		public boolean contains(Member first, Member second) throws LookupException {
			boolean result = false;
			if((first instanceof NormalMethod) && (second instanceof NormalMethod)) {
				result = ((NormalMethod<?,?,?>)first).nearestAncestor(Type.class).subTypeOf(((NormalMethod<?,?,?>)second).nearestAncestor(Type.class)) &&
				         (first.is(CLASS) == Ternary.TRUE) && 
				          first.signature().sameAs(second.signature());
			} else if(((first instanceof RegularMemberVariable) && (second instanceof RegularMemberVariable)) ||
					((first instanceof RegularMemberVariable) && (second instanceof RegularMemberVariable))) {
				 result = ((RegularMemberVariable)first).nearestAncestor(Type.class).subTypeOf(((RegularMemberVariable)second).nearestAncestor(Type.class)) &&
				          first.signature().sameAs(second.signature());
			}
			return result;
		}

		@Override
		public boolean equal(Member first, Member second) throws LookupException {
			return first.equals(second);
		}
	}
	
	private final class JavaImplementsRelation extends StrictPartialOrder<Member> {

		@Override
		public boolean contains(Member first, Member second) throws LookupException {
		  boolean result;
		  
		  if((first != second) && (first instanceof Method) && (second instanceof Method)) {
		    assert first != null;
		    assert second != null;
		    Method<?,?,?,?> method1 = (Method<?,?,?,?>) first;
		    Method<?,?,?,?> method2 = (Method<?,?,?,?>) second;
		    Ternary temp1 = method1.is(DEFINED);
		    boolean defined1;
		    if(temp1 == Ternary.TRUE) {
		      defined1 = true;
		    } else if (temp1 == Ternary.FALSE) {
		      defined1 = false;
		    } else {
		    	temp1 = method1.is(DEFINED);
		      throw new LookupException("The definedness of the first method could not be determined.");
		    }
		    if(defined1) {
		    Ternary temp2 = method2.is(DEFINED);
		    boolean defined2;
		    if(temp2 == Ternary.TRUE) {
		      defined2 = true;
		    } else if (temp2 == Ternary.FALSE) {
		      defined2 = false;
		    } else {
		      throw new LookupException("The definedness of the second method could not be determined.");
		    }
		    result = (!defined2) && 
		             method1.signature().sameAs(method2.signature()) &&
		             (! method2.getNearestType().subTypeOf(method1.getNearestType())) &&
		             method1.sameKind(method2);
		    } else {
		    	result = false;
		    }
		  }
//		  else if ((first instanceof MemberVariable) && (second instanceof MemberVariable)) {
//		    MemberVariable<? extends MemberVariable> var1 = (MemberVariable)first;
//		    MemberVariable<? extends MemberVariable> var2 = (MemberVariable)second;
//		    
//		    result = var1.getNearestType().subTypeOf(var2.getNearestType());  
//		  }
		  else {
		    result = false;
		  }
		  return result; 
		}

		@Override
		public boolean equal(Member first, Member second) {
			return first == second;
		}
		
	}

	private class JavaOverridesRelation extends StrictPartialOrder<Member> {
		@Override
		public boolean contains(Member first, Member second)
		    throws LookupException {
		  boolean result;
		  
		  if((first instanceof Method) && (second instanceof Method)) {
		    assert first != null;
		    assert second != null;
		    Method<?,?,?,?> method1 = (Method<?,?,?,?>) first;
		    Method<?,?,?,?> method2 = (Method<?,?,?,?>) second;
		    Ternary temp = method2.is(OVERRIDABLE);
		    boolean overridable;
		    if(temp == Ternary.TRUE) {
		      overridable = true;
		    } else if (temp == Ternary.FALSE) {
		      overridable = false;
		    } else {
		      throw new LookupException("The overridability of the other method could not be determined.");
		    }
		    result = overridable && 
		             method1.signature().sameAs(method2.signature()) && 
		             method1.getNearestType().subTypeOf(method2.getNearestType()) && 
		             method1.sameKind(method2);
		  } 
//		  else if ((first instanceof MemberVariable) && (second instanceof MemberVariable)) {
//		    MemberVariable<? extends MemberVariable> var1 = (MemberVariable)first;
//		    MemberVariable<? extends MemberVariable> var2 = (MemberVariable)second;
//		    
//		    result = var1.getNearestType().subTypeOf(var2.getNearestType());  
//		  } 
		  else {
		    result = false;
		  }
		  return result; 
		}

		@Override
		public boolean equal(Member first, Member second) {
		  return first == second;
		}
	}

	protected NullType _nullType;
	// Adding properties. Note that 'this' is a PropertyUniverse.
	public final Property<Element> STRICTFP;
	public final Property<Element> SYNCHRONIZED;
	public final Property<Element> TRANSIENT;
	public final Property<Element> VOLATILE;
	public final Property<Element> PROTECTED;
	public final Property<Element> PRIVATE;
	public final Property<Element> PUBLIC;
	public final Property<Element> PACKAGE_ACCESSIBLE;
	
	public Type getNullType(){
		return _nullType;
	}
 /*@
   @ also public behavior
   @
   @ post \result.equals(getDefaultPackage().findType("java.lang.NullPointerException")); 
   @*/
  public Type getNullInvocationException() throws LookupException {
    return findType("java.lang.NullPointerException");
  }
  
  protected void initializePropertyRules() {
  	addPropertyRule(new MemberOverridableByDefault());
  	addPropertyRule(new MemberInheritableByDefault());
  }

 /*@
   @ also public behavior
   @
   @ post \result.equals(getDefaultPackage().findType("java.lang.RuntimeException")); 
   @*/
  public Type getUncheckedException() throws LookupException {
    return findType("java.lang.RuntimeException");
  }
  
  public Type getTopCheckedException() throws LookupException {
    return findType("java.lang.Throwable");
  }

  public boolean isCheckedException(Type type) throws LookupException{
    Type error = findType("java.lang.Error");
    Type runtimeExc = findType("java.lang.RuntimeException");
    return isException(type) && (! type.assignableTo(error)) && (! type.assignableTo(runtimeExc));
  }

  public boolean isException(Type type) throws LookupException {
    return type.assignableTo(findType("java.lang.Throwable"));
  }
  
	  public String getDefaultSuperClassFQN() {
	    return "java.lang.Object";
	  }
	  
    @Override
    public StrictPartialOrder<Member> overridesRelation() {
     return new JavaOverridesRelation();
    }

    public Type findType(String fqn) throws LookupException {
    	TypeReference ref = new TypeReference(fqn);
    	ref.setUniParent(defaultNamespace());
    	return ref.getType();
    }
    
		@Override
		public Type booleanType() throws LookupException {
			return findType("boolean");
		}

		@Override
		public Type classCastException() throws LookupException {
			return findType("java.lang.ClassCastException");
		}

		@Override
		public StrictPartialOrder<Member> hidesRelation() {
			return _hidesRelation;
		}
		
		private JavaHidesRelation _hidesRelation = new JavaHidesRelation();
		
		public StrictPartialOrder<Member> implementsRelation() {
			return _implementsRelation;
		}

		private JavaImplementsRelation _implementsRelation = new JavaImplementsRelation();

		@Override
		public Type voidType() throws LookupException {
			return findType("void");
		}

		@Override
		public EquivalenceRelation<Member> equivalenceRelation() {
			return _equivalenceRelation;
		}

		private JavaEquivalenceRelation _equivalenceRelation = new JavaEquivalenceRelation();

		@Override
		public WeakPartialOrder<Type> subtypeRelation() {
			return _subtypingRelation;
		}
		
		public Type getDefaultSuperClass() throws LookupException {
			  TypeReference typeRef = new DummyTypeReference(getDefaultSuperClassFQN());
		    Type result = typeRef.getType();
		    if (result==null) {
		        throw new LookupException("Default super class "+getDefaultSuperClassFQN()+" not found.");
		    }
		    return result;
		}

		private JavaSubtypingRelation _subtypingRelation = new JavaSubtypingRelation();
		  
}
