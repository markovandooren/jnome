package be.kuleuven.cs.distrinet.jnome.core.language;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaExtendsReference;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaSuperReference;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.NonLocalJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaNormalMethod;
import be.kuleuven.cs.distrinet.jnome.core.modifier.PackageProperty;
import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousInnerClass;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.CapturedType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaBasicTypeArgument;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaDerivedType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaExtendsWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaIntersectionTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaPureWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaSuperWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaUnionTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.PureWildCardType;
import be.kuleuven.cs.distrinet.jnome.core.type.PureWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.RawType;
import be.kuleuven.cs.distrinet.jnome.core.type.RegularJavaType;
import be.kuleuven.cs.distrinet.rejuse.junit.BasicRevision;
import be.kuleuven.cs.distrinet.rejuse.junit.Revision;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.property.PropertyUniverse;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.TargetDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContextFactory;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.property.DynamicChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.property.PropertyRule;
import be.kuleuven.cs.distrinet.chameleon.core.property.StaticChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.core.reference.ElementReferenceWithTarget;
import be.kuleuven.cs.distrinet.chameleon.core.relation.EquivalenceRelation;
import be.kuleuven.cs.distrinet.chameleon.core.relation.StrictPartialOrder;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NamedTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.member.SimpleNameDeclarationWithParametersSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.method.MethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Parameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ParameterSubstitution;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeIndirection;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.UnionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.CapturedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.EqualityConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcardType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalParameterType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedParameterType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcardType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.AbstractInheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclarator;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.PrivateProperty;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.ProtectedProperty;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.PublicProperty;
import be.kuleuven.cs.distrinet.chameleon.support.rule.member.MemberInheritableByDefault;
import be.kuleuven.cs.distrinet.chameleon.support.rule.member.MemberInstanceByDefault;
import be.kuleuven.cs.distrinet.chameleon.support.rule.member.MemberOverridableByDefault;
import be.kuleuven.cs.distrinet.chameleon.support.rule.member.TypeExtensibleByDefault;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;

/**
 * @author Marko van Dooren
 */
public class Java extends ObjectOrientedLanguage {
	
	protected static final String JAVA_LANG_SHORT = "java.lang.Short";

	protected static final String JAVA_LANG_CHARACTER = "java.lang.Character";

	protected static final String JAVA_LANG_BYTE = "java.lang.Byte";

	protected static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";

	protected static final String JAVA_LANG_DOUBLE = "java.lang.Double";

	protected static final String JAVA_LANG_FLOAT = "java.lang.Float";

	protected static final String JAVA_LANG_LONG = "java.lang.Long";

	protected static final String JAVA_LANG_INTEGER = "java.lang.Integer";

	private static final String BOOLEAN = "boolean";

	protected static final String VOID = "void";

	protected static final String INT = "int";

	protected static final String LONG = "long";

	private static final String FLOAT = "float";

	protected static final String DOUBLE = "double";

	protected static final String BYTE = "byte";

	protected static final String CHAR = "char";

	protected static final String SHORT = "short";

