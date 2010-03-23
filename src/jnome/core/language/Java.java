package jnome.core.language;


import java.util.ArrayList;
import java.util.List;

import jnome.core.modifier.PackageProperty;
import jnome.core.type.ArrayType;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.NullType;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.element.Element;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.NamedTarget;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.property.DynamicChameleonProperty;
import chameleon.core.property.StaticChameleonProperty;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.ElementReferenceWithTarget;
import chameleon.core.relation.EquivalenceRelation;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.ConstructedType;
import chameleon.core.type.DerivedType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.TypeConstraint;
import chameleon.core.type.generics.TypeConstraintWithReferences;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.type.inheritance.InheritanceRelation;
import chameleon.core.variable.MemberVariable;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.support.member.simplename.SimpleNameMethodSignature;
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
		PRIMITIVE_TYPE = new DynamicChameleonProperty("primitive", this,Type.class){
		
			@Override
			public Ternary appliesTo(Element element) {
				Ternary result = Ternary.FALSE;
				if(element instanceof Type) {
					String fqn = ((Type)element).getFullyQualifiedName();
					if(fqn.equals("void") || fqn.equals("int") || fqn.equals("long")|| fqn.equals("float")|| fqn.equals("double")
							|| fqn.equals("boolean")|| fqn.equals("byte")|| fqn.equals("char") || fqn.equals("short")) {
						result = Ternary.TRUE;
					}
				}
				return result;
			}
		};
		REFERENCE_TYPE = PRIMITIVE_TYPE.inverse();

		// In Java, a constructor is a class method
		CONSTRUCTOR.addImplication(CLASS);
		// In Java, constructors are not inheritable
		CONSTRUCTOR.addImplication(INHERITABLE.inverse());
		
  	INHERITABLE.addValidElementType(VariableDeclarator.class);
  	PRIVATE.addValidElementType(VariableDeclarator.class);
  	PUBLIC.addValidElementType(VariableDeclarator.class);
  	PROTECTED.addValidElementType(VariableDeclarator.class);
  	OVERRIDABLE.addValidElementType(VariableDeclarator.class);
  	DEFINED.addValidElementType(VariableDeclarator.class);
	}
	
	public Java() {
		this("Java");
	}
	
	
  public Type erasure(Type original) throws LookupException {
  	String fullyQualifiedName = original.getFullyQualifiedName();
  	Type result;
  	if(original instanceof ArrayType) {
  		ArrayType arrayType = (ArrayType) original;
  		result = new ArrayType(erasure(arrayType.componentType()), arrayType.dimension());
  	} else if(original instanceof ConstructedType){
  		FormalTypeParameter formal = ((ConstructedType)original).parameter();
  		List<TypeConstraint> constraints = formal.constraints();
  		if(constraints.size() > 0) {
  			TypeConstraint first = constraints.get(0);
  			if(first instanceof TypeConstraintWithReferences<?>) {
  			  result = ((JavaTypeReference)((TypeConstraintWithReferences<?>)first).typeReferences().get(0)).erasure();	
  			} else {
  				throw new ChameleonProgrammerException("The type constraint of type "+first.getClass().getName()+" is not a valid Java element");
  			}
  		} else {
  			result = getDefaultSuperClass();
  		}
  	} 
  	else {
  		// Regular TYPE
			List<TypeParameter> parameters = original.parameters();
			int size = parameters.size();
			if(size > 0 && (parameters.get(0) instanceof FormalTypeParameter)) {
				List<ActualTypeArgument> args = new ArrayList<ActualTypeArgument>(size);
				String defaultSuperClassFQN = getDefaultSuperClassFQN();
				RootNamespace defaultNamespace = original.language().defaultNamespace();
				for(int i=0; i<size;i++) {
					// FIXME is this where they mean left-most bound ? and is |G| applying erasure to the body of G where
					// references to type parameters are replaced by the left-most bound?
					BasicTypeArgument argument = new BasicTypeArgument(createTypeReference(defaultSuperClassFQN));
					argument.setUniParent(defaultNamespace);
					args.add(argument);
				}
				result = new DerivedType(original, args);
				result.setUniParent(original.parent());
			} else {
  			result = original;
  		}
  	}
  	return result;
  }
  
	public SimpleNameMethodSignature erasure(SimpleNameMethodSignature signature) {
		SimpleNameMethodSignature result = new SimpleNameMethodSignature(signature.name());
		result.setUniParent(signature.parent());
		for(TypeReference tref : signature.typeReferences()) {
			JavaTypeReference jref = (JavaTypeReference) tref;
			result.add(jref.erasedReference());
		}
		return result;
	}
	
