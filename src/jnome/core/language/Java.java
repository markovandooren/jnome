package jnome.core.language;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jnome.core.expression.invocation.NonLocalJavaTypeReference;
import jnome.core.modifier.PackageProperty;
import jnome.core.type.ArrayType;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaBasicTypeArgument;
import jnome.core.type.JavaExtendsWildcard;
import jnome.core.type.JavaIntersectionTypeReference;
import jnome.core.type.JavaPureWildcard;
import jnome.core.type.JavaSuperWildcard;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.JavaUnionTypeReference;
import jnome.core.type.NullType;
import jnome.core.type.PureWildCardType;
import jnome.core.type.PureWildcard;
import jnome.core.type.RawType;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.property.PropertyUniverse;

import chameleon.core.Config;
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
import chameleon.core.variable.MemberVariable;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.ConstructedType;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.IntersectionType;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.UnionType;
import chameleon.oo.type.generics.ActualType;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.CapturedTypeParameter;
import chameleon.oo.type.generics.EqualityConstraint;
import chameleon.oo.type.generics.ExtendsConstraint;
import chameleon.oo.type.generics.ExtendsWildcard;
import chameleon.oo.type.generics.ExtendsWildcardType;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.SuperWildcard;
import chameleon.oo.type.generics.SuperWildcardType;
import chameleon.oo.type.generics.TypeConstraint;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.support.member.simplename.SimpleNameMethodSignature;
import chameleon.support.modifier.PrivateProperty;
import chameleon.support.modifier.ProtectedProperty;
import chameleon.support.modifier.PublicProperty;
import chameleon.support.rule.member.MemberInheritableByDefault;
import chameleon.support.rule.member.MemberInstanceByDefault;
import chameleon.support.rule.member.MemberOverridableByDefault;
import chameleon.support.rule.member.TypeExtensibleByDefault;
import chameleon.support.variable.VariableDeclarator;
import chameleon.util.Pair;

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
		PRIMITIVE_TYPE = new PrimitiveTypeProperty("primitive", this);
		NUMERIC_TYPE = new NumericTypeProperty("numeric", this);
		REFERENCE_TYPE = PRIMITIVE_TYPE.inverse();
		UNBOXABLE_TYPE = new UnboxableTypeProperty("unboxable", this);
		ANNOTATION_TYPE = new StaticChameleonProperty("annotation", this, Type.class); 

		// In Java, a constructor is a class method
		CONSTRUCTOR.addImplication(CLASS);
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
	}
	
	public Java() {
		this("Java");
	}
	
	
  public Type erasure(Type original) throws LookupException {
  	Type result;
  	if(original instanceof ArrayType) {
  		ArrayType arrayType = (ArrayType) original;
  		result = new ArrayType(erasure(arrayType.elementType()));
  	} 
  	else if(original instanceof ConstructedType){
//  		FormalTypeParameter formal = ((ConstructedType)original).parameter();
//  		List<TypeConstraint> constraints = formal.constraints();
//  		if(constraints.size() > 0) {
//  			TypeConstraint first = constraints.get(0);
//  			  result = erasure(first.bound());	
//  		} else {
//  			result = getDefaultSuperClass();
//  		}
  		result = original;
  	} 
  	else {
  		// Regular TYPE
			if(original.nbTypeParameters() > 0 && (original.parameter(1) instanceof FormalTypeParameter)) {
				result = RawType.create(original);
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
			JavaTypeReference erasedReference = jref.erasedReference();
			result.add(erasedReference);
		}
		return result;
	}
	
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

	private static class NumericTypeProperty extends DynamicChameleonProperty {
		private NumericTypeProperty(String name, PropertyUniverse<ChameleonProperty> universe) {
			super(name, universe, Type.class);
		}

		@Override
		public Ternary appliesTo(Element element) {
			Ternary result = Ternary.FALSE;
			Java language = (Java) element.language(Java.class);
			if(element instanceof Type) {
				String fqn = ((Type)element).getFullyQualifiedName();
				if(fqn.equals("int") || fqn.equals("long")|| fqn.equals("float")|| fqn.equals("double")
						|| fqn.equals("byte")|| fqn.equals("char") || fqn.equals("short")) {
					result = Ternary.TRUE;
				}
			}
			return result;
		}
	}
	
	private static class UnboxableTypeProperty extends DynamicChameleonProperty {
		private UnboxableTypeProperty(String name, PropertyUniverse<ChameleonProperty> universe) {
			super(name, universe, Type.class);
		}

		@Override
		public Ternary appliesTo(Element element) {
			Ternary result = Ternary.FALSE;
			if(element instanceof Type) {
				String fqn = ((Type)element).getFullyQualifiedName();
				if(fqn.equals("java.lang.Integer") || fqn.equals("java.lang.Long")|| fqn.equals("java.lang.Float")|| fqn.equals("java.lang.Double")
						|| fqn.equals("java.lang.Boolean")|| fqn.equals("java.lang.Byte")|| fqn.equals("java.lang.Character") || fqn.equals("java.lang.Short")) {
					result = Ternary.TRUE;
				}
			}
			return result;
		}
	}

	private static class PrimitiveTypeProperty extends DynamicChameleonProperty {
		private PrimitiveTypeProperty(String name, PropertyUniverse<ChameleonProperty> universe) {
			super(name, universe, Type.class);
		}

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
	public final DynamicChameleonProperty NUMERIC_TYPE;	
	public final ChameleonProperty REFERENCE_TYPE;	
	public final ChameleonProperty UNBOXABLE_TYPE;
	public final ChameleonProperty ANNOTATION_TYPE;
	
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
  	addPropertyRule(new MemberInstanceByDefault());
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
		public JavaSubtypingRelation subtypeRelation() {
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

		public Type unbox(Type type) throws LookupException {
			String fqn = type.getFullyQualifiedName();
			String newFqn;
			if(fqn.equals("java.lang.Boolean")) {
				newFqn = "boolean";
			} else if (fqn.equals("java.lang.Byte")) {
				newFqn = "byte";
			} else if (fqn.equals("java.lang.Character")) {
				newFqn = "char";
			} else if (fqn.equals("java.lang.Short")) {
				newFqn = "short";
			} else if (fqn.equals("java.lang.Integer")) {
				newFqn = "int";
			} else if (fqn.equals("java.lang.Long")) {
				newFqn = "long";
			} else if (fqn.equals("java.lang.Float")) {
				newFqn = "float";
			} else if (fqn.equals("java.lang.Double")) {
				newFqn = "double";
			} else {
				throw new LookupException("Type "+fqn+" cannot be converted through unboxing.");
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
		
		public JavaTypeReference reference(Type type) {
			JavaTypeReference result;
			if(type instanceof IntersectionType) {
				IntersectionType intersection = (IntersectionType) type;
				result = new JavaIntersectionTypeReference();
				result.setUniParent(defaultNamespace());
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
				result.setUniParent(defaultNamespace());
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
				for(TypeParameter parameter: type.parameters()) {
					ActualTypeArgument arg = argument(parameter);
					arg.setUniParent(null);
					tref.addArgument(arg);
				}
			} else if (type instanceof ConstructedType) {
				//result = new NonLocalJavaTypeReference(new BasicJavaTypeReference(type.signature().name()),type.parent());
				result = new BasicJavaTypeReference(type.signature().name());
				result.setUniParent(((ConstructedType)type).parameter().parent());
			} else if (type instanceof ActualType) {
				//result = new NonLocalJavaTypeReference(new BasicJavaTypeReference(type.signature().name()),type.parent());
				result = new BasicJavaTypeReference(type.signature().name());
				result.setUniParent(((ActualType)type).parameter().parent());
			} else if (type instanceof RegularType) {
				// for now, if this code is invoked, there are no generic parameters.
				if(type.parameters().size() > 0) {
					throw new ChameleonProgrammerException("requesting reference of RegularType with type parameters");
				}
				result = (JavaTypeReference) createTypeReferenceInDefaultNamespace(type.getFullyQualifiedName());
			} else if (type instanceof RawType) {
				result = (JavaTypeReference) createTypeReferenceInDefaultNamespace(type.getFullyQualifiedName());
			} else if (type instanceof ExtendsWildcardType) {
				JavaTypeReference reference = reference(((ExtendsWildcardType)type).bound());
				Element parent = reference.parent();
				reference.setUniParent(null);
				result = (JavaTypeReference) createExtendsWildcard(reference);
				result.setUniParent(parent);
			} else if (type instanceof SuperWildcardType) {
				JavaTypeReference reference = reference(((SuperWildcardType)type).bound());
				Element parent = reference.parent();
				reference.setUniParent(null);
				result = (JavaTypeReference) createSuperWildcard(reference);
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
			try {
				result.getElement();
			} catch (LookupException e) {
				e.printStackTrace();
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

		@Override
		public TypeReference createNonLocalTypeReference(TypeReference tref, Element lookupParent) {
			return new NonLocalJavaTypeReference((JavaTypeReference) tref, lookupParent);
		}
		
		public BasicTypeArgument createBasicTypeArgument(TypeReference tref) {
			return new JavaBasicTypeArgument(tref);
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
		
		private Map<Type, RawType> _rawCache = new HashMap<Type, RawType>();
		
		public void putRawCache(Type type, RawType raw) {
			if(Config.cacheDeclarations()) {
			  _rawCache.put(type, raw);
			} else {
				_rawCache.clear();
			}
		}
		
		public RawType getRawCache(Type original) {
			return _rawCache.get(original);
		}

		@Override
		public boolean upperBoundNotHigherThan(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
			JavaSubtypingRelation subtypeRelation = subtypeRelation();
			return subtypeRelation.upperBoundNotHigherThan(first, second, trace);
		}


}
