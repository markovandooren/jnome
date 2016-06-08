package be.kuleuven.cs.distrinet.jnome.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.support.member.simplename.operator.infix.InfixOperator;
import org.aikodi.chameleon.support.member.simplename.operator.postfix.PostfixOperator;
import org.aikodi.chameleon.support.member.simplename.operator.prefix.PrefixOperator;
import org.aikodi.chameleon.support.modifier.Native;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.chameleon.support.modifier.ValueType;
import org.aikodi.chameleon.workspace.DeclarationLoader;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.InputException;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.RegularJavaType;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

public class PrimitiveTypeFactory {

	public PrimitiveTypeFactory(JavaView view) {
		_view = view;
	}

	private JavaView view() {
		return _view;
	}
	
	private JavaView _view;
	
	public ObjectOrientedLanguage language() {
		return (ObjectOrientedLanguage) _view.language();
	}
	
	public void addPrimitives(String root, DocumentScanner loader) {
		addVoid(root,loader);
		addDouble(root,loader);
		addFloat(root,loader);
		addLong(root,loader);
		addInt(root,loader);
		addShort(root,loader);
		addChar(root,loader);
		addByte(root,loader);
		addBoolean(root,loader);
	}

	protected void addBoolean(String mm, DocumentScanner loader) {
		Public pub = new Public();
		Type booleanT = new PrimitiveType("boolean");
		booleanT.addModifier(pub);
		try {
			new DeclarationLoader(booleanT,mm,view(), loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		addPrefixOperator(booleanT, "boolean", "!");
		addInfixOperator(booleanT, "boolean", equality(), "boolean");
		addInfixOperator(booleanT, "boolean", "!=", "boolean");
		addInfixOperator(booleanT, "boolean", "&", "boolean");
		addInfixOperator(booleanT, "boolean", "|", "boolean");
		addInfixOperator(booleanT, "boolean", "^", "boolean");
		addInfixOperator(booleanT, "boolean", "||", "boolean");
		addInfixOperator(booleanT, "boolean", "&&", "boolean");
		addInfixOperator(booleanT, "boolean", "&=", "boolean");
		addInfixOperator(booleanT, "boolean", "|=", "boolean");
		addInfixOperator(booleanT, "boolean", "^=", "boolean");
		addStringConcatenation(booleanT);
		view().storePrimitiveType("boolean",booleanT);
	}


	protected void addUniPromIntegral(Type type) {
		addPrefixOperator(type, getUniProm(type.name()), "~");
		addShift(type, "<<");
		addShift(type, ">>");
		addShift(type, ">>>");
		addUniProm(type);
	}

	protected void addUniProm(Type type) {
		addPrefixOperator(type, getUniProm(type.name()), "-");
		addPrefixOperator(type, getUniProm(type.name()), "+");
		addPrefixOperator(type, type.name(), "--");
		addPrefixOperator(type, type.name(), "++");
		addPostfixOperator(type, type.name(), "--");
		addPostfixOperator(type, type.name(), "++");
	}

	protected void addShift(Type type, String operator) {
		addInfixOperator(type, getUniProm(type.name()), operator, "char");
		addInfixOperator(type, getUniProm(type.name()), operator, "byte");
		addInfixOperator(type, getUniProm(type.name()), operator, "short");
		addInfixOperator(type, getUniProm(type.name()), operator, "int");
		addInfixOperator(type, getUniProm(type.name()), operator, "long");
	}

	protected void addCompoundAssignment(Type type, String operator) {
		addInfixOperator(type, type.name(), operator, "double");
		addInfixOperator(type, type.name(), operator, "float");
		addInfixOperator(type, type.name(), operator, "long");
		addInfixOperator(type, type.name(), operator, "int");
		addInfixOperator(type, type.name(), operator, "short");
		addInfixOperator(type, type.name(), operator, "char");
		addInfixOperator(type, type.name(), operator, "byte");
	}

	protected String getBinProm(String first, String second) {
		if ((first.equals("double")) || (second.equals("double"))) {
			return "double";
		}
		else if ((first.equals("float")) || (second.equals("float"))) { return "float"; }
		if ((first.equals("long")) || (second.equals("long"))) {
			return "long";
		}
		else
			return "int";
	}
	
	protected String getWrapperFQN(String primitiveTypeName) {
		return view().language(Java7.class).boxName(primitiveTypeName);
	}

	protected String getUniProm(String type) {
		if (type.equals("double") || type.equals("float")
				|| type.equals("long")) {
			return type;
		}
		else {
			return "int";
		}
	}
	
	protected void addStringConcatenation(Type type) {
		addInfixOperator(type, "java.lang.String", "+", "java.lang.Object");
	}

	protected void addWrapperOperators(Type type) {
		binaryNumericOperatorNames.forEach(o -> addWrapperOperators(type, o));
	}
	private final static List<String> binaryNumericOperatorNames = new ArrayList<>();
	static {
		binaryNumericOperatorNames.add("+");
		binaryNumericOperatorNames.add("-");
		binaryNumericOperatorNames.add("*");
		binaryNumericOperatorNames.add("/");
		binaryNumericOperatorNames.add("%");
	}
	
	protected void addWrapperOperators(Type type, String operator) {
		numericTypeNames.forEach(n -> {
			addWrapperOperator(type, operator, n);
		});
	}
	
	private final static List<String> numericTypeNames = new ArrayList<>();
	static {
		numericTypeNames.add("char");
		numericTypeNames.add("byte");
		numericTypeNames.add("short");
		numericTypeNames.add("int");
		numericTypeNames.add("long");
		numericTypeNames.add("float");
		numericTypeNames.add("double");
	}
	
	protected void addWrapperOperator(Type type, String operator, String argumentType) {
		addInfixOperator(type, getWrapperFQN(getBinProm(type.name(), argumentType)), operator, getWrapperFQN(argumentType));
	}

	protected void addBinComp(Type type, String operator) {
		addInfixOperator(type, "boolean", operator, "char");
		addInfixOperator(type, "boolean", operator, "byte");
		addInfixOperator(type, "boolean", operator, "short");
		addInfixOperator(type, "boolean", operator, "int");
		addInfixOperator(type, "boolean", operator, "long");
		addInfixOperator(type, "boolean", operator, "float");
		addInfixOperator(type, "boolean", operator, "double");
	}

	protected void addBinProm(Type type, String operator) {
		addInfixOperator(type, getBinProm("double", type.name()), operator,
				"double");
		addInfixOperator(type, getBinProm("float", type.name()), operator,
				"float");
		addBinPromIntegral(type, operator);
	}

	protected void addBinPromIntegral(Type type, String operator) {
		addInfixOperator(type, getBinProm("long", type.name()), operator,
				"long");
		addInfixOperator(type, getBinProm("int", type.name()), operator,
				"int");
		addInfixOperator(type, getBinProm("char", type.name()), operator,
				"char");
		addInfixOperator(type, getBinProm("byte", type.name()), operator,
				"byte");
		addInfixOperator(type, getBinProm("short", type.name()), operator,
				"short");

	}
	protected void addBinNumOpsIntegral(Type type) {
		addBinPromIntegral(type, "&");
		addBinPromIntegral(type, "^");
		addBinPromIntegral(type, "|");
		addBinNumOps(type);
		addCompoundAssignment(type, "<<=");
		addCompoundAssignment(type, ">>=");
		addCompoundAssignment(type, ">>>=");
		addCompoundAssignment(type, "&=");
		addCompoundAssignment(type, "|=");
		addCompoundAssignment(type, "^=");
	}

	protected void addBinNumOps(Type type) {
		addBinProm(type, "+");
		addBinProm(type, "-");
		addBinProm(type, "*");
		addBinProm(type, "/");
		addBinProm(type, "%");
		addBinComp(type, "<");
		addBinComp(type, ">");
		addBinComp(type, "<=");
		addBinComp(type, ">=");
		addBinComp(type, equality());
		addBinComp(type, "!=");
		addCompoundAssignment(type, "*=");
		addCompoundAssignment(type, "/=");
		addCompoundAssignment(type, "%=");
		addCompoundAssignment(type, "+=");
		addCompoundAssignment(type, "-=");
		addPlusString(type);
	}

	protected void addDouble(String mm, DocumentScanner loader) {
		Public pub = new Public();
		Type doubleT = new PrimitiveType("double");
		try {
			new DeclarationLoader(doubleT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}

		doubleT.addModifier(pub);
		doubleT.addModifier(new ValueType());

		addUniProm(doubleT);
		addBinNumOps(doubleT);
		addStringConcatenation(doubleT);
		addWrapperOperators(doubleT);
		view().storePrimitiveType("double",doubleT);
	}

	protected void addLong(String mm, DocumentScanner loader) {
		Public pub = new Public();

		Type longT = new PrimitiveType("long") {
			public boolean assignableTo(Type other) {
				return other.equals(this)
						|| other.getFullyQualifiedName().equals("float")
						|| other.getFullyQualifiedName().equals("double");
			}
		};
		try {
			new DeclarationLoader(longT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		longT.addInheritanceRelation(new SubtypeRelation(language().createTypeReference("float")));
		longT.addModifier(pub);
		longT.addModifier(new ValueType());

		addUniPromIntegral(longT);

		addBinNumOpsIntegral(longT);
		addStringConcatenation(longT);
		addWrapperOperators(longT);
		view().storePrimitiveType("long",longT);
	}

	protected void addFloat(String mm, DocumentScanner loader) {
		Public pub = new Public();

		Type floatT = new PrimitiveType("float") {
			public boolean assignableTo(Type other) {
				return other.equals(this)
						|| other.getFullyQualifiedName().equals("double");
			}
		};
		try {
			new DeclarationLoader(floatT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		floatT.addInheritanceRelation(new SubtypeRelation(language().createTypeReference("double")));
		floatT.addModifier(pub);
		floatT.addModifier(new ValueType());

		addUniProm(floatT);

		addBinNumOps(floatT);
		addStringConcatenation(floatT);
		addWrapperOperators(floatT);
		view().storePrimitiveType("float",floatT);
	}

	protected static class PrimitiveType extends RegularJavaType {
		public PrimitiveType(String name) {
			super(name);
		}

		@Override
		public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
			return Collections.EMPTY_LIST;
		}
		
		@Override
		public Type erasure() {
			return this;
		}

	}

	protected void addInt(String mm, DocumentScanner loader) {
		Public pub = new Public();

		Type intT = new PrimitiveType("int") {
			public boolean assignableTo(Type other) {
				return other.equals(this)
						|| other.getFullyQualifiedName().equals("long")
						|| other.getFullyQualifiedName().equals("float")
						|| other.getFullyQualifiedName().equals("double");
			}
		};
		try {
			new DeclarationLoader(intT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		intT.addInheritanceRelation(new SubtypeRelation(language().createTypeReference("long")));
		intT.addModifier(pub);
		intT.addModifier(new ValueType());

		addUniPromIntegral(intT);

		addBinNumOpsIntegral(intT);
		addStringConcatenation(intT);
		addWrapperOperators(intT);
		view().storePrimitiveType("int",intT);
	}

	protected void addByte(String mm, DocumentScanner loader) {
		Public pub = new Public();
		Type byteT = new PrimitiveType("byte") {
			public boolean assignableTo(Type other) {
				return other.equals(this)
						|| other.getFullyQualifiedName().equals("short")
						|| other.getFullyQualifiedName().equals("char")
						|| other.getFullyQualifiedName().equals("int")
						|| other.getFullyQualifiedName().equals("long")
						|| other.getFullyQualifiedName().equals("float")
						|| other.getFullyQualifiedName().equals("double");
			}
		};
		try {
			new DeclarationLoader(byteT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		byteT.addInheritanceRelation(new SubtypeRelation(language().createTypeReference("short")));
		byteT.addModifier(pub);

		byteT.addModifier(new ValueType());

		addUniPromIntegral(byteT);

		addBinNumOpsIntegral(byteT);
		addStringConcatenation(byteT);
		addWrapperOperators(byteT);
		view().storePrimitiveType("byte",byteT);
	}

	protected void addShort(String mm, DocumentScanner loader) {
		Public pub = new Public();
		Type shortT = new PrimitiveType("short") {
			public boolean assignableTo(Type other) {
				return other.equals(this)
						|| other.getFullyQualifiedName().equals("char")
						|| other.getFullyQualifiedName().equals("int")
						|| other.getFullyQualifiedName().equals("long")
						|| other.getFullyQualifiedName().equals("float")
						|| other.getFullyQualifiedName().equals("double");
			}
		};
		try {
			new DeclarationLoader(shortT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		shortT.addInheritanceRelation(new SubtypeRelation(language().createTypeReference("int")));
		shortT.addModifier(pub);
		shortT.addModifier(new ValueType());


		addUniPromIntegral(shortT);

		addBinNumOpsIntegral(shortT);
		addStringConcatenation(shortT);
		addWrapperOperators(shortT);
		view().storePrimitiveType("short",shortT);
	}

	protected void addChar(String mm, DocumentScanner loader) {
		Public pub = new Public();

		Type charT = new PrimitiveType("char") {
			public boolean assignableTo(Type other) {
				return other.equals(this)
						|| other.getFullyQualifiedName().equals("int")
						|| other.getFullyQualifiedName().equals("long")
						|| other.getFullyQualifiedName().equals("float")
						|| other.getFullyQualifiedName().equals("double");
			}
		};
		try {
			new DeclarationLoader(charT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		charT.addInheritanceRelation(new SubtypeRelation(language().createTypeReference("int")));
		charT.addModifier(pub);
		charT.addModifier(new ValueType());

		addUniPromIntegral(charT);

		addBinNumOpsIntegral(charT);
		addStringConcatenation(charT);
		addWrapperOperators(charT);
		view().storePrimitiveType("char",charT);
	}

	protected void addVoid(String mm, DocumentScanner loader) {
		Public pub = new Public();
		Type voidT = new PrimitiveType("void") {

			public boolean assignableTo(Type other) {
				return false;
			}

		}; // toevoeging gebeurt door de constructor
		try {
			new DeclarationLoader(voidT,mm,view(),loader);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
		voidT.addModifier(pub);

		voidT.addModifier(new ValueType());
		view().storePrimitiveType("void",voidT);
	}

	protected void addPrefixOperator(Type type, String returnType, String symbol) {
		TypeReference tr = ((Java7)language()).createTypeReference(returnType);
		Public pub = new Public();
		PrefixOperator op = new PrefixOperator(new SimpleNameMethodHeader(symbol, tr));
		op.addModifier(pub);
		op.addModifier(new Native());
		type.add(op);
	}

	protected void addPostfixOperator(Type type, String returnType, String symbol) {
		TypeReference tr = ((Java7)language()).createTypeReference(returnType);
		Public pub = new Public();
		PostfixOperator op = new PostfixOperator(new SimpleNameMethodHeader(symbol, tr));
		op.addModifier(pub);
		op.addModifier(new Native());
		type.add(op);
	}

	public void addInfixOperator(Type type, String returnType, String symbol, String argType) {
		TypeReference tr = ((Java7)language()).createTypeReference(returnType);
		Public pub = new Public();
		SimpleNameMethodHeader sig =  new SimpleNameMethodHeader(symbol,tr);
		InfixOperator op = new InfixOperator(sig);
		op.addModifier(pub);

		TypeReference tr2 = ((Java7)language()).createTypeReference(argType);
		FormalParameter fp = new FormalParameter("arg", tr2);
		sig.addFormalParameter(fp);
		op.addModifier(new Native());
		type.add(op);
	}


	protected String equality() {
		return "==";
	}

	protected void addPlusString(Type type) {
		addInfixOperator(type, "String", "+", "String");
	}


}