	protected Java(String name, Revision version) {
		this(name,new JavaLookupFactory(),version);
	}
	protected Java(String name, LookupContextFactory lookupFactory,Revision version) {
		super(name, lookupFactory, version);
//		_nullType = new NullType(this);
		STRICTFP = new StaticChameleonProperty("strictfp", this, Declaration.class);
		SYNCHRONIZED = new StaticChameleonProperty("synchronized", this, Method.class);
		TRANSIENT = new StaticChameleonProperty("transient", this, MemberVariable.class);
		VOLATILE = new StaticChameleonProperty("volatile", this, MemberVariable.class);
		IMPLEMENTS_RELATION = new StaticChameleonProperty("implements", this, AbstractInheritanceRelation.class);
		PROTECTED = new ProtectedProperty(this, SCOPE_MUTEX);
		PRIVATE = new PrivateProperty(this, SCOPE_MUTEX);
		PUBLIC = new PublicProperty(this, SCOPE_MUTEX);
		PACKAGE_ACCESSIBLE = new PackageProperty(this, SCOPE_MUTEX);
		PRIMITIVE_TYPE = new PrimitiveTypeProperty("primitive", this);
		NUMERIC_TYPE = new NumericTypeProperty("numeric", this);
		REFERENCE_TYPE = PRIMITIVE_TYPE.inverse();
		UNBOXABLE_TYPE = new UnboxableTypeProperty("unboxable", this);
		ANNOTATION_TYPE = new StaticChameleonProperty("annotation", this, Type.class); 

		// In Java, a constructor is a class method
    // CONSTRUCTOR.addImplication(CLASS);
		// In Java, constructors are not inheritable
		CONSTRUCTOR.addImplication(INHERITABLE.inverse());
		// A numeric type is a primitive type
		NUMERIC_TYPE.addImplication(PRIMITIVE_TYPE);
		
  	INHERITABLE.addValidElementType(VariableDeclarator.class);
  	PRIVATE.addValidElementType(VariableDeclarator.class);
  	PUBLIC.addValidElementType(VariableDeclarator.class);
  	PROTECTED.addValidElementType(VariableDeclarator.class);
  	OVERRIDABLE.addValidElementType(VariableDeclarator.class);
  	DEFINED.addValidElementType(VariableDeclarator.class);
  	REFINABLE.addValidElementType(MemberVariableDeclarator.class);
  	
  	_operatorNames = new ArrayList<String>();
  	for(String string: new String[]{"==","!=","+","++","-","--","*","/","+=","-=","*=","/=","&","&&","|","||","^","!","&=","|=","^=","<<=",">>=",">>>+","%","<",">","<=",">=","%=","<<",">>",">>>"}) {
  		_operatorNames.add(string);
  	}
  	initNameMaps();
  	
	}
		
	public Java() {
		this("Java", new BasicRevision(1,6,0));
	}
	
	public Java clone() {
		return new Java();
	}
	
  public Type erasure(Type original) throws LookupException {
  	Type result;
  	if(original instanceof ArrayType) {
  		ArrayType arrayType = (ArrayType) original;
  		result = new ArrayType(erasure(arrayType.elementType()));
  	} 
  	else if(original instanceof FormalParameterType){
  		result = original;
  	} 
  	else {
  		try {
  			if(original.nbTypeParameters(TypeParameter.class) > 0 && (original.parameter(TypeParameter.class,1) instanceof FormalTypeParameter)) {
//				result = RawType.create(original);
  				result = ((JavaType)original).erasure();
			} else {
  			result = original;
  		}
  		} catch(NullPointerException exc) {
  			return null;
  		}
  	}
  	return result;
  }
  
  public boolean isOperator(Method method) {
  	return operatorNames().contains(method.name());
  }
  
  public List<String> operatorNames() {
  	return new ArrayList(_operatorNames);
  }
  
  private List<String> _operatorNames;
  
	public SimpleNameDeclarationWithParametersSignature erasure(SimpleNameDeclarationWithParametersSignature signature) {
		SimpleNameDeclarationWithParametersSignature result = new SimpleNameDeclarationWithParametersSignature(signature.name());
		result.setUniParent(signature.parent());
		for(TypeReference tref : signature.typeReferences()) {
			JavaTypeReference jref = (JavaTypeReference) tref;
			JavaTypeReference erasedReference = jref.erasedReference();
			result.add(erasedReference);
		}
		return result;
	}
	
