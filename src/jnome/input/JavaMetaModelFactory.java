package jnome.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jnome.core.language.Java;
import jnome.input.parser.JavaLexer;
import jnome.input.parser.JavaParser;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.rejuse.io.fileset.FileNamePattern;
import org.rejuse.io.fileset.FileSet;
import org.rejuse.io.fileset.PatternPredicate;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceReference;
import chameleon.core.namespacepart.DemandImport;
import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.variable.FormalParameter;
import chameleon.input.MetaModelFactory;
import chameleon.input.ParseException;
import chameleon.linkage.ILinkage;
import chameleon.linkage.ILinkageFactory;
import chameleon.linkage.IParseErrorHandler;
import chameleon.linkage.ISourceSupplier;
import chameleon.support.member.simplename.SimpleNameMethodHeader;
import chameleon.support.member.simplename.operator.infix.InfixOperator;
import chameleon.support.member.simplename.operator.postfix.PostfixOperator;
import chameleon.support.member.simplename.operator.prefix.PrefixOperator;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.ValueType;
import chameleon.tool.ToolExtension;

/**
 * @author Marko van Dooren
 */

public class JavaMetaModelFactory implements MetaModelFactory {

    /**
     * Return the top of the metamodel when parsing the given set of files.
     *
     * @param files
     *            A set containing all .java files that should be parsed.
     * @pre The given set may not be null | files != null
     * @pre The given set may only contain effective files | for all o in files: |
     *      o instanceof File && | ! ((File)o).isDirectory()
     * @return The result will not be null | result != null
     * @return All given files will be parsed and inserted into the metamodel
     *
     * @throws TokenStreamException
     *             Something went wrong
     * @throws RecognitionException
     *             Something went wrong
     * @throws MalformedURLException
     *             Something went wrong
     * @throws FileNotFoundException
     *             Something went wrong
     * @throws RecognitionException 
     */
    public Namespace getMetaModel(ILinkageFactory fact, Set<File> files) throws MalformedURLException, FileNotFoundException, IOException, LookupException, ParseException {

//		final Namespace defaultPackage = new RootNamespace(null, "",
//				new Java(), new JavaNamespacePartLocalContext());
    	 if(fact ==null) {
    		 throw new IllegalArgumentException("Linkage factory is null");
    	 }
        Java lang = new Java();
        setToolExtensions(lang);
        final Namespace defaultPackage = lang.defaultNamespace();
        int count = 0;
        for (File file : files) {
            System.out.println(++count + " Parsing "+ file.getAbsolutePath());
            try {
              addFileToGraph(fact.createLinkage(file), file, lang);
            } catch (RecognitionException e) {
              throw new ParseException(e);
            }
        }

        addPrimitives(defaultPackage);
        addInfixOperators(defaultPackage);

        return defaultPackage;
    }

    public Namespace getMetaModel(ISourceSupplier supply) throws MalformedURLException, FileNotFoundException, IOException, LookupException, ParseException {

//		final Namespace defaultPackage = new RootNamespace(null, "",
//				new Java(), new JavaNamespacePartLocalContext());
        Java lang = new Java();
        setToolExtensions(lang);
        final Namespace defaultPackage = lang.defaultNamespace();
        try {
            for (supply.reset(); supply.hasNext(); supply.next()) {
                addStringToGraph(supply.getLinkage(), supply.getSource(),
                        lang);

            }
            addPrimitives(defaultPackage);
            addInfixOperators(defaultPackage);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;

        }
        return defaultPackage;
    }

    private Map<Class<? extends ToolExtension>,ToolExtension> toolExtensions = new HashMap<Class<? extends ToolExtension>,ToolExtension>();

    protected void setToolExtensions(Language lang) {
        for (Class<? extends ToolExtension> t : toolExtensions.keySet()) {
            ToolExtension ext = toolExtensions.get(t);
            lang.setToolExtension(t,ext.clone());
        }
    }

    public void addToolExtension(Class<? extends ToolExtension> extClass, ToolExtension ext) {
        toolExtensions.put(extClass,ext);
    }

    public void removeToolExtension(Class<? extends ToolExtension> extClass) {
        toolExtensions.remove(extClass);
    }

