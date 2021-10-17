package org.aikodi.java.core.language;


import java.util.*;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupContextFactory;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.property.DynamicChameleonProperty;
import org.aikodi.chameleon.core.property.PropertyRule;
import org.aikodi.chameleon.core.property.StaticChameleonProperty;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.core.reference.CrossReferenceWithName;
import org.aikodi.chameleon.core.reference.ElementReference;
import org.aikodi.chameleon.core.relation.EquivalenceRelation;
import org.aikodi.chameleon.core.relation.StrictPartialOrder;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.expression.NamedTarget;
import org.aikodi.chameleon.oo.language.LanguageWithBoxing;
import org.aikodi.chameleon.oo.language.LanguageWithErasure;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguageImpl;
import org.aikodi.chameleon.oo.member.SignatureWithParameters;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.*;
import org.aikodi.chameleon.oo.type.generics.*;
import org.aikodi.chameleon.oo.type.inheritance.AbstractInheritanceRelation;
import org.aikodi.chameleon.oo.variable.VariableDeclarator;
import org.aikodi.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import org.aikodi.chameleon.support.modifier.PrivateProperty;
import org.aikodi.chameleon.support.modifier.ProtectedProperty;
import org.aikodi.chameleon.support.modifier.PublicProperty;
import org.aikodi.chameleon.support.rule.member.MemberInheritableByDefault;
import org.aikodi.chameleon.support.rule.member.MemberInstanceByDefault;
import org.aikodi.chameleon.support.rule.member.MemberOverridableByDefault;
import org.aikodi.chameleon.support.rule.member.TypeExtensibleByDefault;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.java.core.expression.invocation.ExtendsReference;
import org.aikodi.java.core.expression.invocation.JavaSuperReference;
import org.aikodi.java.core.expression.invocation.NonLocalJavaTypeReference;
import org.aikodi.java.core.modifier.PackageProperty;
import org.aikodi.java.core.property.ValueClass;
import org.aikodi.java.core.type.AnonymousInnerClass;
import org.aikodi.java.core.type.ArrayType;
import org.aikodi.java.core.type.BasicJavaTypeReference;
import org.aikodi.java.core.type.CapturedType;
import org.aikodi.java.core.type.DirectJavaTypeReference;
import org.aikodi.java.core.type.JavaConstrainedTypeReference;
import org.aikodi.java.core.type.JavaEqualityTypeArgument;
import org.aikodi.java.core.type.JavaExtendsWildcard;
import org.aikodi.java.core.type.JavaIntersectionTypeReference;
import org.aikodi.java.core.type.JavaPureWildcard;
import org.aikodi.java.core.type.JavaSuperWildcard;
import org.aikodi.java.core.type.JavaType;
import org.aikodi.java.core.type.JavaTypeInstantiation;
import org.aikodi.java.core.type.JavaTypeReference;
import org.aikodi.java.core.type.JavaUnionTypeReference;
import org.aikodi.java.core.type.NullType;
import org.aikodi.java.core.type.PureWildcard;
import org.aikodi.java.core.type.RawType;
import org.aikodi.java.core.type.RegularJavaType;
import org.aikodi.java.workspace.JavaView;
import org.aikodi.rejuse.junit.BasicRevision;
import org.aikodi.rejuse.junit.Revision;
import org.aikodi.rejuse.logic.ternary.Ternary;

/**
 * A class representing the Java programming language.
 * 
 * FIXME This class has too many responsibilities with respect to subtyping.
 * 
 * @author Marko van Dooren
 */
public class Java7 extends ObjectOrientedLanguageImpl implements LanguageWithBoxing, LanguageWithErasure {

	public static final String NAME = "Java";

	protected static final String JAVA_LANG_SHORT = "java.lang.Short";

	protected static final String JAVA_LANG_CHARACTER = "java.lang.Character";

	protected static final String JAVA_LANG_BYTE = "java.lang.Byte";

	protected static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";

	protected static final String JAVA_LANG_DOUBLE = "java.lang.Double";

	protected static final String JAVA_LANG_FLOAT = "java.lang.Float";

	protected static final String JAVA_LANG_LONG = "java.lang.Long";

	protected static final String JAVA_LANG_INTEGER = "java.lang.Integer";

	protected static final String BOOLEAN = "boolean";

	protected static final String VOID = "void";

	protected static final String INT = "int";

	protected static final String LONG = "long";

	protected static final String FLOAT = "float";

	protected static final String DOUBLE = "double";