	public <T extends CrossReference<? extends TargetDeclaration>> CrossReference<? extends TargetDeclaration> erasure(T ref) {
		CrossReference result = null;
		if(ref instanceof JavaTypeReference) {
			result = ((JavaTypeReference) ref).erasedReference();
		} else if (ref != null) { 
			result = (CrossReference) ref.clone();
			if(ref instanceof NamedTarget) {
				NamedTarget namedTarget = (NamedTarget)result;
				CrossReferenceTarget target = namedTarget.getTarget();
				if(target instanceof CrossReference) {
					namedTarget.setTarget((CrossReferenceTarget)erasure((T)target));
				}
			} else if(ref instanceof ElementReferenceWithTarget) {
				ElementReferenceWithTarget eref = (ElementReferenceWithTarget) result;
				CrossReferenceTarget target = eref.getTarget();
				if(target instanceof CrossReference) {
					eref.setTarget(erasure((CrossReference)target));
				}
			}
		}
		return result;
	}
	
	private class NumericTypeProperty extends DynamicChameleonProperty {
		private NumericTypeProperty(String name, PropertyUniverse<ChameleonProperty> universe) {
			super(name, universe, Type.class);
		}

		@Override
		public Ternary selfAppliesTo(Element element) {
			Ternary result = Ternary.FALSE;
			if(element instanceof Type) {
				String fqn = ((Type)element).getFullyQualifiedName();
				if(_numericPrimitives.contains(fqn)) {
					result = Ternary.TRUE;
				}
			}
			return result;
		}
	}
	
	private class UnboxableTypeProperty extends DynamicChameleonProperty {
		private UnboxableTypeProperty(String name, PropertyUniverse<ChameleonProperty> universe) {
			super(name, universe, Type.class);
		}

		@Override
		public Ternary selfAppliesTo(Element element) {
			Ternary result = Ternary.FALSE;
			if(element instanceof Type) {
				String fqn = ((Type)element).getFullyQualifiedName();
				if(_unboxables.contains(fqn)) {
					result = Ternary.TRUE;
				}
			}
			return result;
		}
	}

	private class PrimitiveTypeProperty extends DynamicChameleonProperty {
		private PrimitiveTypeProperty(String name, PropertyUniverse<ChameleonProperty> universe) {
			super(name, universe, Type.class);
		}

		@Override
		public Ternary selfAppliesTo(Element element) {
			Ternary result = Ternary.FALSE;
			if(element instanceof RegularJavaType) {
				String fqn = ((RegularJavaType)element).getFullyQualifiedName();
				if(_primitives.contains(fqn)) {
					result = Ternary.TRUE;
				}
			}
			return result;
		}
	}

	private final class JavaEquivalenceRelation extends EquivalenceRelation<Member> {
		@Override
		public boolean contains(Member first, Member second) throws LookupException {
			return first.equals(second);
		}
	}

	protected Type _nullType;
	
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
	public final DynamicChameleonProperty NUMERIC_TYPE;	
	public final ChameleonProperty REFERENCE_TYPE;	
	public final ChameleonProperty UNBOXABLE_TYPE;
	public final ChameleonProperty ANNOTATION_TYPE;
	
	public Type getNullType(Namespace ns) {
		if(_nullType == null) {
			try {
				_nullType = findType("null type",ns);
			} catch (LookupException e) {
				throw new ChameleonProgrammerException(e);
			}
		}
		return _nullType;
	}
	
 /*@
   @ also public behavior
   @
   @ post \result.equals(getDefaultPackage().findType("java.lang.NullPointerException")); 
   @*/
  public Type getNullInvocationException(Namespace ns) throws LookupException {
    return findType("java.lang.NullPointerException",ns);
  }
  
  protected void initializePropertyRules() {
  	MEMBER_OVERRIDABLE_BY_DEFAULT = new MemberOverridableByDefault();
  	MEMBER_INHERITABLE_BY_DEFAULT = new MemberInheritableByDefault();
  	TYPE_EXTENSIBLE_BY_DEFAULT = new TypeExtensibleByDefault();
  	addPropertyRule(MEMBER_OVERRIDABLE_BY_DEFAULT);
  	addPropertyRule(MEMBER_INHERITABLE_BY_DEFAULT);
  	addPropertyRule(TYPE_EXTENSIBLE_BY_DEFAULT);
  	addPropertyRule(new MemberInstanceByDefault());
  	addPropertyRule(new MemberPackageAccessibleByDefault());
  }
  
