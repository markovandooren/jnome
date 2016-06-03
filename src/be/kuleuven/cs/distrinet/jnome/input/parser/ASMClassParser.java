package be.kuleuven.cs.distrinet.jnome.input.parser;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_STRICT;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.aikodi.chameleon.core.declaration.SimpleNameSignature;
import org.aikodi.chameleon.core.document.Document;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.modifier.Modifier;
import org.aikodi.chameleon.core.namespace.LazyRootNamespace;
import org.aikodi.chameleon.core.namespace.RootNamespaceReference;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.method.NativeImplementation;
import org.aikodi.chameleon.oo.method.exception.ExceptionClause;
import org.aikodi.chameleon.oo.method.exception.TypeExceptionDeclaration;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeArgumentWithTypeReference;
import org.aikodi.chameleon.oo.type.generics.ExtendsConstraint;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.oo.variable.VariableDeclaration;
import org.aikodi.chameleon.plugin.output.Syntax;
import org.aikodi.chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import org.aikodi.chameleon.support.modifier.Abstract;
import org.aikodi.chameleon.support.modifier.Enum;
import org.aikodi.chameleon.support.modifier.Final;
import org.aikodi.chameleon.support.modifier.Interface;
import org.aikodi.chameleon.support.modifier.Native;
import org.aikodi.chameleon.support.modifier.Private;
import org.aikodi.chameleon.support.modifier.Protected;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.chameleon.support.modifier.Static;
import org.aikodi.chameleon.util.Pair;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7LanguageFactory;
import be.kuleuven.cs.distrinet.jnome.core.modifier.StrictFP;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Synchronized;
import be.kuleuven.cs.distrinet.jnome.core.modifier.Volatile;
import be.kuleuven.cs.distrinet.jnome.core.namespacedeclaration.JavaNamespaceDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaEqualityTypeArgument;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaExtendsWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaPureWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaSuperWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.variable.JavaVariableDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.variable.MultiFormalParameter;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.association.SingleAssociation;

public class ASMClassParser {

  public ASMClassParser(ZipFile file, ZipEntry entry, String className, String packageFQN) {
    if(className == null) {
      throw new ChameleonProgrammerException();
    }
    _jarFile = file;
    _entry = entry;
    _name = className;
    _packageFQN = packageFQN;
  }

  private String _packageFQN;

  public String packageFQN() {
    return _packageFQN;
  }

  private String _name;

  public String name() {
    return _name;
  }

  public void add(ASMClassParser child, String qn) {
    if(_children == null) {
      _children = new HashMap<String,ASMClassParser>();
    }
    String next = Util.getAllButFirstPart(qn);
    if(next == null) {
      _children.put(qn,child);
    } else {
      ASMClassParser c = _children.get(Util.getFirstPart(qn));
      if(c != null) {
        c.add(child, next);
      }
    }
  }

  /**
   * A map that keeps track of the names of the inner classes, and the
   * files that they read.
   */
  private Map<String,ASMClassParser> _children;

  private ZipFile _jarFile;

  private ZipEntry _entry;

  public Document load(Java7 language) throws FileNotFoundException, IOException, LookupException {
    Type t = read(language);
    Document doc = new Document();

    NamespaceDeclaration decl;
    if(packageFQN() != null) {
      decl = new JavaNamespaceDeclaration(packageFQN());
    } else {
      decl = new JavaNamespaceDeclaration(new RootNamespaceReference());
    }
    doc.add(decl);
    decl.add(t);
    return doc;
  }

  protected Type read(Java7 language) throws FileNotFoundException, IOException {
  	
    InputStream inputStream = new BufferedInputStream(_jarFile.getInputStream(_entry));
    ClassReader reader = new ClassReader(inputStream);
    ClassExtractor extractor = new ClassExtractor(language);
    reader.accept(extractor, Opcodes.ASM4);
    inputStream.close();
    Type result = extractor.type();
    if(_children != null) {
      for(ASMClassParser child: _children.values()) {
        result.add(child.read(language));
      }
    }
    return result;
  }

  protected JavaTypeReference toRef(String tref, Java7 language) {
    return language.createTypeReference(toDots(tref));
  }

  private String toDots(String name) {
    return name.replace('/', '.').replace('$', '.');
  }

  protected ObjectOrientedFactory factory(Language language) {
    return language.plugin(ObjectOrientedFactory.class);
  }

  protected class ClassExtractor extends ClassVisitor {

    private Java7 _language;

    private Type _type;

    public Type type() {
      return _type;
    }

