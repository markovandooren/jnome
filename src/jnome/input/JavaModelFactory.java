package jnome.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;
import jnome.input.parser.JavaLexer;
import jnome.input.parser.JavaParser;
import jnome.output.JavaCodeWriter;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.rejuse.io.fileset.FileNamePattern;
import org.rejuse.io.fileset.FileSet;
import org.rejuse.io.fileset.PatternPredicate;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceReference;
import chameleon.core.namespacepart.DemandImport;
import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.plugin.output.Syntax;
import chameleon.support.input.ChameleonParser;
import chameleon.support.input.ModelFactoryUsingANTLR;
import chameleon.support.member.simplename.SimpleNameMethodHeader;
import chameleon.support.member.simplename.operator.infix.InfixOperator;
import chameleon.support.member.simplename.operator.postfix.PostfixOperator;
import chameleon.support.member.simplename.operator.prefix.PrefixOperator;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.ValueType;

/**
 * @author Marko van Dooren
 */

public class JavaModelFactory extends ModelFactoryUsingANTLR {

	/**
	 * BE SURE TO CALL INIT() IF YOU USE THIS CONSTRUCTOR.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public JavaModelFactory() {
		Java lang = new Java();
		lang.setPlugin(Syntax.class, new JavaCodeWriter());
		setLanguage(lang, ModelFactory.class);
	}
	
	/**
	 * BE SURE TO CALL INIT() IF YOU USE THIS CONSTRUCTOR.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public JavaModelFactory(Java language) throws IOException, ParseException {
		setLanguage(language, ModelFactory.class);
	}
	
	
	/**
	 * Initialize a new Java model factory with the given collection of base classes.
	 * All predefined elements of the language will be initialized. 
	 */
	public JavaModelFactory(Collection<File> base) throws IOException, ParseException {
		this(new Java(), base);
	}
	
	//FIXME: Object and String must be parsed.
	public JavaModelFactory(Java language, Collection<File> base) throws IOException, ParseException {
		setLanguage(language, ModelFactory.class);
		initializeBase(base);
	}
  
	@Override
	public void initializePredefinedElements() {
	  addPrimitives(language().defaultNamespace());
	  addInfixOperators(language().defaultNamespace());
	}


	public void addPrimitives(Namespace defaultPackage) {
        addVoid(defaultPackage);
        addByte(defaultPackage);
        addChar(defaultPackage);
        addShort(defaultPackage);
        addInt(defaultPackage);
        addLong(defaultPackage);
        addFloat(defaultPackage);
        addDouble(defaultPackage);
        addBoolean(defaultPackage);
    }

	  private Type findType(Namespace defaultNamespace, String fqn) throws LookupException {
	  	Java lang = (Java) defaultNamespace.language();
	  	return lang.findType(fqn);
	  }
	