    private void addPrimitives(Namespace defaultPackage) {
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

    private void addInfixOperators(Namespace defaultPackage) {
        try {
        	  TypeReference ref = new TypeReference("java.lang.Object");
        	  ref.setUniParent(defaultPackage);
            Type obj = ref.getType();
            if (obj != null) {
                addInfixOperator(obj, "boolean", "==", "Object");
                addInfixOperator(obj, "boolean", "!=", "Object");
                addInfixOperator(obj, "String", "+", "String");
            }
            ref = new TypeReference("java.lang.String");
        	  ref.setUniParent(defaultPackage);
            Type string = ref.getType();
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
            throw new Error(e);
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

    private void addFileToGraph(ILinkage linkage, File file, Java language) throws FileNotFoundException, IOException, MalformedURLException, RecognitionException, LookupException {
        // The file name is used in the lexers and parser to
        // give more informative error messages
        String fileName = file.getName();

        // The constructor throws an FileNotFoundException if for some
        // reason the file can't be read.
        InputStream fileInputStream = new FileInputStream(file);
        
        // This message is printed here because we don't want that this
        // message will be printed if the file doesn't exist
        // LOGGER.debug("Adding " + absolutePath + "...");

        lexAndParse(linkage, fileInputStream, fileName, language, linkage
                .getParseErrorHandler());
    }

    public void addSource(ILinkage linkage, String source, Java language) throws MalformedURLException, ParseException, IOException, LookupException {
        try {
            addStringToGraph(linkage, source, language);
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException(
                    "Parent element for compilation unit must be package");
        } catch (RecognitionException e) {
          throw new ParseException(e);
        }
    }

    private void addStringToGraph(ILinkage linkage, String string, Java language) throws IOException, MalformedURLException, RecognitionException, LookupException {
        String name = "document";
        InputStream inputStream = new StringBufferInputStream(string);
        
        lexAndParse(linkage, inputStream, name, language, linkage
                .getParseErrorHandler());
        // System.out.println("metamodel made of " + name);
    }

    private JavaParser getParser(InputStream inputStream, String fileName, IParseErrorHandler handler, Java language) throws RecognitionException, IOException {
//        JavaLexer lexer = getLexer(inputStream);
        
//    		File file = new File(fileName);
//    		System.out.println("getParser receives:" + file.getAbsolutePath());
    	
//        InputStream fileInputStream = new FileInputStream(file);
        ANTLRInputStream input = new ANTLRInputStream(inputStream);
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        parser.setLanguage(language);
        
        
        // Tell the lexer the name of the file he is lexing
        // this name is used to generate more informative error messages

        //lexer.setFilename(fileName);

        // For ChameleonEditor
        //@FIXME restore tab size.
        //lexer.setTabSize(1);

        // Create a parser
        // The parser reads from the selector
        //@FIXME restore use of error handler
//        JavaWalker parser = getParser(lexer);//, handler);

        // Tell the parser the filename for more informative error messages
        //parser.setFilename(fileName);

        // Tell the parser to make instances of the ExtendedAST class
        // to build de AST.
        //parser.getASTFactory().setASTNodeClass(ExtendedAST.class);

        return parser;
    }

    private void lexAndParse(ILinkage linkage, InputStream inputStream, String fileName, Java language, IParseErrorHandler handler) throws IOException, MalformedURLException, RecognitionException, LookupException {

        JavaParser parser = getParser(inputStream, fileName, handler, language);
        parser.setLanguage(language);

        // Parse the compilationUnit
        // A compilation unit in java is a file, so we parse the complete
        // file here.
        // While parsing the compilation unit, the parser builds an AST
        // containing a structured representation of all the java
        // elements in the current file.
        // Throw a RecognitionException if the file can't be
        // recognized, this shouldn't happen if the file is compileable
        // Throws a TokenStreamException if there goes something wrong
        // with generating tokens. This shouldn't happen.
        parse(parser);

        
        // Get the AST the parser produced
//        ExtendedASDeT pAST = (ExtendedAST) parser.getAST();
        // showtree(fileName, parser.getAST(), parser.getTokenNames());
        // close the inputstream
//        inputStream.close();
        // Are there java elements found?
//        if (pAST != null) {
//            // Give a root to pAST
//            // pAST can have siblings: packagedefinition,
//            // all importstatements and ObjectTypes are all siblings
//            // at the toplevel
//            // Create a new ExtendedAST
//            ExtendedAST rootAST = new ExtendedAST();
//            // Set the text of the root
//            rootAST.setText("<ROOTAST>");
//            // Put everything under root so that it really becomes root
//            rootAST.setFirstChild(pAST);
//            // System.out.println("lengte AST : "+rootAST.getLengthChildren());
//            // System.out.println(rootAST.toStringTree());
//
//            // Let the graphRoot acquire the AST, hereby extending
//            // the structure of java elements already build
//            // try {
//            CompilationUnitAcquirer acquirer = getFactory()
//                    .getCompilationUnitAcquirer(linkage);
//            acquirer.acquire(defaultPackage, rootAST);
//            // project.add(cu);
//            // }
//            // catch (InvalidURL e) {
//            // // shouldn't happen
//            // throw new ShouldntHappenException(e);
//            // }
//        }
//        else {
//            // LOGGER.warn(
//            // "No java elements found in file " +
//            // absolutePath + ". Empty file?"
//            // );
//        }
    }

    // private final static Logger LOGGER =
    // Logger.getLogger("seaster.metamodelfactory");

    // static {
    // PropertyConfigurator.configure("logging.properties");
    // }

//    public Factory getFactory() {
//        return new Factory();
//    }

    /**
     * @param parser
     * @throws RecognitionException 
     */
    protected void parse(JavaParser parser) throws RecognitionException  {
        parser.compilationUnit();
    }
    
    static class DummyLinkageFactory implements ILinkageFactory{

    	public ILinkage createLinkage(File file) {
    		return new ILinkage(){

    			public IParseErrorHandler getParseErrorHandler() {
    				return null;
    			}

    			public String getSource() {
    				return null;
    			}

    			public void decoratePosition(int offset, int length, String dectype, Element el) {
    			}

    			public int getLineOffset(int i) {
    				return 0;
    			}

    			public void addCompilationUnit(CompilationUnit cu) {
    				
    			}};
    	}}


    public static void main(String[] args) {
        try {
            long start = Calendar.getInstance().getTimeInMillis();
            FileSet fileSet = new FileSet();
            fileSet.include(new PatternPredicate(new File(args[0]),
                    new FileNamePattern(args[1])));
            Set files = fileSet.getFiles();
            new JavaMetaModelFactory().getMetaModel(new DummyLinkageFactory(), files);
            long stop = Calendar.getInstance().getTimeInMillis();
            System.out.println("DONE !!!");
            System.out.println("Acquiring took " + (stop - start) + "ms.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPrefixOperator(Type type, String returnType, String symbol) {
        TypeReference tr = new TypeReference(null, returnType);
        Public pub = new Public();
        PrefixOperator op = new PrefixOperator(new SimpleNameMethodHeader(symbol), tr);
        op.addModifier(pub);
        op.addModifier(new Native());
        type.add(op);
    }

    public void addPostfixOperator(Type type, String returnType, String symbol) {
        TypeReference tr = new TypeReference(null, returnType);
        Public pub = new Public();
        PostfixOperator op = new PostfixOperator(new SimpleNameMethodHeader(symbol), tr);
        op.addModifier(pub);
        op.addModifier(new Native());
        type.add(op);
    }

    public void addInfixOperator(Type type, String returnType, String symbol, String argType) {
        TypeReference tr = new TypeReference(null, returnType);
        Public pub = new Public();
        SimpleNameMethodHeader sig =  new SimpleNameMethodHeader(symbol);
        InfixOperator op = new InfixOperator(sig, tr);
        op.addModifier(pub);

        TypeReference tr2 = new TypeReference(argType);
        FormalParameter fp = new FormalParameter(new SimpleNameSignature("arg"), tr2);
        sig.addParameter(fp);
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
        Type clas = new RegularType(new SimpleNameSignature("void")) {

            public boolean assignableTo(Type other) {
                return false;
            }

        }; // toevoeging gebeurt door de constructor
        clas.addModifier(pub);

        cu.addType(clas);
        clas.addModifier(new ValueType());
    }

    public void addByte(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();
        Type byteT = new RegularType(new SimpleNameSignature("byte")) {
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
        byteT.addModifier(pub);

        cu.addType(byteT);
        byteT.addModifier(new ValueType());

        addUniPromIntegral(byteT);

        addBinNumOpsIntegral(byteT);

    }

    public void addShort(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();
        Type shortT = new RegularType(new SimpleNameSignature("short")) {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("char")
                        || other.getFullyQualifiedName().equals("int")
                        || other.getFullyQualifiedName().equals("long")
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        shortT.addModifier(pub);
        cu.addType(shortT);
        shortT.addModifier(new ValueType());


        addUniPromIntegral(shortT);

        addBinNumOpsIntegral(shortT);
    }

    public void addChar(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type charT = new RegularType(new SimpleNameSignature("char")) {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("int")
                        || other.getFullyQualifiedName().equals("long")
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        charT.addModifier(pub);
        cu.addType(charT);
        charT.addModifier(new ValueType());

        addUniPromIntegral(charT);

        addBinNumOpsIntegral(charT);
    }

    public void addInt(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type intT = new RegularType(new SimpleNameSignature("int")) {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("long")
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        intT.addModifier(pub);
        cu.addType(intT);
        intT.addModifier(new ValueType());

        addUniPromIntegral(intT);

        addBinNumOpsIntegral(intT);
    }

    public void addLong(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type longT = new RegularType(new SimpleNameSignature("long")) {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("float")
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        longT.addModifier(pub);
        cu.addType(longT);
        longT.addModifier(new ValueType());

        addUniPromIntegral(longT);

        addBinNumOpsIntegral(longT);
    }

    public void addFloat(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type floatT = new RegularType(new SimpleNameSignature("float")) {
            public boolean assignableTo(Type other) {
                return other.equals(this)
                        || other.getFullyQualifiedName().equals("double");
            }
        };
        floatT.addModifier(pub);
        cu.addType(floatT);
        floatT.addModifier(new ValueType());

        addUniProm(floatT);

        addBinNumOps(floatT);
    }

    public void addDouble(Namespace mm) {
        NamespacePart cu = getNamespacePart(mm);
        Public pub = new Public();

        Type doubleT = new RegularType(new SimpleNameSignature("double"));
        doubleT.addModifier(pub);
        cu.addType(doubleT);
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
        Type booleanT = new RegularType(new SimpleNameSignature("boolean"));
        booleanT.addModifier(pub);
        getNamespacePart(mm).addType(booleanT);
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

    public Set loadFiles(String path, String extension, boolean recursive){
        return load(path,extension,recursive);
    }

//	LOAD FILES
    /**
     * Load all the files in the last directory of the given path with the given extention
     * @param path			The directory to from where to load the cs-files
     * @param extension     Only files with this extension will be loaded
     * @param recursive	    Wether or not to also load cs-files from all sub directories
     * @return A set with all the cs-files in the given path
     */
    public static Set load(String path, String extension, boolean recursive){
        Set result = new HashSet();
        File f = new File(path);
        //System.out.println("Scanning source file in "+f.getAbsolutePath());
        if(f.isDirectory()){
            File [] files = f.listFiles();

            for(int i=0; i < files.length; i++){
                if(recursive){
                    result.addAll(load(files[i].getPath(), extension, recursive));
                }else{
                    if(files[i].getName().endsWith(extension)){
                        result.add(files[i]);
                    }
                }
            }
        }else{
            if(f.getName().endsWith(extension)){
                result.add(f);
            }
        }
        return result;
    }

    /**
     * Load all the cs-files in the last directory of each path in the list
     * @param pathList		The directories to from where to load the cs-files
     * @param extension     Only files with this extension will be loaded
     * @param recursive	    Wether or not to also load cs-files from all sub directories
     * @return A set with all the cs-files in the given path
     */
    public static Set loadFiles(List pathList, String extension, boolean recursive){
        Set result = new HashSet();

        Iterator it = pathList.iterator();
        while(it.hasNext()){
            String path = it.next().toString();
            result.addAll(load(path, extension, recursive));
        }
        return result;
    }
}