    public ClassExtractor(Java7 language) {
      super(Opcodes.ASM4);
      _language = language;
      initMethodAccessMap();
      initClassAccessMap();
      initFieldAccessMap();
    }

    /**
     * Visit a class
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      if(! isSynthetic(access)) {
        String n = Util.getLastPart(toDots(name));
        Type type = factory(_language).createRegularType(n);
        // OK, so ASM only creates a signature when there are generic parameters.
        // otherwise you only get superName and interfaces (you'll get them as well with generics though).
        // What a bad design. You're forcing me to write crap code.
        if(signature != null) {
          new SignatureReader(signature).accept(new ClassSignatureExtractor(type,language()));
        } else {
          // Object has null as its super name
          if(superName != null) {
            TypeReference zuppaKlass = toRef(superName,_language);
            type.addInheritanceRelation(new SubtypeRelation(zuppaKlass));
          }
          if(interfaces != null) {
            for(String iface: interfaces) {
              type.addInheritanceRelation(new SubtypeRelation(toRef(iface,_language)));
            }
          }
        }
        type.addModifiers(accessToClassModifier(access));
        _type = type;
      }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
      if(! isSynthetic(access)) {
        MemberVariableDeclarator decl = new MemberVariableDeclarator();
        decl.addModifiers(accessToFieldModifier(access));
        VariableDeclaration declaration = new JavaVariableDeclaration(name);
        decl.add(declaration);
        if(signature != null) {
          new SignatureReader(signature).accept(new FieldSignatureExtractor(decl,language()));
        } else {
          new SignatureReader(desc).accept(new FieldSignatureExtractor(decl,language()));
        }
        _type.add(decl);
      }
      return null;
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
      if(! isSynthetic(access)) {

      }
    }

    public Java7 language() {
      return _language;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      if(! isSynthetic(access)) {
        Method m = factory(_language).createNormalMethod(name,null);
        boolean constructor = false;
        if(name.equals("<init>")) {
          name = _type.name();
          m.setName(name);
          factory(_language).transformToConstructor(m);
          m.setReturnTypeReference(_language.createTypeReference(name));
          constructor = true;
        }
        _type.add(m);
        List<Modifier> mods = accessToMethodModifier(access);
        m.addModifiers(mods);
        m.setImplementation(new NativeImplementation());
        MethodExtractor extractor;
        if(constructor) {
          extractor = new ConstructorExtractor(m,language());
        } else {
          extractor = new MethodExtractor(m,language()); 
        }
        if(signature != null) {
          new SignatureReader(signature).accept(extractor);
        } else {
          new SignatureReader(desc).accept(extractor);
        }
        if(isVarargs(access)) {
          FormalParameter param = m.lastFormalParameter();
          MultiFormalParameter multi = MultiFormalParameter.createUnsafe(param.name(), (JavaTypeReference)param.getTypeReference());
          SingleAssociation<Element, Element> parentLink = param.parentLink();
          parentLink.getOtherRelation().replace(parentLink, multi.parentLink());
        }

        ExceptionClause clause = new ExceptionClause();
        m.setExceptionClause(clause);
        if(exceptions != null) {
          for(String ex:exceptions) {
            clause.add(new TypeExceptionDeclaration(toRef(ex,_language)));
          }
        }
      }
      return null;
    }

    protected boolean isSynthetic(int access) {
      return (access & ACC_SYNTHETIC) != 0;
    }

    protected List<Modifier> accessToFieldModifier(int access) {
      List<Modifier> result = new ArrayList<Modifier>();
      for(Integer i: _fieldAccessList) {
        if((access & i) != 0) {
          result.add(Util.clone(_fieldAccessMap.get(i)));
        }
      }
      return result;
    }

    private void initFieldAccessMap() {
      _fieldAccessMap = new HashMap<Integer, Modifier>();
      _fieldAccessMap.put(ACC_PUBLIC, new Public());
      _fieldAccessMap.put(ACC_PRIVATE, new Private());
      _fieldAccessMap.put(ACC_PROTECTED, new Protected());
      _fieldAccessMap.put(ACC_STATIC, new Static());
      _fieldAccessMap.put(ACC_FINAL, new Final());
      _fieldAccessMap.put(Opcodes.ACC_VOLATILE, new Volatile());
      _classAccessMap.put(Opcodes.ACC_ENUM, new Enum());
      _fieldAccessList = new ArrayList<Integer>(_fieldAccessMap.keySet());
    }

    private Map<Integer, Modifier> _fieldAccessMap;

    private List<Integer> _fieldAccessList;

    protected List<Modifier> accessToMethodModifier(int access) {
      List<Modifier> result = new ArrayList<Modifier>();
      for(Integer i: _methodAccessList) {
        if((access & i) != 0) {
          result.add(Util.clone(_methodAccessMap.get(i)));
        }
      }
      return result;
    }

    protected boolean isVarargs(int access) {
      return ((access & Opcodes.ACC_VARARGS) != 0);
    }

    private void initMethodAccessMap() {
      _methodAccessMap = new HashMap<Integer, Modifier>();
      _methodAccessMap.put(ACC_PUBLIC, new Public());
      _methodAccessMap.put(ACC_PRIVATE, new Private());
      _methodAccessMap.put(ACC_PROTECTED, new Protected());
      _methodAccessMap.put(ACC_STATIC, new Static());
      _methodAccessMap.put(Opcodes.ACC_SYNCHRONIZED, new Synchronized());
      _methodAccessMap.put(ACC_FINAL, new Final());
      _methodAccessMap.put(Opcodes.ACC_NATIVE, new Native());
      _methodAccessMap.put(ACC_STRICT, new StrictFP());
      _methodAccessMap.put(ACC_INTERFACE, new Interface());
      _methodAccessMap.put(ACC_ABSTRACT, new Abstract());
      _methodAccessList = new ArrayList<Integer>(_methodAccessMap.keySet());
    }

    private Map<Integer, Modifier> _methodAccessMap;

    private List<Integer> _methodAccessList;

    protected List<Modifier> accessToClassModifier(int access) {
      List<Modifier> result = new ArrayList<Modifier>();
      for(Integer i: _classAccessList) {
        if((access & i) != 0) {
          Modifier modifier = _classAccessMap.get(i);
          if(modifier != null) {
            result.add(Util.clone(modifier));
          }
        }
      }
      return result;
    }

    private void initClassAccessMap() {
      _classAccessMap = new HashMap<Integer, Modifier>();
      _classAccessMap.put(ACC_PUBLIC, new Public());
      _classAccessMap.put(ACC_PRIVATE, new Private());
      _classAccessMap.put(ACC_PROTECTED, new Protected());
      _classAccessMap.put(ACC_FINAL, new Final());
      _classAccessMap.put(ACC_STATIC, new Static()); // not according to Opcodes, but I think that is wrong
      _classAccessMap.put(ACC_STRICT, new StrictFP()); // not according to Opcodes, but I think that is wrong
      _classAccessMap.put(ACC_SUPER, null); // What is this?
      _classAccessMap.put(ACC_INTERFACE, new Interface());
      _classAccessMap.put(ACC_ABSTRACT, new Abstract());
      _classAccessMap.put(Opcodes.ACC_ENUM, new Enum());
      _classAccessList = new ArrayList<Integer>(_classAccessMap.keySet());
    }

    private Map<Integer, Modifier> _classAccessMap;

    private List<Integer> _classAccessList;
  }

  protected class FieldSignatureExtractor extends TypeReferenceExtractor {

    public FieldSignatureExtractor(MemberVariableDeclarator var, Java7 language) {
      super(language);
      _var = var;
    }

    private MemberVariableDeclarator _var;

    @Override
    protected void connect(TypeReference tref) {
      _var.setTypeReference(tref);
    }
  }



  protected class MethodExtractor extends SignatureVisitor {

    public MethodExtractor(Method method, Java7 language) {
      super(Opcodes.ASM4);
      _method = method;
      _language = language;
    }

    public Java7 language() {
      return _language;
    }

    protected Java7 _language;

    private Method _method;

    private int _nbArgs = 0;

    private FormalTypeParameter _currentTypeParameter;

    @Override
    public void visitFormalTypeParameter(String name) {
      _currentTypeParameter = new FormalTypeParameter(name);
      _method.header().addTypeParameter(_currentTypeParameter);
    }

    @Override
    public SignatureVisitor visitClassBound() {
      return new TypeParameterBoundExtractor(_currentTypeParameter,language());
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
      return new TypeParameterBoundExtractor(_currentTypeParameter,language());
    }

    @Override
    public SignatureVisitor visitParameterType() {
      return new TypeReferenceExtractor(language()) {
        @Override
        protected void connect(TypeReference tref) {
          String name = "arg" + _nbArgs++;
          _method.header().addFormalParameter(new FormalParameter(name, tref));
        }
      };
    }

    @Override
    public SignatureVisitor visitReturnType() {
      return new TypeReferenceExtractor(_language) {
        @Override
        protected void connect(TypeReference tref) {
          _method.setReturnTypeReference(tref);
        }
      };
    }

  }

  private class ConstructorExtractor extends MethodExtractor {

    public ConstructorExtractor(Method method, Java7 language) {
      super(method,language);
    }

    @Override
    public SignatureVisitor visitReturnType() {
      // for a constructor, the return type of the method is already set.
      return new TypeReferenceExtractor(_language);
    }
  }

  private class TypeParameterBoundExtractor extends TypeReferenceExtractor {

    private FormalTypeParameter _param;

    public TypeParameterBoundExtractor(FormalTypeParameter param, Java7 language) {
      super(language);
      _param = param;
    }

    @Override
    protected void connect(TypeReference tref) {
      if(_param != null) {
        _param.addConstraint(new ExtendsConstraint(tref));
      }
    }
  }

  protected class ClassSignatureExtractor extends SignatureVisitor {

    private Java7 _language;

    public Java7 language() {
      return _language;
    }

    private class InheritanceExtractor extends TypeReferenceExtractor {
      private InheritanceExtractor(Java7 language) {
        super(language);
      }

      @Override
      protected void connect(TypeReference tref) {
        _type.addInheritanceRelation(new SubtypeRelation(tref));
      }
    }

    public ClassSignatureExtractor(Type type, Java7 language) {
      super(Opcodes.ASM4);
      _type = type;
      _language = language;
    }

    private Type _type;

    @Override
    public SignatureVisitor visitSuperclass() {
      return new InheritanceExtractor(language());
    }

    @Override
    public SignatureVisitor visitInterface() {
      return new InheritanceExtractor(language());
    }

    @Override
    public void visitFormalTypeParameter(String name) {
      _param = new FormalTypeParameter(name);
      _type.addParameter(TypeParameter.class, _param);
    }

    private FormalTypeParameter _param;

    @Override
    public SignatureVisitor visitClassBound() {
      return new TypeParameterBoundExtractor(_param,language());
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
      return new TypeParameterBoundExtractor(_param,language());
    }
  }

  protected class TypeReferenceExtractor extends SignatureVisitor {

    public TypeReferenceExtractor(Java7 language) {
      super(Opcodes.ASM4);
      _language = language;
      initPrimitiveMap();
    }

    private Java7 _language;

    public Java7 language() {
      return _language;
    }
    
    /**
     * For some reason, inner classes are presented via a different method.
     * For now, we simply discard the existing type reference since
     * for the following code:
     * <code>
     * class A extends B {
     *   class C extends X&lt;C&gt;
     * }
     * </code> 
     * 
     * the super class of C is presented as B.X&lt;C&gt;.
     * {@inheritDoc}
     */
    @Override
    public void visitInnerClassType(String name) {
      _tref = toRef(name,_language);
      connect(_tref);
    }
    