    public void addInfixOperators(Namespace defaultPackage) {
        try {
            Type obj = findType(defaultPackage, "java.lang.Object");
            if (obj != null) {
                addInfixOperator(obj, "boolean", "==", "Object");
                addInfixOperator(obj, "boolean", "!=", "Object");
                addInfixOperator(obj, "String", "+", "String");
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

//    public JavaLexer getLexer(CharStream in) {
//        return new JavaLexer(in);
//    }

//    public JavaWalker getParser(JavaLexer lexer) throws RecognitionException {
//    	CommonTokenStream tokens = new CommonTokenStream(lexer);
//    	JavaParser parser = new JavaParser(tokens);
//    	JavaParser.compilationUnit_return r = parser.compilationUnit();
//    	CommonTree tree = (CommonTree) r.getTree();
//    	CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
//    	nodes.setTokenStream(tokens);
//        return new JavaWalker(nodes);
//    }
//
    // Parse a method
//    public void replaceMethod(ILinkage linkage, String methodCode, Method method) throws IOException, RecognitionException, TokenStreamException, LookupException {
//
//        // make an inputstream of the methodeCode
//        InputStream inputStream = new StringBufferInputStream(methodCode);
//        // Get a parser
//        JavaWalker parser = getParser(inputStream, "", linkage
//                .getParseErrorHandler());
//        // Parse method
//        ((JavaRecognizer) parser).field();
//        // get AST
//
//        ExtendedAST pAST = (ExtendedAST) parser.getAST();
//
//        // make model of method and replace in model
//        getFactory().getNewMethodAcquirer(linkage).acquireNewMethod(null, pAST,
//                method);
//
//        inputStream.close();
//    }

    // Parse object block
//    public void parseTypeMembers(ILinkage linkage, String code, int line, int column, Element typeMember) throws IOException, RecognitionException, TokenStreamException, LookupException {
//
//        // add brackets, necessary for parsing classblock with this parser
//        code = "{\n" + code + "\n}";
//        // System.out.println(code);
//
//        // make an inputstream of the methodeCode
//        InputStream inputStream = new StringBufferInputStream(code);
//
//        // Get a parser
//        LLkParser parser = getParser(inputStream, "", linkage
//                .getParseErrorHandler());
//
//        // Parse object block
//        ((JavaRecognizer) parser).classBlock();
//
//        // get AST
//        ExtendedAST pAST = (ExtendedAST) parser.getAST();
//
//        // pAST.showChildren(0);
//        ;
//        // Adapt line/column numbers in AST to real line/column numbers in
//        // document
//        adaptLineAndColNumbersAST(pAST, line, column, 1);
//
//        // pAST.showChildren(0);
//
//        // Acquire (for testing this acquire - another acquirer can be desired
//        // in other cases)
//        Type Type = (Type) typeMember.getAllParents().get(0);
//
//        getFactory().getObjectBlockAcquirer(linkage).acquire(pAST, Type);
//
//        /*
//           * if(typeMember.getClass() == NonConstructor.class){
//           * Type.removeMethod((NonConstructor)typeMember); }
//           * if(typeMember.getClass() == Constructor.class){
//           * Type.removeConstructor((Constructor)typeMember); }
//           * if(typeMember.getClass() == Type.class){
//           * Type.removeType((Type)typeMember); }
//           */
//        removeElement(typeMember);
//
//        // make model of method and replace in model
//        // getFactory().getNewMethodAcquirer().acquireNewMethod(null,pAST,method);
//
//        inputStream.close();
//    }
//
//    public void parseNameSpaceMembers(ILinkage linkage, String code, int line, int column, Element nameSpaceMember) throws IOException, RecognitionException, TokenStreamException, LookupException {
//
//        // add brackets, necessary for parsing classblock with this parser
//        // code = "{\n"+code+"\n}";
//        // System.out.println(code);
//
//        // make an inputstream of the methodeCode
//        InputStream inputStream = new StringBufferInputStream(code);
//
//        // Get a parser
//        LLkParser parser = getParser(inputStream, "", linkage
//                .getParseErrorHandler());
//
//        // Parse object block
//        ((JavaRecognizer) parser).typeDefinition();
//
//        // get AST
//        ExtendedAST pAST = (ExtendedAST) parser.getAST();
//
//        // pAST.showChildren(0);
//
//        // Adapt line/column numbers in AST to real line/column numbers in
//        // document
//        adaptLineAndColNumbersAST(pAST, line, column, 0);
//
//        ExtendedAST ast = new ExtendedAST();
//        ast.setType(JavaTokenTypes.CLASS_DEF);
//        ast.addChild(pAST);
//
//        // ast.showChildren(0);
//
//        // CompilationUnit cu = (CompilationUnit)
//        // nameSpaceMember.getAllParents().get(0);
//        // getFactory().getCompilationUnitAcquirer(document).acquireObjectTypes(cu,
//        // ast);
//        NamespacePart pp = (NamespacePart) nameSpaceMember.getAllParents().get(
//                0);
//        getFactory().getCompilationUnitAcquirer(linkage).acquireObjectTypes(pp,
//                ast);
//        pp.removeType((Type) nameSpaceMember);
//
//        inputStream.close();
//    }

    public void removeElement(Element element) {
      element.parentLink().connectTo(null);
    }

//    private void adaptLineAndColNumbersAST(ExtendedAST ast, int line, int column, int skipLines) {
//        // IChameleonDocument doc = getDocument(); // werkt niet altijd!!
//        List children = ast.getChildrenLinColNotZero();
//        /*
//           * // line in document where new code starts int startLine = 0; //
//           * column in document where new code starts int startCol = 0; try{
//           * startLine = doc.getLineOfOffset(offset); startCol = offset -
//           * doc.getLineOffset(startLine); }catch(Exception
//           * e){e.printStackTrace();}
//           */
//
//        for (int i = 0; i < children.size(); i++) {
//            ExtendedAST current = (ExtendedAST) children.get(i);
//
//            if (current.getLineNumber() == (1 + skipLines)) {
//                current.setLineNumber(line + 1);
//                current.setColumnNumber(column + 1);
//            }
//            else {
//                current.setLineNumber(current.getLineNumber() + line
//                        - skipLines);
//            }
//        }
//    }

    /*
      * private void adaptLineColNumbers(ExtendedAST ast, int offset){
      * IChameleonDocument doc = getDocument(); List children =
      * ast.getChildrenLinColNotZero();
      *  // line in document where new code starts int startLine = 0; // column
      * in document where new code starts int startCol = 0; try{ startLine =
      * doc.getLineOfOffset(offset); startCol = offset -
      * doc.getLineOffset(startLine); }catch(Exception e){e.printStackTrace();}
      *
      * for(int i=0; i<children.size(); i++){ ExtendedAST current =
      * (ExtendedAST) children.get(i);
      *
      * if(current.getLineNumber()==2){ current.setLineNumber(startLine+1);
      * current.setColumnNumber(startCol+1); } else{
      * current.setLineNumber(current.getLineNumber()+startLine-1); } } }
      */

    /*
      * private IChameleonDocument getDocument(){ IEditorPart editor =
      * ChameleonEditorPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
      * IChameleonDocument document = null; if(editor != null){ document =
      * (IChameleonDocument)((TextEditor)editor).getDocumentProvider().getDocument(editor.getEditorInput()); }
      * return document; }
      */

    public ChameleonParser getParser(InputStream inputStream, String fileName) throws IOException {
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
    
  	protected <P extends Element> Element parse(Element<?,P> element, String text) throws ParseException {
  		try {
  		  InputStream inputStream = new StringBufferInputStream(text);
  		  Element result = null;
  		  if(element instanceof Member) {
  	  		result = ((JavaParser)getParser(inputStream, "document")).memberDecl().element;
  			}
  			return result;
  		} catch(RecognitionException exc) {
  			throw new ParseException(element.nearestAncestor(CompilationUnit.class));
  		} catch(IOException exc) {
  			throw new ChameleonProgrammerException("Parsing of a string caused an IOException",exc);
  		}
  	}

    public static void main(String[] args) {
        try {
            long start = Calendar.getInstance().getTimeInMillis();
            FileSet fileSet = new FileSet();
            fileSet.include(new PatternPredicate(new File(args[0]),
                    new FileNamePattern(args[1])));
            Set files = fileSet.getFiles();
            ModelFactoryUsingANTLR factory = new JavaModelFactory(files);
            long stop = Calendar.getInstance().getTimeInMillis();
            System.out.println("DONE !!!");
            System.out.println("Acquiring took " + (stop - start) + "ms.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPrefixOperator(Type type, String returnType, String symbol) {
        TypeReference tr = ((Java)language()).createTypeReference(null, returnType);
        Public pub = new Public();
        PrefixOperator op = new PrefixOperator(new SimpleNameMethodHeader(symbol), tr);
        op.addModifier(pub);
        op.addModifier(new Native());
        type.add(op);
    }

    public void addPostfixOperator(Type type, String returnType, String symbol) {
        TypeReference tr = ((Java)language()).createTypeReference(null, returnType);
        Public pub = new Public();
        PostfixOperator op = new PostfixOperator(new SimpleNameMethodHeader(symbol), tr);
        op.addModifier(pub);
        op.addModifier(new Native());
        type.add(op);
    }

    public void addInfixOperator(Type type, String returnType, String symbol, String argType) {
        TypeReference tr = ((Java)language()).createTypeReference(returnType);
        Public pub = new Public();
        SimpleNameMethodHeader sig =  new SimpleNameMethodHeader(symbol);
        InfixOperator op = new InfixOperator(sig, tr);
        op.addModifier(pub);

        TypeReference tr2 = ((Java)language()).createTypeReference(argType);
        FormalParameter fp = new FormalParameter(new SimpleNameSignature("arg"), tr2);
        sig.addFormalParameter(fp);
        op.addModifier(new Native());
        type.add(op);
    }

    private NamespacePart getNamespacePart(Namespace pack) {
        // Namespace javaLang = (Namespace)pack.getOrCreatePackage("java.lang");

        NamespacePart pp = new NamespacePart(pack);
        new CompilationUnit(pp);
        pp.addImport(new DemandImport(new NamespaceReference(
                new NamespaceReference("java"), "lang")));
        return pp;
    }

    public void addVoid(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();
        Type clas = new RegularType("void") {

            public boolean assignableTo(Type other) {
                return false;
            }

        }; // toevoeging gebeurt door de constructor
        clas.addModifier(pub);

        cu.add(clas);
        clas.addModifier(new ValueType());
    }
    
    public Java java() {
    	return (Java) language();
    }

    public void addByte(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();
        Type byteT = new RegularType("byte") {
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
        byteT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("short")));
        byteT.addModifier(pub);

        cu.add(byteT);
        byteT.addModifier(new ValueType());

        addUniPromIntegral(byteT);

        addBinNumOpsIntegral(byteT);

    }

    public void addShort(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();
        Type shortT = new RegularType("short") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("char")
                        || other.getFullyQualifiedName().equals("int")
                        || other.getFullyQualifiedName().equals("long")
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        shortT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("int")));
        shortT.addModifier(pub);
        cu.add(shortT);
        shortT.addModifier(new ValueType());


        addUniPromIntegral(shortT);

        addBinNumOpsIntegral(shortT);
    }

    public void addChar(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type charT = new RegularType("char") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("int")
                        || other.getFullyQualifiedName().equals("long")
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        charT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("int")));
        charT.addModifier(pub);
        cu.add(charT);
        charT.addModifier(new ValueType());

        addUniPromIntegral(charT);

        addBinNumOpsIntegral(charT);
    }

    public void addInt(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type intT = new RegularType("int") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("long")
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        intT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("long")));
        intT.addModifier(pub);
        cu.add(intT);
        intT.addModifier(new ValueType());

