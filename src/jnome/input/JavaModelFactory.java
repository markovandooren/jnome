package jnome.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.namespacedeclaration.JavaNamespaceDeclaration;
import jnome.core.type.NullType;
import jnome.core.type.RegularJavaType;
import jnome.input.parser.JavaLexer;
import jnome.input.parser.JavaParser;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.document.Document;
import chameleon.core.element.Element;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ParseException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.member.Member;
import chameleon.oo.method.SimpleNameMethodHeader;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.oo.variable.FormalParameter;
import chameleon.support.input.ChameleonParser;
import chameleon.support.input.ModelFactoryUsingANTLR;
import chameleon.support.member.simplename.operator.infix.InfixOperator;
import chameleon.support.member.simplename.operator.postfix.PostfixOperator;
import chameleon.support.member.simplename.operator.prefix.PrefixOperator;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.ValueType;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSourceImpl;
import chameleon.workspace.ProjectException;
import chameleon.workspace.ProjectLoaderImpl;

/**
 * @author Marko van Dooren
 */

public class JavaModelFactory extends ModelFactoryUsingANTLR {

//	/**
//	 * BE SURE TO CALL INIT() IF YOU USE THIS CONSTRUCTOR.
//	 * 
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	public JavaModelFactory() {
//		Java lang = new Java();
//		setLanguage(lang, ModelFactory.class);
//	}
	
	protected JavaModelFactory(boolean bogus) {
		
	}
	
	/**
	 */
	public JavaModelFactory() {
	}
	
	@Override
	public void initializePredefinedElements() {
		RootNamespace root = language().defaultNamespace();
		SyntheticProjectLoader loader = new SyntheticProjectLoader();
		try {
			language().project().addSource(loader);
		} catch (ProjectException e) {
			// Since it is synthetic, nothing _should_ go wrong here.
			throw new ChameleonProgrammerException(e);
		}
		addPrimitives("",loader);
	  addInfixOperators(root);
	  addNullType(root,loader);
	}

	private void addNullType(RootNamespace root, SyntheticProjectLoader loader) {
    loader.addInputSource(new SyntheticInputSource(new NullType(java()),"",language()));
	}



	public void addPrimitives(String root, SyntheticProjectLoader loader) {
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

	  private Type findType(Namespace defaultNamespace, String fqn) throws LookupException {
	  	Java lang = (Java) defaultNamespace.language();
	  	return lang.findType(fqn);
	  }
	  
	  protected String equality() {
			return "==";
		}
	  
    public void addInfixOperators(Namespace defaultPackage) {
        try {
            Type obj = findType(defaultPackage, "java.lang.Object");
            if (obj != null) {
                addInfixOperator(obj, "boolean", equality(), "Object");
                addInfixOperator(obj, "boolean", "!=", "Object");
                addPlusString(obj);
            }
            Type string = findType(defaultPackage, "java.lang.String");
            if (string != null) {
                addInfixOperator(string, "String", "+", "Object");
                addInfixOperator(string, "String", "+=", "Object");
                addInfixOperator(string, "String", "+", "byte");
                addInfixOperator(string, "String", "+=", "byte");
                addInfixOperator(string, "String", "+", "short");
                addInfixOperator(string, "String", "+=", "short");
                addInfixOperator(string, "String", "+", "char");
                addInfixOperator(string, "String", "+=", "char");
                addInfixOperator(string, "String", "+", "int");
                addInfixOperator(string, "String", "+=", "int");
                addInfixOperator(string, "String", "+", "long");
                addInfixOperator(string, "String", "+=", "long");
                addInfixOperator(string, "String", "+", "float");
                addInfixOperator(string, "String", "+=", "float");
                addInfixOperator(string, "String", "+", "double");
                addInfixOperator(string, "String", "+=", "double");
                addInfixOperator(string, "String", "+", "boolean");
                addInfixOperator(string, "String", "+=", "boolean");
            }

        }
        catch (LookupException e) {
        	// This should only happen if the Java system library was not parsed.
        	e.printStackTrace();
            throw new ChameleonProgrammerException(e);
        }
    }


    public void removeElement(Element element) {
      element.parentLink().connectTo(null);
    }


    @Override
    public ChameleonParser getParser(InputStream inputStream) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(inputStream);
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        parser.setLanguage((ObjectOrientedLanguage) language());
        return parser;
    }

    /**
     * @param parser
     * @throws RecognitionException 
     */
    public void parse(JavaParser parser) throws RecognitionException  {
        parser.compilationUnit();
    }
    
  	protected <P extends Element> Element parse(Element element, String text) throws ParseException {
  		try {
  		  InputStream inputStream = new StringBufferInputStream(text);
  		  Element result = null;
  		  if(element instanceof Member) {
  	  		result = ((JavaParser)getParser(inputStream)).memberDecl().element;
  			}
  			return result;
  		} catch(RecognitionException exc) {
  			throw new ParseException(element.nearestAncestor(Document.class));
  		} catch(IOException exc) {
  			throw new ChameleonProgrammerException("Parsing of a string caused an IOException",exc);
  		}
  	}

//    public static void main(String[] args) {
//        try {
//            long start = Calendar.getInstance().getTimeInMillis();
//            FileSet fileSet = new FileSet();
//            fileSet.include(new PatternPredicate(new File(args[0]),
//                    new FileNamePattern(args[1])));
//            Set files = fileSet.getFiles();
//            ModelFactoryUsingANTLR factory = new JavaModelFactory(files);
//            long stop = Calendar.getInstance().getTimeInMillis();
//            System.out.println("DONE !!!");
//            System.out.println("Acquiring took " + (stop - start) + "ms.");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void addPrefixOperator(Type type, String returnType, String symbol) {
        TypeReference tr = ((Java)language()).createTypeReference(null, returnType);
        Public pub = new Public();
        PrefixOperator op = new PrefixOperator(new SimpleNameMethodHeader(symbol, tr));
        op.addModifier(pub);
        op.addModifier(new Native());
        type.add(op);
    }