  private PropertyRule<Member> MEMBER_OVERRIDABLE_BY_DEFAULT;
  private PropertyRule<Member> MEMBER_INHERITABLE_BY_DEFAULT;
  private PropertyRule<Member> TYPE_EXTENSIBLE_BY_DEFAULT;

  public PropertyRule<Member> ruleMemberOverridableByDefault() {
  	return MEMBER_OVERRIDABLE_BY_DEFAULT;
  }
  
 /*@
   @ also public behavior
   @
   @ post \result.equals(getDefaultPackage().findType("java.lang.RuntimeException")); 
   @*/
  @Override
  public Type getUncheckedException(Namespace ns) throws LookupException {
    return findType("java.lang.RuntimeException",ns);
  }
  
  public Type getTopCheckedException(Namespace ns) throws LookupException {
    return findType("java.lang.Throwable",ns);
  }

  public boolean isCheckedException(Type type) throws LookupException{
  	Namespace ns = type.view().namespace();
    Type error = findType("java.lang.Error",ns);
    Type runtimeExc = findType("java.lang.RuntimeException",ns);
    return isException(type) && (! type.assignableTo(error)) && (! type.assignableTo(runtimeExc));
  }

  public boolean isException(Type type) throws LookupException {
    return type.assignableTo(findType("java.lang.Throwable",type.view().namespace()));
  }
  
	  public String getDefaultSuperClassFQN() {
	    return "java.lang.Object";
	  }
	  
    @Override
		public Type booleanType(Namespace ns) throws LookupException {
			return findType(BOOLEAN,ns);
		}

		@Override
		public Type classCastException(Namespace ns) throws LookupException {
			return findType("java.lang.ClassCastException",ns);
		}

//		@Override
//		public StrictPartialOrder<Member> hidesRelation() {
//			return _hidesRelation;
//		}
//		
//		private JavaHidesRelation _hidesRelation = new JavaHidesRelation();
		
		public StrictPartialOrder<Member> implementsRelation() {
			return _implementsRelation;
		}

		private JavaImplementsRelation _implementsRelation = new JavaImplementsRelation();

		@Override
		public Type voidType(Namespace root) throws LookupException {
			return findType(VOID,root);
		}

		@Override
		public EquivalenceRelation<Member> equivalenceRelation() {
			return _equivalenceRelation;
		}

		private JavaEquivalenceRelation _equivalenceRelation = new JavaEquivalenceRelation();

		@Override
		public JavaSubtypingRelation subtypeRelation() {
			return _subtypingRelation;
		}
		
//		public Type getDefaultSuperClass(Namespace root) throws LookupException {
//			  TypeReference typeRef = createTypeReferenceInNamespace(getDefaultSuperClassFQN(),root);
//		    Type result = typeRef.getType();
//		    if (result==null) {
//		        throw new LookupException("Default super class "+getDefaultSuperClassFQN()+" not found.");
//		    }
//		    return result;
//		}

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

//		@Override
//		protected Language cloneThis() {
//			return new Java(null);
//		}