	protected static final String BYTE = "byte";

	protected static final String CHAR = "char";

	protected static final String SHORT = "short";

	protected Java7(String name, Revision version) {
		this(name,new JavaLookupFactory(),version);
	}
	protected Java7(String name, LookupContextFactory lookupFactory,Revision version) {
		super(name, lookupFactory, version);

		STRICTFP = add(new StaticChameleonProperty("strictfp", Declaration.class));
		SYNCHRONIZED = add(new StaticChameleonProperty("synchronized", Method.class));
		TRANSIENT = add(new StaticChameleonProperty("transient", Variable.class));
		VOLATILE = add(new StaticChameleonProperty("volatile", Variable.class));
		IMPLEMENTS_RELATION = add(new StaticChameleonProperty("implements", AbstractInheritanceRelation.class));
		PROTECTED = add(new ProtectedProperty(SCOPE_MUTEX));
		PRIVATE = add(new PrivateProperty(SCOPE_MUTEX));
		PUBLIC = add(new PublicProperty(SCOPE_MUTEX));
		PACKAGE_ACCESSIBLE = add(new PackageProperty(SCOPE_MUTEX));
		NUMERIC_TYPE = add(new NumericTypeProperty("numeric"));
		REFERENCE_TYPE = PRIMITIVE_TYPE.inverse();
		UNBOXABLE_TYPE = add(new UnboxableTypeProperty("unboxable"));
		ANNOTATION_TYPE = add(new StaticChameleonProperty("annotation", Type.class)); 
		DEFAULT = add(new StaticChameleonProperty("default", Method.class));
		DEFAULT.addContradiction(ABSTRACT());
		// In Java, a constructor is a class method
		// CONSTRUCTOR.addImplication(CLASS);
		// In Java, constructors are not inheritable
		CONSTRUCTOR.addImplication(INHERITABLE().inverse());
		// A numeric type is a primitive type
		NUMERIC_TYPE.addImplication(PRIMITIVE_TYPE);

		INHERITABLE().addValidElementType(VariableDeclarator.class);
		PRIVATE.addValidElementType(VariableDeclarator.class);
		PUBLIC.addValidElementType(VariableDeclarator.class);
		PROTECTED.addValidElementType(VariableDeclarator.class);
		OVERRIDABLE().addValidElementType(VariableDeclarator.class);
		DEFINED().addValidElementType(VariableDeclarator.class);
		REFINABLE().addValidElementType(MemberVariableDeclarator.class);
		FINAL().addValidElementType(MemberVariableDeclarator.class);

		TYPE_WITH_VALUE_SEMANTICS = add(new ValueClass());
		TYPE_WITH_REFERENCE_SEMANTICS = TYPE_WITH_VALUE_SEMANTICS.inverse();

		for(String string: new String[]{"==","!=","+","++","-","--","*","/","+=","-=","*=","/=","&","&&","|","||","^","!","&=","|=","^=","<<=",">>=",">>>+","%","<",">","<=",">=","%=","<<",">>",">>>"}) {
			_operatorNames.add(string);
		}
	}

	protected DynamicChameleonProperty createPrimitiveTypeProperty()
	{
		return new PrimitiveTypeProperty("primitive", namesOfPrimitiveTypes());
	}

	public Java7() {
		this("Java", new BasicRevision(1,7,0));
	}

	public Java7 clone() {
		return new Java7();
	}

	/**
	 * Return the erasure of the given type.
	 *
	 * @param original
	 */
	public Type erasure(Type original) {
		return erasedType(original);
	}