    private void initPrimitiveMap() {
      _primitiveMap = new HashMap<Character, String>();
      _primitiveMap.put('C', "char");
      _primitiveMap.put('V', "void");
      _primitiveMap.put('Z', "boolean");
      _primitiveMap.put('B', "byte");
      _primitiveMap.put('S', "short");
      _primitiveMap.put('I', "int");
      _primitiveMap.put('F', "float");
      _primitiveMap.put('J', "long");
      _primitiveMap.put('D', "double");
    }

    private String getPrimitiveTypeName(char c) {
      return _primitiveMap.get(c);
    }

    private JavaTypeReference _tref;

    public JavaTypeReference typeReference() {
      return _tref;
    }

    private Map<Character, String> _primitiveMap;

    @Override
    public void visitBaseType(char t) {
      _tref = _language.createTypeReference(getPrimitiveTypeName(t));
      connect(_tref);
    }

    @Override
    public void visitTypeVariable(String name) {
      _tref = _language.createTypeReference(name);
      connect(_tref);
    }

    @Override
    public void visitClassType(String fqn) {
      _tref = toRef(fqn,_language);
      connect(_tref);
    }

    protected void connect(TypeReference tref) {
      // do nothing by default;
    }

    @Override
    public SignatureVisitor visitArrayType() {
      return new TypeReferenceExtractor(language()) {
        @Override
        protected void connect(TypeReference tref) {
          _tref = new ArrayTypeReference((JavaTypeReference) tref);
          TypeReferenceExtractor.this.connect(_tref);
        }
      };
    }