    public void addPostfixOperator(Type type, String returnType, String symbol) {
        TypeReference tr = ((Java)language()).createTypeReference(null, returnType);
        Public pub = new Public();
        PostfixOperator op = new PostfixOperator(new SimpleNameMethodHeader(symbol, tr));
        op.addModifier(pub);
        op.addModifier(new Native());
        type.add(op);
    }

    public void addInfixOperator(Type type, String returnType, String symbol, String argType) {
        TypeReference tr = ((Java)language()).createTypeReference(returnType);
        Public pub = new Public();
        SimpleNameMethodHeader sig =  new SimpleNameMethodHeader(symbol,tr);
        InfixOperator op = new InfixOperator(sig);
        op.addModifier(pub);

        TypeReference tr2 = ((Java)language()).createTypeReference(argType);
        FormalParameter fp = new FormalParameter(new SimpleNameSignature("arg"), tr2);
        sig.addFormalParameter(fp);
        op.addModifier(new Native());
        type.add(op);
    }
    
    protected static class SyntheticProjectLoader extends ProjectLoaderImpl {
    	
    }
    
    protected static class SyntheticInputSource extends InputSourceImpl {

    	public SyntheticInputSource(Type type, String namespaceFQN, Language lang) {
    		_type = type;
    		InputSourceNamespace ns = (InputSourceNamespace) lang.defaultNamespace().getOrCreateNamespace(namespaceFQN);
    		setNamespace(ns);
    		NamespaceDeclaration nsd = lang.plugin(ObjectOrientedFactory.class).createNamespaceDeclaration(namespaceFQN);
    		nsd.add(type);
    		Document doc = new Document();
    		doc.add(nsd);
    		setDocument(doc);
    		doc.namespace();
    	}
    	
    	private Type _type;
    	
    	public Type type() {
    		return _type;
    	}
    	
			@Override
			public List<String> targetDeclarationNames(Namespace ns) {
				return Collections.singletonList(type().name());
			}

			@Override
			public List<Declaration> targetDeclarations(String name) throws LookupException {
				Type type = type();
				List<Declaration> result;
				if(type.name().equals(name)) {
					result = new ArrayList<>(1);
					result.add(type);
				} else {
					result = Collections.EMPTY_LIST;
				}
				return result;
			}

			@Override
			protected void doLoad() throws InputException {
				// there is nothing to load, as the element has already been defined.
			}
    	
    }

    public void addVoid(String mm, SyntheticProjectLoader loader) {
        Public pub = new Public();
        Type voidT = new PrimitiveType("void") {

            public boolean assignableTo(Type other) {
                return false;
            }

        }; // toevoeging gebeurt door de constructor
        loader.addInputSource(new SyntheticInputSource(voidT,mm,language()));
        voidT.addModifier(pub);

        voidT.addModifier(new ValueType());
        ((Java)language()).storePrimitiveType("void",voidT);
    }
    
    public Java java() {
    	return (Java) language();
    }

