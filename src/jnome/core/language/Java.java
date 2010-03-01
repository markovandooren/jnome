package jnome.core.language;


import jnome.core.modifier.PackageProperty;
import jnome.core.type.NullType;


import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.property.StaticChameleonProperty;
import chameleon.core.relation.EquivalenceRelation;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.inheritance.InheritanceRelation;
import chameleon.core.variable.MemberVariable;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.support.modifier.PrivateProperty;
import chameleon.support.modifier.ProtectedProperty;
import chameleon.support.modifier.PublicProperty;
import chameleon.support.rule.member.MemberInheritableByDefault;
import chameleon.support.rule.member.MemberOverridableByDefault;
import chameleon.support.rule.member.TypeExtensibleByDefault;
import chameleon.support.variable.VariableDeclarator;

/**
 * @author Marko van Dooren
 */
public class Java extends ObjectOrientedLanguage {

	protected Java(String name) {
		super(name, new JavaLookupFactory());
		_nullType = new NullType(this);
		new RootNamespace(new SimpleNameSignature(""), this);
		this.defaultNamespace().setNullType();
		STRICTFP = new StaticChameleonProperty("strictfp", this, Declaration.class);
		SYNCHRONIZED = new StaticChameleonProperty("synchronized", this, Method.class);
		TRANSIENT = new StaticChameleonProperty("transient", this, MemberVariable.class);
		VOLATILE = new StaticChameleonProperty("volatile", this, MemberVariable.class);
		IMPLEMENTS_RELATION = new StaticChameleonProperty("implements", this, InheritanceRelation.class);
		PROTECTED = new ProtectedProperty(this, SCOPE_MUTEX);
		PRIVATE = new PrivateProperty(this, SCOPE_MUTEX);
		PUBLIC = new PublicProperty(this, SCOPE_MUTEX);
		PACKAGE_ACCESSIBLE = new PackageProperty(this, SCOPE_MUTEX);

		// In Java, a constructor is a class method
		CONSTRUCTOR.addImplication(CLASS);
		// In Java, constructors are not inheritable
		CONSTRUCTOR.addImplication(INHERITABLE.inverse());
		
  	INHERITABLE.addValidElementType(VariableDeclarator.class);
  	PRIVATE.addValidElementType(VariableDeclarator.class);
  	PUBLIC.addValidElementType(VariableDeclarator.class);
  	PROTECTED.addValidElementType(VariableDeclarator.class);
	}
	
	public Java() {
		this("Java");
	}
	
	private final class JavaEquivalenceRelation extends EquivalenceRelation<Member> {
		@Override
		public boolean contains(Member first, Member second) throws LookupException {
			return first.equals(second);
		}
	}

	protected NullType _nullType;
	// Adding properties. Note that 'this' is a PropertyUniverse.
	public final ChameleonProperty STRICTFP;
	public final ChameleonProperty SYNCHRONIZED;
	public final ChameleonProperty TRANSIENT;
	public final ChameleonProperty VOLATILE;
	public final StaticChameleonProperty PROTECTED;
	public final StaticChameleonProperty PRIVATE;
	public final StaticChameleonProperty PUBLIC;
	public final ChameleonProperty PACKAGE_ACCESSIBLE;
	public final ChameleonProperty IMPLEMENTS_RELATION;
	
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
  	addPropertyRule(new TypeExtensibleByDefault());
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
		  
		/**
		 * Returns true if the given character is a valid character
		 * for an identifier.
		 */
		@Override
		public boolean isValidIdentifierCharacter(char character){
			return Character.isJavaIdentifierPart(character);
		}

		@Override
		protected void initializeValidityRules() {
			
		}

		@Override
		protected Language cloneThis() {
			return new Java();
		}

}