    @Override
    public void visitTypeArgument() {
      ((BasicJavaTypeReference)typeReference()).addArgument(new JavaPureWildcard());
    }

    @Override
    public SignatureVisitor visitTypeArgument(char kind) {
      // create visitor with 'this' as its parent.
      final TypeArgumentWithTypeReference arg;
      if(kind == SignatureVisitor.INSTANCEOF) {
        arg = _language.createEqualityTypeArgument(null);
      } else if(kind == SignatureVisitor.EXTENDS) {
        arg = _language.createExtendsWildcard(null);
      } else if(kind == SignatureVisitor.SUPER) {
        arg = _language.createSuperWildcard(null);
      } else {
        throw new ChameleonProgrammerException();
      }
      ((BasicJavaTypeReference)typeReference()).addArgument(arg);
      return new TypeReferenceExtractor(language()) {
        /**
         * If there is no bound, we replace the type argument with a pure wildcard.
         * I GUESS that ? is modeled as an EXTENDS bound without a type reference.
         */
        //FIXME Or is this method no invoked for ??
        @Override
        public void visitEnd() {
          if(this.typeReference() == null) {
            SingleAssociation parentLink = arg.parentLink();
            parentLink.getOtherRelation().replace(parentLink, new JavaPureWildcard().parentLink());
          }
        }
        protected void connect(TypeReference tref) {
          arg.setTypeReference(tref);
        }
      };
    }

  }


