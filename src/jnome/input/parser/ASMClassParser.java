package jnome.input.parser;

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

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.core.modifier.StrictFP;
import jnome.core.modifier.Synchronized;
import jnome.core.modifier.Volatile;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.PureWildcard;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.document.Document;
import chameleon.core.lookup.LookupException;
import chameleon.core.modifier.Modifier;
import chameleon.core.namespace.LazyRootNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.method.Method;
import chameleon.oo.method.SimpleNameMethodHeader;
import chameleon.oo.method.exception.ExceptionClause;
import chameleon.oo.method.exception.TypeExceptionDeclaration;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.ExtendsConstraint;
import chameleon.oo.type.generics.ExtendsWildcard;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.SuperWildcard;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.oo.variable.FormalParameter;
import chameleon.oo.variable.VariableDeclaration;
import chameleon.plugin.output.Syntax;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Constructor;
import chameleon.support.modifier.Enum;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Interface;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Protected;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.util.Pair;
import chameleon.util.Util;
import chameleon.workspace.Project;

public class ASMClassParser {

	public ASMClassParser(JarFile file, JarEntry entry, Java lang, String className, String packageFQN) {
		_lang = lang;
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
			ASMClassParser c = _children.get(qn);
			c.add(child, next);
		}
	}
	
	private Map<String,ASMClassParser> _children;
	
	public Java language() {
		return _lang;
	}
	
	private JarFile _jarFile;
	
	private Java _lang;
	
	private JarEntry _entry;
		
	public Type load() throws FileNotFoundException, IOException, LookupException {
		Type t = read();
		Document doc = new Document();
		Namespace ns = language().defaultNamespace().getOrCreateNamespace(_packageFQN);
		NamespaceDeclaration decl = new NamespaceDeclaration(ns);
		doc.add(decl);
		decl.add(t);
		return t;
	}
	
	protected Type read() throws FileNotFoundException, IOException {
		InputStream inputStream = new BufferedInputStream(_jarFile.getInputStream(_entry));
		ClassReader reader = new ClassReader(inputStream);
		ClassExtractor extractor = new ClassExtractor();
		reader.accept(extractor, Opcodes.ASM4);
		inputStream.close();
		Type result = extractor.type();
		if(_children != null) {
			for(ASMClassParser child: _children.values()) {
				result.add(child.read());
			}
		}
		return result;
	}
	
	protected JavaTypeReference toRef(String tref) {
		return language().createTypeReference(toDots(tref));
	}

	private String toDots(String name) {
		return name.replace('/', '.').replace('$', '.');
	}
	
	protected ObjectOrientedFactory factory() {
		return language().plugin(ObjectOrientedFactory.class);
	}
	
	protected class ClassExtractor extends ClassVisitor {

		private Type _type;
		
		public Type type() {
			return _type;
		}
		
		public ClassExtractor() {
			super(Opcodes.ASM4);
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
				Type type = factory().createRegularType(new SimpleNameSignature(n));
				// OK, so ASM only creates a signature when there are generic parameters.
				// otherwise you only get superName and interfaces (you'll get them as well with generics though).
				// What a bad design. You're forcing me to write crap code.
				if(signature != null) {
					new SignatureReader(signature).accept(new ClassSignatureExtractor(type));
				} else {
					TypeReference zuppaKlass = toRef(superName);
					type.addInheritanceRelation(new SubtypeRelation(zuppaKlass));
					if(interfaces != null) {
						for(String iface: interfaces) {
							type.addInheritanceRelation(new SubtypeRelation(toRef(iface)));
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
				VariableDeclaration declaration = new VariableDeclaration(name);
				decl.add(declaration);
				if(signature != null) {
					new SignatureReader(signature).accept(new FieldSignatureExtractor(decl));
				} else {
					new SignatureReader(desc).accept(new FieldSignatureExtractor(decl));
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
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if(! isSynthetic(access)) {
				Method m;
				m = new NormalMethod(new SimpleNameMethodHeader(name, null));
				if(name.equals("<init>")) {
					name = _type.name();
					m.setName(name);
					m.addModifier(new Constructor());
					m.setReturnTypeReference(language().createTypeReference(name));
				}
				_type.add(m);
				List<Modifier> mods = accessToMethodModifier(access);
				m.addModifiers(mods);
				if(signature != null) {
					new SignatureReader(signature).accept(new MethodExtractor(m));
				} else {
					new SignatureReader(desc).accept(new MethodExtractor(m));
				}
				
				ExceptionClause clause = new ExceptionClause();
				m.setExceptionClause(clause);
				if(exceptions != null) {
					for(String ex:exceptions) {
						clause.add(new TypeExceptionDeclaration(toRef(ex)));
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
					result.add(_fieldAccessMap.get(i).clone());
				}
			}
			return result;
		}
				
		private void initFieldAccessMap() {
			_fieldAccessMap = new HashMap<>();
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
					result.add(_methodAccessMap.get(i).clone());
				}
			}
			return result;
		}
		
		protected boolean isVarargs(int access) {
			return ((access & Opcodes.ACC_VARARGS) != 0);
		}
		
		private void initMethodAccessMap() {
			_methodAccessMap = new HashMap<>();
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
						result.add(modifier.clone());
					}
				}
			}
			return result;
		}
		
		private void initClassAccessMap() {
			_classAccessMap = new HashMap<>();
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

		public FieldSignatureExtractor(MemberVariableDeclarator var) {
			_var = var;
		}

		private MemberVariableDeclarator _var;
		
		@Override
		protected void connect(TypeReference tref) {
			_var.setTypeReference(tref);
		}
	}
	

	
	protected class MethodExtractor extends SignatureVisitor {

		public MethodExtractor(Method method) {
			super(Opcodes.ASM4);
			_method = method;
		}
		
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
			return new TypeParameterBoundExtractor(_currentTypeParameter);
		}
		
		@Override
		public SignatureVisitor visitInterfaceBound() {
			return new TypeParameterBoundExtractor(_currentTypeParameter);
		}
		
		@Override
		public SignatureVisitor visitParameterType() {
			return new TypeReferenceExtractor() {
				@Override
				protected void connect(TypeReference tref) {
					String name = "arg" + _nbArgs++;
					_method.header().addFormalParameter(new FormalParameter(name, tref));
				}
			};
		}
		
		@Override
		public SignatureVisitor visitReturnType() {
			return new TypeReferenceExtractor() {
				@Override
				protected void connect(TypeReference tref) {
					_method.setReturnTypeReference(tref);
				}
			};
		}
		
	}
	
	private class TypeParameterBoundExtractor extends TypeReferenceExtractor {
		
		private FormalTypeParameter _param;
		
		public TypeParameterBoundExtractor(FormalTypeParameter param) {
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

		

		private class InheritanceExtractor extends TypeReferenceExtractor {
			private InheritanceExtractor() {
			}

			@Override
			protected void connect(TypeReference tref) {
				_type.addInheritanceRelation(new SubtypeRelation(tref));
			}
		}

		public ClassSignatureExtractor(Type type) {
			super(Opcodes.ASM4);
			_type = type;
		}
		
		private Type _type;
		
		@Override
		public SignatureVisitor visitSuperclass() {
			return new InheritanceExtractor();
		}
		
		@Override
		public SignatureVisitor visitInterface() {
			return new InheritanceExtractor();
		}
		
//		@Override
//		public void visitFo(String name) {
//		}
		
		@Override
		public void visitFormalTypeParameter(String name) {
			_param = new FormalTypeParameter(name);
			_type.addParameter(TypeParameter.class, _param);
		}
		
		private FormalTypeParameter _param;
		
		@Override
		public SignatureVisitor visitClassBound() {
			return new TypeParameterBoundExtractor(_param);
		}
		
		@Override
		public SignatureVisitor visitInterfaceBound() {
			return new TypeParameterBoundExtractor(_param);
		}
	}
	
	protected class TypeReferenceExtractor extends SignatureVisitor {

		public TypeReferenceExtractor() {
			super(Opcodes.ASM4);
			initPrimitiveMap();
		}
		
		private void initPrimitiveMap() {
			_primitiveMap = new HashMap<>();
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
			_tref = language().createTypeReference(getPrimitiveTypeName(t));
			connect(_tref);
		}
		
		@Override
		public void visitTypeVariable(String name) {
			_tref = language().createTypeReference(name);
			connect(_tref);
		}
		
		@Override
		public void visitClassType(String fqn) {
			_tref = toRef(fqn);
			connect(_tref);
		}
		
		protected void connect(TypeReference tref) {
			// do nothing by default;
		}
		
		@Override
		public SignatureVisitor visitArrayType() {
			return new TypeReferenceExtractor() {
				@Override
				protected void connect(TypeReference tref) {
					_tref = new ArrayTypeReference((JavaTypeReference) tref);
					TypeReferenceExtractor.this.connect(_tref);
				}
			};
		}
		
		@Override
		public void visitTypeArgument() {
			((BasicJavaTypeReference)typeReference()).addArgument(new PureWildcard());
		}
		
		@Override
		public SignatureVisitor visitTypeArgument(char kind) {
			// create visitor with 'this' as its parent.
			final ActualTypeArgumentWithTypeReference arg;
			if(kind == SignatureVisitor.INSTANCEOF) {
				arg = new BasicTypeArgument(null);
			} else if(kind == SignatureVisitor.EXTENDS) {
				arg = new ExtendsWildcard(null);
			} else if(kind == SignatureVisitor.SUPER) {
				arg = new SuperWildcard(null);
			} else {
				throw new ChameleonProgrammerException();
			}
			((BasicJavaTypeReference)typeReference()).addArgument(arg);
			return new TypeReferenceExtractor() {
				/**
				 * If there is no bound, we replace the type argument with a pure wildcard.
				 * I GUESS that ? is modeled as an EXTENDS bound without a type reference.
				 */
				//FIXME Or is this method no invoked for ??
				@Override
				public void visitEnd() {
					if(this.typeReference() == null) {
						SingleAssociation parentLink = arg.parentLink();
						parentLink.getOtherRelation().replace(parentLink, new PureWildcard().parentLink());
					}
				}
				protected void connect(TypeReference tref) {
					arg.setTypeReference(tref);
				}
			};
		}
		
	}
	
	
  public static void main(String[] args) throws IOException, Exception {
  	JarFile jar = new JarFile(args[0]);
  	Java lang = new JavaLanguageFactory().create();
  	Project project = new Project("test", new LazyRootNamespace(), lang);
  	Enumeration<JarEntry> entries = jar.entries();
  	List<Pair<String, JarEntry>> names = new ArrayList<>();
  	while(entries.hasMoreElements()) {
  		JarEntry entry = entries.nextElement();
  		String name = entry.getName();
  		if(name.endsWith(".class")) {
  			String tmp = Util.getLastPart(Util.getAllButLastPart(name).replace('/', '.')).replace('$', '.');
  			String shortName = Util.getLastPart(tmp);
  			if(! shortName.substring(0,1).matches("[0-9]")) {
  				names.add(new Pair<String, JarEntry>(tmp, entry));
  			}
  		}
  	}
  	Collections.sort(names, new Comparator<Pair<String,JarEntry>>(){
			@Override
			public int compare(Pair<String, JarEntry> o1, Pair<String, JarEntry> o2) {
				int first = o1.first().length();
				int second = o2.first().length();
				return first - second;
			}
  	});
  	Map<String, ASMClassParser> map = new HashMap<>();
  	List<ASMClassParser> parsers = new ArrayList<>();
  	for(Pair<String,JarEntry> pair: names) {
  		String fqn = pair.first();
			String name = Util.getFirstPart(fqn);
			JarEntry entry = pair.second();
			ASMClassParser parser = new ASMClassParser(jar,entry, lang, name,packageFQN(entry.getName()));
			String second = Util.getAllButFirstPart(fqn);
			if(second != null) {
				map.get(name).add(parser, second);
			} else {
				map.put(name, parser);
				parsers.add(parser);
			}
  	}
  	Syntax syntax = lang.plugin(Syntax.class);
  	for(ASMClassParser parser: parsers) {
  		Type t = parser.load();
  		System.out.println("<<<<<<<<<<<<<<<");
  		System.out.println(syntax.toCode(t));
  		System.out.println(">>>>>>>>>>>>>>>");
  	}
  	jar.close();
  }

	private static String packageFQN(String entryName) {
		String semiFQN = Util.getLastPart(Util.getAllButLastPart(entryName).replace('/', '.'));
		return semiFQN;
	}
}