	public static Type erasedType(Type original) {
		Type result;
		if(original instanceof ArrayType) {
			result = ((ArrayType) original).erasure();
		}
		else if(original instanceof TypeVariable){
			result = original;
		}
		else {
			try {
				if(original.nbTypeParameters(TypeParameter.class) > 0 && (original.parameter(TypeParameter.class,0) instanceof FormalTypeParameter)) {
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

	public Set<String> operatorNames() {
		return java.util.Collections.unmodifiableSet(_operatorNames);
	}

	private final Set<String> _operatorNames = new HashSet<String>();

	public SignatureWithParameters erasure(SignatureWithParameters signature) {
		SignatureWithParameters result = new SignatureWithParameters(signature.name());
		result.setUniParent(signature.parent());
		for(TypeReference tref : signature.typeReferences()) {
			JavaTypeReference jref = (JavaTypeReference) tref;
			TypeReference erasedReference = jref.erasedReference();
			result.add(erasedReference);
		}
		return result;
	}

	/**
	 * FIXME Move this method to the Java type references themselves.
	 * This code is no longer experimental.
	 * 
	 * @param ref
	 * @return
	 */
	public <T extends CrossReference<? extends Declaration>> CrossReference<? extends Declaration> erasure(T ref) {
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
			} else if(ref instanceof ElementReference) {
				ElementReference eref = (ElementReference) result;
				CrossReferenceTarget target = eref.getTarget();
				if(target instanceof CrossReference) {
					eref.setTarget(erasure((CrossReference)target));
				}
			}
		}
		return result;
	}

	private class NumericTypeProperty extends DynamicChameleonProperty {
		private NumericTypeProperty(String name) {
			super(name, Type.class);
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
		private UnboxableTypeProperty(String name) {
			super(name, Type.class);
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

	public static class PrimitiveTypeProperty extends DynamicChameleonProperty {
		public PrimitiveTypeProperty(String name, Set<String> primitives) {
			super(name, Type.class);
			_primitives = new HashSet<>(primitives);
		}

		private Set<String> _primitives;

		@Override
		public Ternary selfAppliesTo(Element element) {
			Ternary result = Ternary.FALSE;
			if(element instanceof RegularJavaType) {
				String fqn = ((RegularJavaType)element).name();
				if(_primitives.contains(fqn)) {
					result = Ternary.TRUE;
				}
			}
			return result;
		}
	}

	private final class JavaEquivalenceRelation implements EquivalenceRelation<Declaration> {
		@Override
		public boolean contains(Declaration first, Declaration second) throws LookupException {
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
	public final DynamicChameleonProperty NUMERIC_TYPE;
	public final ChameleonProperty REFERENCE_TYPE;	
	public final ChameleonProperty UNBOXABLE_TYPE;
	public final ChameleonProperty ANNOTATION_TYPE;
	public final ChameleonProperty DEFAULT;
	public final ChameleonProperty TYPE_WITH_VALUE_SEMANTICS;
	public final ChameleonProperty TYPE_WITH_REFERENCE_SEMANTICS;


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
		TYPE_REFERENCE_BY_DEFAULT = new TypeReferenceByDefault();
		addPropertyRule(MEMBER_OVERRIDABLE_BY_DEFAULT);
		addPropertyRule(MEMBER_INHERITABLE_BY_DEFAULT);
		addPropertyRule(TYPE_EXTENSIBLE_BY_DEFAULT);
		addPropertyRule(TYPE_REFERENCE_BY_DEFAULT);
		addPropertyRule(new MemberInstanceByDefault());
		addPropertyRule(new MemberPackageAccessibleByDefault());
	}

	private PropertyRule<Type> TYPE_REFERENCE_BY_DEFAULT;
	private PropertyRule<Declaration> MEMBER_OVERRIDABLE_BY_DEFAULT;
	private PropertyRule<Declaration> MEMBER_INHERITABLE_BY_DEFAULT;
	private PropertyRule<Declaration> TYPE_EXTENSIBLE_BY_DEFAULT;

	public PropertyRule<Declaration> ruleMemberOverridableByDefault() {
		return MEMBER_OVERRIDABLE_BY_DEFAULT;
	}

	public ChameleonProperty REFERENCE_TYPE() {
		return REFERENCE_TYPE;
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

	public StrictPartialOrder<Declaration> implementsRelation() {
		return _implementsRelation;
	}

	private JavaImplementsRelation _implementsRelation = new JavaImplementsRelation();

	@Override
	public Type voidType(Namespace root) throws LookupException {
		return findType(VOID,root);
	}

	@Override
	public EquivalenceRelation<Declaration> equivalenceRelation() {
		return _equivalenceRelation;
	}

	private JavaEquivalenceRelation _equivalenceRelation = new JavaEquivalenceRelation();

	@Override
	public JavaSubtypingRelation subtypeRelation() {
		return _subtypingRelation;
	}

	private JavaSubtypingRelation _subtypingRelation = new JavaSubtypingRelation(this);

	/**
	 * Returns true if the given character is a valid character
	 * for an identifier.
	 */
	@Override
	public boolean isValidIdentifierCharacter(char character){
		return Character.isJavaIdentifierPart(character);
	}

	protected void initNameMaps() {
		_boxMap = boxMap();
		_unboxMap = unboxMap();
		_numericPrimitives = namesOfNumericPrimitives();
		_unboxables = unboxables();
	}

	public static Map<String, String> boxMap() {
		Map<String, String> result = new HashMap<String,String>();
		result.put(BOOLEAN, JAVA_LANG_BOOLEAN);
		result.put(INT, JAVA_LANG_INTEGER);
		result.put(LONG, JAVA_LANG_LONG);
		result.put(FLOAT, JAVA_LANG_FLOAT);
		result.put(DOUBLE, JAVA_LANG_DOUBLE);
		result.put(BYTE, JAVA_LANG_BYTE);
		result.put(CHAR, JAVA_LANG_CHARACTER);
		result.put(SHORT, JAVA_LANG_SHORT);
		return result;
	}

	public static Map<String, String> unboxMap() {
		Map<String, String> result = new HashMap<String,String>();
		result.put(JAVA_LANG_BOOLEAN, BOOLEAN);
		result.put(JAVA_LANG_INTEGER, INT);
		result.put(JAVA_LANG_LONG, LONG);
		result.put(JAVA_LANG_FLOAT, FLOAT);
		result.put(JAVA_LANG_DOUBLE, DOUBLE);
		result.put(JAVA_LANG_BYTE, BYTE);
		result.put(JAVA_LANG_CHARACTER, CHAR);
		result.put(JAVA_LANG_SHORT, SHORT);
		return result;
	}

	public static Set<String> unboxables() {
		Set<String> result = new HashSet<String>();
		result.add(JAVA_LANG_INTEGER);
		result.add(JAVA_LANG_LONG);
		result.add(JAVA_LANG_FLOAT);
		result.add(JAVA_LANG_DOUBLE);
		result.add(JAVA_LANG_BOOLEAN);
		result.add(JAVA_LANG_BYTE);
		result.add(JAVA_LANG_CHARACTER);
		result.add(JAVA_LANG_SHORT);
		return result;
	}

	public static Set<String> namesOfNumericPrimitives() {
		Set<String> numericPrimitives = new HashSet<String>();
		numericPrimitives.add(INT);
		numericPrimitives.add(LONG);
		numericPrimitives.add(FLOAT);
		numericPrimitives.add(DOUBLE);
		numericPrimitives.add(BYTE);
		numericPrimitives.add(CHAR);
		numericPrimitives.add(SHORT);
		return numericPrimitives;
	}

	/**
	 * Return the names of the primitive types.
	 * @return A non-null set that contains the names of the numeric types
	 * plus "void" and "boolean".
	 */
	public static Set<String> namesOfPrimitiveTypes() {
		Set<String> result = namesOfNumericPrimitives();
		result.add(BOOLEAN);
		result.add(VOID);
		return result;
	}

	private Map<String,String> _boxMap;

	private Map<String,String> _unboxMap;

	private Set<String> _numericPrimitives;

	private Set<String> _unboxables;

	public Type boxedType(Type type) throws LookupException {
		String fqn = type.getFullyQualifiedName();
		String boxedFqn = _boxMap.get(fqn);
		if(boxedFqn == null) {
			throw new LookupException("Type "+fqn+" cannot be converted through boxing.");
		}

		return findType(boxedFqn, type.view().namespace());
	}

	public String boxName(String fqn) {
		return _boxMap.get(fqn);
	}

	public boolean isBoxable(Type type) throws LookupException {
		return type.isTrue(PRIMITIVE_TYPE);
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

	public boolean isUnboxable(Type type) {
		String fqn = type.getFullyQualifiedName();
		return _unboxMap.get(fqn) != null;
	}

	public boolean convertibleToNumeric(Type type) {
		return isUnboxable(type) || _numericPrimitives.contains(type.getFullyQualifiedName());
	}

	public TypeReference box(TypeReference aRef, Namespace root) throws LookupException {
		if (! (aRef instanceof CrossReferenceWithName)) {
			return aRef;
		}
		String newFqn = _boxMap.get(((CrossReferenceWithName)(aRef)).name());
		if(newFqn == null) {
			return aRef;
		}
		TypeReference result = createTypeReference(newFqn);
		result.setUniParent(root);
		return result;
	}

	@Override
	public BasicJavaTypeReference createTypeReference(String fqn) {
		return new BasicJavaTypeReference(fqn);
	}

	public CrossReferenceTarget createTypeReferenceTarget(String fqn) {
		return BasicJavaTypeReference.typeReferenceTarget(fqn);
	}

	@Override
	public BasicJavaTypeReference createTypeReference(Type type) {
		BasicJavaTypeReference result = createTypeReference(type.getFullyQualifiedName());
		if(! (type instanceof TypeIndirection)) {
			for(TypeParameter par: type.parameters(TypeParameter.class)) {
				if(par instanceof InstantiatedTypeParameter) {
					InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
					result.addArgument(Util.clone(inst.argument()));
				}
			}
		}
		return result;
	}

	@Override
	public BasicJavaTypeReference createTypeReference(CrossReference<? extends Declaration> target, String name) {
		return new BasicJavaTypeReference(target, name);
	}

	public BasicJavaTypeReference createTypeReference(NamedTarget target) {
		return new BasicJavaTypeReference(target);
	}

	public <P extends Parameter> TypeInstantiation instantiatedType(Class<P> kind, List<P> parameters, Type baseType) {
		return new JavaTypeInstantiation(new FunctionalParameterSubstitution<P>(kind, parameters), baseType);
	}

	public TypeInstantiation createDerivedType(Type baseType, List<TypeArgument> typeArguments) throws LookupException {
		return ((RegularJavaType)baseType).createDerivedType(typeArguments);
	}

	@Override
	public IntersectionTypeReference createIntersectionReference(TypeReference... types) {
		return new JavaIntersectionTypeReference(Arrays.asList(types));
	}

	@Override
	public UnionTypeReference createUnionReference(TypeReference... types) {
		return new JavaUnionTypeReference(Arrays.asList(types));
	}

	//FIXME get rid of this monster. Now that the code has stabilized
	//      it should be merged into the classes and a method should be
	//      added to JavaType.
	public TypeReference reference(Type type) {
		TypeReference result;
		Namespace rootNamespace = type.view().namespace();
		if(type instanceof NullType) {
			return new DirectJavaTypeReference(type);
		} else if(type instanceof IntersectionType) {
			result = ((IntersectionType)type).reference();
		} else if(type instanceof UnionType) {
			result = ((UnionType)type).reference();
		} else if (type instanceof ArrayType) {
			result = ((ArrayType)type).reference();
		} else if (type instanceof TypeInstantiation){
			result = ((TypeInstantiation)type).reference();
		} else if (type instanceof TypeVariable) {
			result = ((TypeVariable)type).reference();
		} else if (type instanceof InstantiatedParameterType) {
			result = ((InstantiatedParameterType)type).reference();
		} else if (type instanceof AnonymousInnerClass) {
			result = ((AnonymousInnerClass)type).reference();
		} else if (type instanceof RegularType) {
			// for now, if this code is invoked, there are no generic parameters.
			List<TypeParameter> parameters = type.parameters(TypeParameter.class);
			List<TypeArgument> arguments = new ArrayList<>();
			for(TypeParameter tpar: parameters) {
				Element lookupParent = tpar;
				TypeReference nameref = createTypeReference(tpar.name());
				TypeReference tref = createNonLocalTypeReference(nameref, lookupParent);
				arguments.add(createEqualityTypeArgument(tref));
			}
			result = createTypeReference(type.getFullyQualifiedName(), parameters, arguments);
			result.setUniParent(rootNamespace);
		} else if (type instanceof RawType) {
			result = ((RawType)type).reference();
		} else if (type instanceof ExtendsWildcardType) {
			result = ((ExtendsWildcardType)type).reference();
		} else if (type instanceof SuperWildcardType) {
			result = ((SuperWildcardType)type).reference();
		} else if (type instanceof ConstrainedType) {
			result = ((ConstrainedType) type).reference();
		}
		else {
			throw new ChameleonProgrammerException("Type of type is "+type.getClass().getName());
		}
		if(result.lexical().parent() == null) {
			throw new ChameleonProgrammerException();
		}
		return result;
	}

	@Override
	public TypeReference createTypeReference(String fqn, List<TypeParameter> parameters, List<TypeArgument> arguments) {
		BasicJavaTypeReference result = new BasicJavaTypeReference(fqn);
		for (TypeArgument arg : arguments) {
			result.addArgument(arg);
		}
		return result;
	}

	@Override
	public TypeReference createSuperReference(TypeReference reference) {
		return new JavaSuperReference(reference);
	}

	@Override
	public TypeReference createExtendsReference(TypeReference reference) {
		return new ExtendsReference(reference);
	}

	@Override
	public JavaConstrainedTypeReference createConstrainedTypeReference() {
		return new JavaConstrainedTypeReference();
	}

	public static TypeArgument cloneActualTypeArgument(TypeParameter parameter) {
		TypeArgument result = null;
		if(parameter instanceof InstantiatedTypeParameter) {
			TypeArgument argument = ((InstantiatedTypeParameter)parameter).argument();
			result = Util.clone(argument);
			if(result instanceof TypeArgumentWithTypeReference) {
				TypeArgumentWithTypeReference argWithRef = (TypeArgumentWithTypeReference) result;
				//it will be detached from the cloned argument automatically
				NonLocalJavaTypeReference ref = new NonLocalJavaTypeReference((JavaTypeReference) argWithRef.typeReference(),argument);
				argWithRef.setTypeReference(ref);
			}
		} else {
			List<TypeConstraint> constraints = ((CapturedTypeParameter)parameter).constraints();
			if(constraints.size() == 1){ 
				TypeConstraint typeConstraint = constraints.get(0);
				final TypeReference clone = Util.clone(typeConstraint.typeReference());
				if(typeConstraint instanceof EqualityConstraint) {
					result = parameter.language(Java7.class).createEqualityTypeArgument(clone);
				} else if(typeConstraint instanceof ExtendsConstraint) {
					result = parameter.language(Java7.class).createExtendsWildcard(clone);
				} else if(typeConstraint instanceof SuperConstraint) {
					result = parameter.language(Java7.class).createSuperWildcard(clone);
				}
			}				
		}
		if(result != null) {
			result.setUniParent(parameter);
			return result;
		} else {
			throw new ChameleonProgrammerException();
		}
	}

	@Override
	public NonLocalTypeReference createNonLocalTypeReference(TypeReference tref, Element lookupTarget) {
		return new NonLocalJavaTypeReference((JavaTypeReference) tref, lookupTarget);
	}

	@Override
	public EqualityTypeArgument createEqualityTypeArgument(TypeReference tref) {
		JavaEqualityTypeArgument result = new JavaEqualityTypeArgument(tref);
		return result;
	}

	@Override
	public TypeReference createDirectTypeReference(Type type) {
		return new DirectJavaTypeReference(type);
	}

	@Override
	public ExtendsWildcard createExtendsWildcard(TypeReference tref) {
		return new JavaExtendsWildcard(tref);
	}

	@Override
	public SuperWildcard createSuperWildcard(TypeReference tref) {
		return new JavaSuperWildcard(tref);
	}

	public PureWildcard createPureWildcard() {
		return new JavaPureWildcard();
	}

	//		@Override
	//		public boolean upperBoundNotHigherThan(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
	//			JavaSubtypingRelation subtypeRelation = subtypeRelation();
	//			return subtypeRelation.upperBoundNotHigherThan(first, second, trace);
	//		}

	public Type createdCapturedType(ParameterSubstitution<?> parameterSubstitution, Type base) {
		return new CapturedType(parameterSubstitution, base);
	}

	private class PrimitiveTypeReference extends BasicJavaTypeReference {
		public PrimitiveTypeReference(String name, Type type) {
			super(name);
			_type = type;
		}
		private Type _type;

		@Override
		public Type getElement() throws LookupException {
			return _type;
		}

	}

	public Type binaryNumericPromotion(Type first, Type second) throws LookupException {
		if(! convertibleToNumeric(first) || ! convertibleToNumeric(second)) {
			throw new LookupException("Cannot perform binary numeric conversion on "+first.getFullyQualifiedName()+" and "+ second.getFullyQualifiedName());
		}
		Type ufirst = unboxIfNecessary(first);
		Type usecond = unboxIfNecessary(second);
		JavaView view = first.view(JavaView.class);
		Type tdouble = view.primitiveType("double");
		Type result = null;
		if(ufirst.sameAs(tdouble) || usecond.sameAs(tdouble)) {
			result = tdouble;
		} else {
			Type tfloat = view.primitiveType("float");
			if(ufirst.sameAs(tfloat) || usecond.sameAs(tfloat)) {
				result = tfloat;
			} else {
				Type tlong = view.primitiveType("long");
				if(ufirst.sameAs(tlong) || usecond.sameAs(tlong)) {
					result = tlong;
				} else {
					result = view.primitiveType("int");
				}
			}
		}
		return result;
	}

	private Type unboxIfNecessary(Type type) throws LookupException {
		if(isUnboxable(type)) {
			return unbox(type);
		} else {
			return type;
		}
	}

	@Override
	public View createView() {
		return new JavaView(createRootNamespace(), this);
	}
}