  public static void main(String[] args) throws IOException, Exception {
    String jarPath = args[0];
    JarFile jar = new JarFile(jarPath);
    Java7 lang = new Java7LanguageFactory().create();
    Project project = new Project("test", new File("."), new JavaView(new LazyRootNamespace(), lang));


    Enumeration<JarEntry> entries = jar.entries();
    List<Pair<Pair<String,String>, JarEntry>> names = new ArrayList<Pair<Pair<String,String>, JarEntry>>();
    while(entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      String name = entry.getName();
      if(name.endsWith(".class")) {
        String tmp = Util.getAllButLastPart(name).replace('/', '.').replace('$', '.');
        if(! tmp.matches(".*\\.[0-9].*")) {
          names.add(new Pair<Pair<String,String>, JarEntry>(new Pair<String,String>(tmp,Util.getLastPart(Util.getAllButLastPart(name).replace('/', '.')).replace('$', '.')), entry));
        }
      }
    }
    Collections.sort(names, new Comparator<Pair<Pair<String,String>,JarEntry>>(){
      @Override
      public int compare(Pair<Pair<String,String>, JarEntry> o1, Pair<Pair<String,String>, JarEntry> o2) {
        int first = o1.first().first().length();
        int second = o2.first().first().length();
        return first - second;
      }
    });
    List<ASMClassParser> parsers = new ArrayList<ASMClassParser>();
    Map<String, ASMClassParser> map = new HashMap<String, ASMClassParser>();
    for(Pair<Pair<String,String>,JarEntry> pair: names) {
      JarEntry entry = pair.second();
      String qn = pair.first().second();
      String name = Util.getLastPart(qn);
      String packageFQN = packageFQN(entry.getName());
      ASMClassParser parser = new ASMClassParser(jar,entry, name, packageFQN);
      String second = Util.getAllButFirstPart(qn);
      String key = (packageFQN == null ? name : packageFQN+"."+Util.getFirstPart(qn));
      if(second != null) {
        ASMClassParser asmClassParser = map.get(key);
        // Deal with bad jars that contain class files of inner classes but not the outer class
        // e.g. the OS X rt.jar.
        if(asmClassParser != null) {
          asmClassParser.add(parser, second);
        }
      } else {
        map.put(key, parser);
        parsers.add(parser);
      }
    }

    Syntax syntax = lang.plugin(Syntax.class);
    //  	for(ASMClassParser parser: parsers) {
    //  		Type t = parser.load();
    //  		System.out.println("<<<<<<<<<<<<<<<");
    //  		System.out.println(syntax.toCode(t));
    //  		System.out.println(">>>>>>>>>>>>>>>");
    //  	}
    System.out.println(syntax.toCode(map.get("java.util.Set").load(lang)));
    System.out.println(syntax.toCode(map.get("java.util.Collection").load(lang)));
    jar.close();
  }

  private static String packageFQN(String entryName) {
    return Util.getAllButLastPart(Util.getAllButLastPart(entryName).replace('/', '.'));
  }

	public String resourceName() {
		return _jarFile.getName() + " : " +_entry.getName();
	}
}