//	public TypeReference erasure(JavaTypeReference jref) {
//		JavaTypeReference result = createTypeReference(erasure(jref.getTarget()), (SimpleNameSignature)jref.signature().clone());
//		result.setArrayDimension(jref.arrayDimension());
//		return result;
//	}

	public <T extends CrossReference<?,?,? extends TargetDeclaration>> CrossReference<?,?,? extends TargetDeclaration> erasure(T ref) {
		if(ref instanceof JavaTypeReference) {
			return ((JavaTypeReference) ref).erasedReference();
		} else if( ref != null){
			CrossReference<?,?,? extends TargetDeclaration> result = ref.clone();
			// replace target with erasure.
			if(ref instanceof NamedTarget) {
				NamedTarget namedTarget = (NamedTarget)result;
				InvocationTarget<?, ?> target = namedTarget.getTarget();
				if(target instanceof CrossReference) {
				  namedTarget.setTarget((InvocationTarget)erasure((T)target));
				}
			} else if(ref instanceof ElementReferenceWithTarget) {
				ElementReferenceWithTarget eref = (ElementReferenceWithTarget) result;
				eref.setTarget(erasure(eref.getTarget()));
			}
			return result;
		} else {
			return null;
		}
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
	public final DynamicChameleonProperty PRIMITIVE_TYPE;	
	public final ChameleonProperty REFERENCE_TYPE;	
	
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
			  TypeReference typeRef = createTypeReferenceInDefaultNamespace(getDefaultSuperClassFQN());
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

		public Type box(Type type) throws LookupException {
			String fqn = type.getFullyQualifiedName();
			String newFqn;
			if(fqn.equals("boolean")) {
				newFqn = "java.lang.Boolean";
			} else if (fqn.equals("byte")) {
				newFqn = "java.lang.Byte";
			} else if (fqn.equals("char")) {
				newFqn = "java.lang.Character";
			} else if (fqn.equals("short")) {
				newFqn = "java.lang.Short";
			} else if (fqn.equals("int")) {
				newFqn = "java.lang.Integer";
			} else if (fqn.equals("long")) {
				newFqn = "java.lang.Long";
			} else if (fqn.equals("float")) {
				newFqn = "java.lang.Float";
			} else if (fqn.equals("double")) {
				newFqn = "java.lang.Double";
			} else {
				throw new LookupException("Type "+fqn+" cannot be converted through boxing.");
			}
			return findType(newFqn);
		}

		public JavaTypeReference box(JavaTypeReference aRef) throws LookupException {
			String fqn = aRef.getElement().getFullyQualifiedName();
			String newFqn;
			if(fqn.equals("boolean")) {
				newFqn = "java.lang.Boolean";
			} else if (fqn.equals("byte")) {
				newFqn = "java.lang.Byte";
			} else if (fqn.equals("char")) {
				newFqn = "java.lang.Character";
			} else if (fqn.equals("short")) {
				newFqn = "java.lang.Short";
			} else if (fqn.equals("int")) {
				newFqn = "java.lang.Integer";
			} else if (fqn.equals("long")) {
				newFqn = "java.lang.Long";
			} else if (fqn.equals("float")) {
				newFqn = "java.lang.Float";
			} else if (fqn.equals("double")) {
				newFqn = "java.lang.Double";
			} else {
				throw new LookupException("Type "+fqn+" cannot be converted through boxing.");
			}
			JavaTypeReference result = createTypeReference(newFqn);
			result.setUniParent(defaultNamespace());
			return result;
		}

		@Override
		public JavaTypeReference createTypeReference(String fqn) {
			return new BasicJavaTypeReference(fqn);
		}

		@Override
		public JavaTypeReference createTypeReference(CrossReference<?, ?, ? extends TargetDeclaration> target, String name) {
			return new BasicJavaTypeReference(target, name);
		}

		@Override
		public JavaTypeReference createTypeReference(CrossReference<?, ?, ? extends TargetDeclaration> target, SimpleNameSignature signature) {
			return new BasicJavaTypeReference(target, signature);
		}

		public JavaTypeReference createTypeReference(NamedTarget target) {
			return new BasicJavaTypeReference(target);
		}
}