		protected void initNameMaps() {
		  _boxMap = new HashMap<String,String>();
		  _boxMap.put(BOOLEAN, JAVA_LANG_BOOLEAN);
		  _boxMap.put(INT, JAVA_LANG_INTEGER);
		  _boxMap.put(LONG, JAVA_LANG_LONG);
		  _boxMap.put(FLOAT, JAVA_LANG_FLOAT);
		  _boxMap.put(DOUBLE, JAVA_LANG_DOUBLE);
		  _boxMap.put(BYTE, JAVA_LANG_BYTE);
		  _boxMap.put(CHAR, JAVA_LANG_CHARACTER);
		  _boxMap.put(SHORT, JAVA_LANG_SHORT);
		  
		  _unboxMap = new HashMap<String,String>();
		  _unboxMap.put(JAVA_LANG_BOOLEAN, BOOLEAN);
		  _unboxMap.put(JAVA_LANG_INTEGER, INT);
		  _unboxMap.put(JAVA_LANG_LONG, LONG);
		  _unboxMap.put(JAVA_LANG_FLOAT, FLOAT);
		  _unboxMap.put(JAVA_LANG_DOUBLE, DOUBLE);
		  _unboxMap.put(JAVA_LANG_BYTE, BYTE);
		  _unboxMap.put(JAVA_LANG_CHARACTER, CHAR);
		  _unboxMap.put(JAVA_LANG_SHORT, SHORT);
		  
		  _numericPrimitives = new HashSet<String>();
		  _numericPrimitives.add(INT);
		  _numericPrimitives.add(LONG);
		  _numericPrimitives.add(FLOAT);
		  _numericPrimitives.add(DOUBLE);
		  _numericPrimitives.add(BYTE);
		  _numericPrimitives.add(CHAR);
		  _numericPrimitives.add(SHORT);
		  
		  _primitives = new HashSet<String>(_numericPrimitives);
		  _primitives.add(BOOLEAN);
		  _primitives.add(VOID);
		  
		  _unboxables = new HashSet<String>();
		  _unboxables.add(JAVA_LANG_INTEGER);
		  _unboxables.add(JAVA_LANG_LONG);
		  _unboxables.add(JAVA_LANG_FLOAT);
		  _unboxables.add(JAVA_LANG_DOUBLE);
		  _unboxables.add(JAVA_LANG_BOOLEAN);
		  _unboxables.add(JAVA_LANG_BYTE);
		  _unboxables.add(JAVA_LANG_CHARACTER);
		  _unboxables.add(JAVA_LANG_SHORT);
		}
		
		private Map<String,String> _boxMap;

		private Map<String,String> _unboxMap;

		private Set<String> _numericPrimitives;
		
		private Set<String> _primitives;

		private Set<String> _unboxables;

		public Type box(Type type) throws LookupException {
			String fqn = type.getFullyQualifiedName();
			String newFqn = _boxMap.get(fqn);
			if(newFqn == null) {
				throw new LookupException("Type "+fqn+" cannot be converted through boxing.");
			}
			return findType(newFqn,type.view().namespace());
		}

		public Type unbox(Type type) throws LookupException {
			//SPEED this is horrible
			String fqn = type.getFullyQualifiedName();
			String newFqn = _unboxMap.get(fqn);
			if(newFqn == null) {
				throw new LookupException("Type "+fqn+" cannot be converted through unboxing.");
			}
			return findType(newFqn,type.view().namespace());
		}

		public JavaTypeReference box(JavaTypeReference aRef, Namespace root) throws LookupException {
			//SPEED this is horrible
			String fqn = aRef.getElement().getFullyQualifiedName();
			String newFqn = _boxMap.get(fqn);
			if(newFqn == null) {
				throw new LookupException("Type "+fqn+" cannot be converted through boxing.");
			}
			JavaTypeReference result = createTypeReference(newFqn);
			result.setUniParent(root);
			return result;
		}

		@Override
		public BasicJavaTypeReference createTypeReference(String fqn) {
//			Type t = _primitiveCache.get(fqn);
//			if(t != null) {
//				return new PrimitiveTypeReference(fqn,t);
//			} else {
				return new BasicJavaTypeReference(fqn);
//			}
		}