        addUniPromIntegral(intT);

        addBinNumOpsIntegral(intT);
    }

    public void addLong(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type longT = new RegularType("long") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        longT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("float")));
        longT.addModifier(pub);
        cu.add(longT);
        longT.addModifier(new ValueType());

        addUniPromIntegral(longT);

        addBinNumOpsIntegral(longT);
    }

    public void addFloat(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type floatT = new RegularType("float") {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        floatT.addInheritanceRelation(new SubtypeRelation(java().createTypeReference("double")));
        floatT.addModifier(pub);
        cu.add(floatT);
        floatT.addModifier(new ValueType());

        addUniProm(floatT);

        addBinNumOps(floatT);
    }

    public void addDouble(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type doubleT = new RegularType("double");
        doubleT.addModifier(pub);
        cu.add(doubleT);
        doubleT.addModifier(new ValueType());

        addUniProm(doubleT);
        addBinNumOps(doubleT);
    }

    public void addBinNumOpsIntegral(Type type) {
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

    public void addBinNumOps(Type type) {
        addBinProm(type, "+");
        addBinProm(type, "-");
        addBinProm(type, "*");
        addBinProm(type, "/");
        addBinProm(type, "%");
        addBinComp(type, "<");
        addBinComp(type, ">");
        addBinComp(type, "<=");
        addBinComp(type, ">=");
        addBinComp(type, "==");
        addBinComp(type, "!=");
        addCompoundAssignment(type, "*=");
        addCompoundAssignment(type, "/=");
        addCompoundAssignment(type, "%=");
        addCompoundAssignment(type, "+=");
        addCompoundAssignment(type, "-=");
        addInfixOperator(type, "String", "+", "String");
    }

    public void addBinComp(Type type, String operator) {
        addInfixOperator(type, "boolean", operator, "char");
        addInfixOperator(type, "boolean", operator, "byte");
        addInfixOperator(type, "boolean", operator, "short");
        addInfixOperator(type, "boolean", operator, "int");
        addInfixOperator(type, "boolean", operator, "long");
        addInfixOperator(type, "boolean", operator, "float");
        addInfixOperator(type, "boolean", operator, "double");
    }

    public void addBinProm(Type type, String operator) {
        addInfixOperator(type, getBinProm("double", type.getName()), operator,
                "double");
        addInfixOperator(type, getBinProm("float", type.getName()), operator,
                "float");
        addBinPromIntegral(type, operator);
    }

    public void addBinPromIntegral(Type type, String operator) {
        addInfixOperator(type, getBinProm("long", type.getName()), operator,
                "long");
        addInfixOperator(type, getBinProm("int", type.getName()), operator,
                "int");
        addInfixOperator(type, getBinProm("char", type.getName()), operator,
                "char");
        addInfixOperator(type, getBinProm("byte", type.getName()), operator,
                "byte");
        addInfixOperator(type, getBinProm("short", type.getName()), operator,
                "short");

    }

    public void addUniPromIntegral(Type type) {
        addPrefixOperator(type, getUniProm(type.getName()), "~");
        addShift(type, "<<");
        addShift(type, ">>");
        addShift(type, ">>>");
        addUniProm(type);
    }

    public void addUniProm(Type type) {
        addPrefixOperator(type, getUniProm(type.getName()), "-");
        addPrefixOperator(type, getUniProm(type.getName()), "+");
        addPrefixOperator(type, type.getName(), "--");
        addPrefixOperator(type, type.getName(), "++");
        addPostfixOperator(type, type.getName(), "--");
        addPostfixOperator(type, type.getName(), "++");
    }

    public void addShift(Type type, String operator) {
        addInfixOperator(type, getUniProm(type.getName()), operator, "char");
        addInfixOperator(type, getUniProm(type.getName()), operator, "byte");
        addInfixOperator(type, getUniProm(type.getName()), operator, "short");
        addInfixOperator(type, getUniProm(type.getName()), operator, "int");
        addInfixOperator(type, getUniProm(type.getName()), operator, "long");
    }

    public void addCompoundAssignment(Type type, String operator) {
        addInfixOperator(type, type.getName(), operator, "double");
        addInfixOperator(type, type.getName(), operator, "float");
        addInfixOperator(type, type.getName(), operator, "long");
        addInfixOperator(type, type.getName(), operator, "int");
        addInfixOperator(type, type.getName(), operator, "short");
        addInfixOperator(type, type.getName(), operator, "char");
        addInfixOperator(type, type.getName(), operator, "byte");
    }

    public void addBoolean(Namespace mm) {
        Public pub = new Public();
        Type booleanT = new RegularType("boolean");
        booleanT.addModifier(pub);
        getNamespacePart(mm).add(booleanT);
        addPrefixOperator(booleanT, "boolean", "!");
        addInfixOperator(booleanT, "boolean", "==", "boolean");
        addInfixOperator(booleanT, "boolean", "!=", "boolean");
        addInfixOperator(booleanT, "boolean", "&", "boolean");
        addInfixOperator(booleanT, "boolean", "|", "boolean");
        addInfixOperator(booleanT, "boolean", "^", "boolean");
        addInfixOperator(booleanT, "boolean", "||", "boolean");
        addInfixOperator(booleanT, "boolean", "&&", "boolean");
        addInfixOperator(booleanT, "boolean", "&=", "boolean");
        addInfixOperator(booleanT, "boolean", "|=", "boolean");
        addInfixOperator(booleanT, "boolean", "^=", "boolean");
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
				return new JavaModelFactory();
			} catch (Exception e) {
				throw new RuntimeException("Exception while cloning a JavaModelFactory", e);
			}
		}
}