    protected void addByte(String mm, SyntheticProjectLoader loader) {
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
        loader.addInputSource(new SyntheticInputSource(byteT,mm,language()));
        byteT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("short")));
        byteT.addModifier(pub);

        byteT.addModifier(new ValueType());

        addUniPromIntegral(byteT);

        addBinNumOpsIntegral(byteT);
        ((Java)language()).storePrimitiveType("byte",byteT);
    }

    protected void addShort(String mm, SyntheticProjectLoader loader) {
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
        loader.addInputSource(new SyntheticInputSource(shortT,mm,language()));
        shortT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("int")));
        shortT.addModifier(pub);
        shortT.addModifier(new ValueType());


        addUniPromIntegral(shortT);

        addBinNumOpsIntegral(shortT);
        ((Java)language()).storePrimitiveType("short",shortT);
    }

    protected void addChar(String mm, SyntheticProjectLoader loader) {
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
        loader.addInputSource(new SyntheticInputSource(charT,mm,language()));
        charT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("int")));
        charT.addModifier(pub);
        charT.addModifier(new ValueType());

        addUniPromIntegral(charT);

        addBinNumOpsIntegral(charT);
        ((Java)language()).storePrimitiveType("char",charT);
    }

    public void addInt(String mm, SyntheticProjectLoader loader) {
        Public pub = new Public();

        Type intT = new PrimitiveType("int") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("long")
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        loader.addInputSource(new SyntheticInputSource(intT,mm,language()));
        intT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("long")));
        intT.addModifier(pub);
        intT.addModifier(new ValueType());

        addUniPromIntegral(intT);

        addBinNumOpsIntegral(intT);
        ((Java)language()).storePrimitiveType("int",intT);
    }

    public void addLong(String mm, SyntheticProjectLoader loader) {
        Public pub = new Public();

        Type longT = new PrimitiveType("long") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        loader.addInputSource(new SyntheticInputSource(longT,mm,language()));
        longT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("float")));
        longT.addModifier(pub);
        longT.addModifier(new ValueType());

        addUniPromIntegral(longT);

        addBinNumOpsIntegral(longT);
        ((Java)language()).storePrimitiveType("long",longT);
 }

    public void addFloat(String mm, SyntheticProjectLoader loader) {
        Public pub = new Public();

        Type floatT = new PrimitiveType("float") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        loader.addInputSource(new SyntheticInputSource(floatT,mm,language()));
        floatT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("double")));
        floatT.addModifier(pub);
        floatT.addModifier(new ValueType());

        addUniProm(floatT);

        addBinNumOps(floatT);
        ((Java)language()).storePrimitiveType("float",floatT);
    }
    
    private static class PrimitiveType extends RegularJavaType {
    	public PrimitiveType(SimpleNameSignature sig) {
				super(sig);
			}

			public PrimitiveType(String name) {
				super(name);
			}

			@Override
    	public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
    		return Collections.EMPTY_LIST;
    	}
    }

    protected void addDouble(String mm, SyntheticProjectLoader loader) {
        Public pub = new Public();
        Type doubleT = new PrimitiveType("double");
        loader.addInputSource(new SyntheticInputSource(doubleT,mm,language()));
        
        doubleT.addModifier(pub);
        doubleT.addModifier(new ValueType());

        addUniProm(doubleT);
        addBinNumOps(doubleT);
        ((Java)language()).storePrimitiveType("double",doubleT);
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

		protected void addPlusString(Type type) {
			addInfixOperator(type, "String", "+", "String");
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

    protected void addBoolean(String mm, SyntheticProjectLoader loader) {
        Public pub = new Public();
        Type booleanT = new PrimitiveType("boolean");
        booleanT.addModifier(pub);
        loader.addInputSource(new SyntheticInputSource(booleanT,mm,language()));
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
        ((Java)language()).storePrimitiveType("boolean",booleanT);
    }

    public String getBinProm(String first, String second) {
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

    public String getUniProm(String type) {
        if (type.equals("double") || type.equals("float")
                || type.equals("long")) {
            return type;
        }
        else {
            return "int";
        }
    }

//    public Set loadFiles(String path, String extension, boolean recursive){
//        return load(path,extension,recursive);
//    }

//	LOAD FILES

    @Override
		public ModelFactoryUsingANTLR clone() {
			try {
				JavaModelFactory javaModelFactory = new JavaModelFactory();
				javaModelFactory.setDebug(debug());
				return javaModelFactory;
			} catch (Exception e) {
				throw new RuntimeException("Exception while cloning a JavaModelFactory", e);
			}
		}
}