		@Override
		public BasicJavaTypeReference createTypeReference(Type type) {
			BasicJavaTypeReference result = createTypeReference(type.getFullyQualifiedName());
			if(! (type instanceof TypeIndirection)) {
				for(TypeParameter par: type.parameters(TypeParameter.class)) {
					if(par instanceof InstantiatedTypeParameter) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						result.addArgument(inst.argument().clone());
					}
				}
			}
			return result;
		}

		public BasicJavaTypeReference createExpandedTypeReference(Type type) throws LookupException {
			BasicJavaTypeReference result = createTypeReference(type.getFullyQualifiedName());
			if(! (type instanceof TypeIndirection)) {
				for(TypeParameter par: type.parameters(TypeParameter.class)) {
					if(par instanceof InstantiatedTypeParameter) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						ActualTypeArgument argument = inst.argument();
						ActualTypeArgument clone = argument.clone();
						if(argument instanceof ActualTypeArgumentWithTypeReference) {
							ActualTypeArgumentWithTypeReference arg = (ActualTypeArgumentWithTypeReference) argument;
							TypeReference tref = arg.typeReference();
							Type t = tref.getType();
							((ActualTypeArgumentWithTypeReference)clone).setTypeReference(createExpandedTypeReference(t));
						}
						result.addArgument(clone);
					}
				}
			}
			return result;
		}
		
		
		@Override
		public BasicJavaTypeReference createTypeReference(CrossReference<? extends TargetDeclaration> target, String name) {
			return new BasicJavaTypeReference(target, name);
		}

		@Override
		public BasicJavaTypeReference createTypeReference(CrossReference<? extends TargetDeclaration> target, SimpleNameSignature signature) {
			return new BasicJavaTypeReference(target, signature);
		}

		public BasicJavaTypeReference createTypeReference(NamedTarget target) {
			return new BasicJavaTypeReference(target);
		}

		public <P extends Parameter> DerivedType createDerivedType(Class<P> kind, List<P> parameters, Type baseType) {
			return new JavaDerivedType(kind, parameters, baseType);
		}
		
		public DerivedType createDerivedType(Type baseType, List<ActualTypeArgument> typeArguments) throws LookupException {
			return new JavaDerivedType(baseType,typeArguments);
		}
		
		public NormalMethod createNormalMethod(MethodHeader header) {
			return new JavaNormalMethod(header);
		}

		public TypeReference glb(List<? extends JavaTypeReference> typeReferenceList) {
			return new JavaIntersectionTypeReference(typeReferenceList);
		}

		@Override
		public IntersectionTypeReference createIntersectionReference(TypeReference first, TypeReference second) {
			List<TypeReference> list = new ArrayList<TypeReference>();
			list.add(first);
			list.add(second);
			return new JavaIntersectionTypeReference(list);
		}
		
		//FIXME get rid of this monster. Now that the code has stabilized
		//      it should be merged into the classes and a method should be
		//      added to JavaType.
		public JavaTypeReference reference(Type type) throws LookupException {
			JavaTypeReference result;
			Namespace rootNamespace = type.view().namespace();
			if(type instanceof IntersectionType) {
				IntersectionType intersection = (IntersectionType) type;
				result = new JavaIntersectionTypeReference();
				result.setUniParent(rootNamespace);
				for(Type t: ((IntersectionType)type).types()) {
					JavaTypeReference reference = reference(t);
					Element oldParent = reference.parent();
					reference.setUniParent(null);
					// first clean up the uni link, we must add it to the non-local reference.
					TypeReference nl = createNonLocalTypeReference(reference, oldParent);
					((JavaIntersectionTypeReference)result).add(nl);
				}
			} else if(type instanceof UnionType) {
				UnionType intersection = (UnionType) type;
				result = new JavaUnionTypeReference();
				result.setUniParent(rootNamespace);
				for(Type t: ((UnionType)type).types()) {
					JavaTypeReference reference = reference(t);
					Element oldParent = reference.parent();
					reference.setUniParent(null);
					// first clean up the uni link, we must add it to the non-local reference.
					TypeReference nl = createNonLocalTypeReference(reference, oldParent);
					((JavaUnionTypeReference)result).add(nl);
				}
			}
			else if (type instanceof ArrayType) {
				JavaTypeReference reference = reference(((ArrayType)type).elementType());
				Element oldParent = reference.parent();
				reference.setUniParent(null);
				result = new ArrayTypeReference(reference);
				result.setUniParent(oldParent);
			}	else if (type instanceof DerivedType){
				BasicJavaTypeReference tref = new BasicJavaTypeReference(type.signature().name());
				result = new NonLocalJavaTypeReference(tref,type.parent());
				result.setUniParent(type.parent());
				// next setup the generic parameters.
				for(TypeParameter parameter: type.parameters(TypeParameter.class)) {
					ActualTypeArgument arg = argument(parameter);
					arg.setUniParent(null);
					tref.addArgument(arg);
				}
			} else if (type instanceof FormalParameterType) {
				//result = new NonLocalJavaTypeReference(new BasicJavaTypeReference(type.signature().name()),type.parent());
				result = new BasicJavaTypeReference(type.signature().name());
				result.setUniParent(((FormalParameterType)type).parameter().parent());
			} else if (type instanceof InstantiatedParameterType) {
				//result = new NonLocalJavaTypeReference(new BasicJavaTypeReference(type.signature().name()),type.parent());
				result = new BasicJavaTypeReference(type.signature().name());
				result.setUniParent(((InstantiatedParameterType)type).parameter().parent());
			} else if (type instanceof AnonymousInnerClass) {
				String fqn = ((AnonymousInnerClass)type).invocation().getTypeReference().getElement().getFullyQualifiedName();
				result = (JavaTypeReference) createTypeReferenceInNamespace(fqn,rootNamespace);
			} else if (type instanceof RegularType) {
				// for now, if this code is invoked, there are no generic parameters.
				result = (JavaTypeReference) createTypeReferenceInNamespace(type.getFullyQualifiedName(),rootNamespace);
				if(type.nbTypeParameters(TypeParameter.class) > 0) {
//					throw new ChameleonProgrammerException("requesting reference of RegularType with type parameters");
					for(TypeParameter tpar: type.parameters(TypeParameter.class)) {
						Element lookupParent = tpar;
						JavaTypeReference nameref = createTypeReference(tpar.signature().name());
						TypeReference tref = new NonLocalJavaTypeReference(nameref, lookupParent);
						((BasicJavaTypeReference)result).addArgument(new BasicTypeArgument(tref));
					}
				}
			} else if (type instanceof RawType) {
				result = (JavaTypeReference) createTypeReferenceInNamespace(type.getFullyQualifiedName(),rootNamespace);
			} else if (type instanceof ExtendsWildcardType) {
				JavaTypeReference reference = reference(((ExtendsWildcardType)type).bound());
				Element parent = reference.parent();
				reference.setUniParent(null);
				result = new JavaExtendsReference(reference);
				result.setUniParent(parent);
			} else if (type instanceof SuperWildcardType) {
				JavaTypeReference reference = reference(((SuperWildcardType)type).bound());
				Element parent = reference.parent();
				reference.setUniParent(null);
				result = new JavaSuperReference(reference);
				result.setUniParent(parent);
			} else if (type instanceof PureWildCardType) {
				result = (JavaTypeReference) createPureWildcard();
				// A pure wildcard type has the original pure wildcard as its parent. The parent of the new reference is the parent of
				// the original pure wildcard.
				result.setUniParent(type.parent().parent());
			}
			else {
				throw new ChameleonProgrammerException("Type of type is "+type.getClass().getName());
			}
			if(result.parent() == null) {
				throw new ChameleonProgrammerException();
			}
			return result;
		}

		public static ActualTypeArgument argument(TypeParameter parameter) {
			ActualTypeArgument result = null;
			if(parameter instanceof InstantiatedTypeParameter) {
				ActualTypeArgument argument = ((InstantiatedTypeParameter)parameter).argument();
				result = argument.clone();
				if(result instanceof ActualTypeArgumentWithTypeReference) {
					ActualTypeArgumentWithTypeReference argWithRef = (ActualTypeArgumentWithTypeReference) result;
					//it will be detached from the cloned argument automatically
					NonLocalJavaTypeReference ref = new NonLocalJavaTypeReference((JavaTypeReference) argWithRef.typeReference(),argument);
					argWithRef.setTypeReference(ref);
				}
			} else {
				List<TypeConstraint> constraints = ((CapturedTypeParameter)parameter).constraints();
				if(constraints.size() == 1 && constraints.get(0) instanceof EqualityConstraint) {
					result = new BasicTypeArgument(constraints.get(0).typeReference().clone());
				}
//					// there are always constraints in a captured type parameter
//					for(TypeConstraint constraint: constraints) {
//						if(constraints instanceof ExtendsConstraint) {
//							
//						}
//					}
				
			}
			if(result != null) {
				result.setUniParent(parameter);
				return result;
			} else {
				throw new ChameleonProgrammerException();
			}
		}
		
		public <E extends Element> E replace(TypeReference replacement, Declaration declarator, E in, Class<E> kind) throws LookupException {
			return NonLocalJavaTypeReference.replace(replacement, declarator, in,kind);
		}

		@Override
		public TypeReference createNonLocalTypeReference(TypeReference tref, Element lookupParent) {
			return new NonLocalJavaTypeReference((JavaTypeReference) tref, lookupParent);
		}
		
		public BasicTypeArgument createBasicTypeArgument(TypeReference tref) {
			if(tref == null) {
				throw new ChameleonProgrammerException();
			}
			JavaBasicTypeArgument result = new JavaBasicTypeArgument(tref);
//			tref.parentLink().getOtherRelation().addListener(new AssociationListener<Element>() {
//
//				private void printTrace() {
//					try {
//						throw new Exception();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				
//				public void notifyElementAdded(Element element) {
//					System.out.println("+++++ ADDED +++++");
//					printTrace();
//				}
//
//				public void notifyElementRemoved(Element element) {
//					System.out.println("+++++ REMOVED +++++ ");
////					printTrace();
//				}
//
//				public void notifyElementReplaced(Element oldElement, Element newElement) {
//					JavaBasicTypeArgument javaBasicTypeArgument = (JavaBasicTypeArgument)newElement.nearestAncestor(JavaBasicTypeArgument.class);
//					if(javaBasicTypeArgument != null) {
//					  javaBasicTypeArgument._trace = new CreationStackTrace();
//					}
//					System.out.println("+++++ REPLACED +++++");
//				}
//			});
			return result;
		}
		
		public ExtendsWildcard createExtendsWildcard(TypeReference tref) {
			return new JavaExtendsWildcard(tref);
		}
		
		public SuperWildcard createSuperWildcard(TypeReference tref) {
			return new JavaSuperWildcard(tref);
		}
		
		public PureWildcard createPureWildcard() {
			return new JavaPureWildcard();
		}
		
//		private Map<Type, RawType> _rawCache = new HashMap<Type, RawType>();
		
//		public synchronized void putRawCache(Type type, RawType raw) {
//			if(Config.cacheDeclarations()) {
//			  _rawCache.put(type, raw);
//			} else {
//				_rawCache.clear();
//			}
//		}
		
//		public synchronized RawType getRawCache(Type original) {
//			return _rawCache.get(original);
//		}

		@Override
		public boolean upperBoundNotHigherThan(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
			JavaSubtypingRelation subtypeRelation = subtypeRelation();
			return subtypeRelation.upperBoundNotHigherThan(first, second, trace);
		}

		@Override
		public synchronized void flushCache() {
//		  _rawCache = new HashMap<Type, RawType>();
//		  subtypeRelation().flushCache();
		}

		public Type createdCapturedType(ParameterSubstitution parameterSubstitution, Type base) {
			return new CapturedType(parameterSubstitution, base);
		}

		private class PrimitiveTypeReference extends BasicJavaTypeReference {
			public PrimitiveTypeReference(String name, Type type) {
				super(name);
				_type = type;
			}
			private Type _type;
			@Override
			public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
				return (X) _type;
			}
		}
}
