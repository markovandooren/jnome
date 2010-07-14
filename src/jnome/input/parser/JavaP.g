
/*

TODO

1) Support for annotations

*/


/*
 [The "BSD licence"]
 Copyright (c) 2007-2008 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *      
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more 
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or 
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse 
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several 
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and 
 *          normalInterfaceDeclaration rather than classDeclaration and 
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.  
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation, 
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source 
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java 
 *      letter-or-digit is a character for which the method 
 *      Character.isJavaIdentifierPart(int) returns true."
 */
parser grammar JavaP;

options {
  backtrack=true; 
  memoize=true;
  output=AST;
  superClass = ChameleonParser;
}

scope MethodScope {
  Method method;
  Token start;
}

scope TargetScope {
  InvocationTarget target;
  Token start;
}


@parser::members {

  public RegularMethodInvocation invocation(String name, InvocationTarget target) {
    return new JavaMethodInvocation(name, target);
  }

  private JavaFactory _javaFactory = new JavaFactory();
  
  public JavaFactory factory() {
    return _javaFactory;
  }
  
  public void setFactory(JavaFactory factory) {
    _javaFactory = factory;
  }

  public InvocationTarget cloneTargetOfTarget(NamedTarget target) {
    InvocationTarget result = null;
    if(target != null) {
      InvocationTarget targetOfTarget = target.getTarget();
      if(targetOfTarget != null) {
        result = targetOfTarget.clone();
      }
    }
    return result;
  }
  
  public RegularType createType(SimpleNameSignature signature) {
     return factory().createRegularType(signature);
  }

  public InvocationTarget cloneTarget(InvocationTarget target) {
    InvocationTarget result = null;
    if(target != null) {
        result = target.clone();
    }
    return result;
  }

  public static class ClassCreatorRest {
    public ClassCreatorRest(List<ActualArgument> args) {
      _args = args; // NO ENCAPSULATION, BUT IT IS JUST THE PARSER.
    }
    
    public List<ActualArgument> arguments() {
      return _args;
    }
    
    private List<ActualArgument> _args;
    
    public void setBody(ClassBody body) {
      _body = body;
    }
    
    public ClassBody body() {
      return _body;
    }
    
    private ClassBody _body;
  }
  

  public static class StupidVariableDeclaratorId {
       public StupidVariableDeclaratorId(String name, int dimension, CommonToken nameToken) {
         _name = name;
         _dimension = dimension;
         _token = nameToken;
       }
       private final String _name;
       private final int _dimension;
       
       public CommonToken nameToken() {
         return _token;
       }
       
       private CommonToken _token;
       
       public String name() {
         return _name;
       }
       
       public int dimension() {
         return _dimension;
       }
  }
  

  public void processType(NamespacePart np, Type type){
    if(np == null) {throw new IllegalArgumentException("namespace part given to processType is null.");}
    if(type == null) {throw new IllegalArgumentException("type given to processType is null.");}
    np.add(type);
    // inherit from java.lang.Object if there is no explicit extends relation
    String fqn = type.getFullyQualifiedName();
    if(fqn != null) {
      if(type.inheritanceRelations().isEmpty() && (! fqn.equals("java.lang.Object"))){
        type.addInheritanceRelation(new SubtypeRelation(createTypeReference(new NamespaceOrTypeReference("java.lang"),"Object")));
      }
    }

  }
  
  public JavaTypeReference myToArray(JavaTypeReference ref, StupidVariableDeclaratorId id) {
    int dim = id.dimension(); 
    if(dim > 0) {
      return new ArrayTypeReference(ref,dim);
    } else {
      return ref;
    }
  }
  
  public void addNonTopLevelObjectInheritance(Type type) {
    if(type.inheritanceRelations().isEmpty()){
      type.addInheritanceRelation(new SubtypeRelation(createTypeReference(new NamespaceOrTypeReference("java.lang"),"Object")));
    }
  }
  
  public JavaTypeReference typeRef(String qn) {
    return ((Java)language()).createTypeReference(qn);
  }

  public JavaTypeReference createTypeReference(CrossReference<?, ?, ? extends TargetDeclaration> target, String name) {
    return ((Java)language()).createTypeReference(target,name);
  }
  
  public JavaTypeReference createTypeReference(CrossReference<?, ?, ? extends TargetDeclaration> target, SimpleNameSignature signature) {
    return ((Java)language()).createTypeReference(target,signature);
  }

  public JavaTypeReference createTypeReference(NamedTarget target) {
    return ((Java)language()).createTypeReference(target);
  }
  
  public Java java() {
    return (Java)language();
  }
}


// starting point for parsing a java file
/* The annotations are separated out to make parsing faster, but must be associated with
   a packageDeclaration or a typeDeclaration (and not an empty one). */
compilationUnit returns [CompilationUnit element] 
@init{ 
NamespacePart npp = null;
retval.element = getCompilationUnit();
}
    :    annotations
        (   np=packageDeclaration
                {npp=np.element;
                 retval.element.add(npp);
                 npp.addImport(new DemandImport(new NamespaceReference("java.lang")));
                } 
            (imp=importDeclaration{npp.addImport(imp.element);})* 
            (typech=typeDeclaration
                {processType(npp,typech.element);
                }
            )*
        |   cd=classOrInterfaceDeclaration
               {npp = new NamespacePart(language().defaultNamespace());
                retval.element.add(npp);
                npp.addImport(new DemandImport(new NamespaceReference("java.lang")));
                processType(npp,cd.element);
               } 
            (typech=typeDeclaration
               {processType(npp,typech.element);
               }
            )*
        )
    |   (np=packageDeclaration
            {
              npp=np.element;
            }
         )?
        {
         if(npp == null) {
           npp = new NamespacePart(language().defaultNamespace());
         }
         npp.addImport(new DemandImport(new NamespaceReference("java.lang")));
         retval.element.add(npp);
        }
        (imp=importDeclaration
          {npp.addImport(imp.element);}
        )* 
        (typech=typeDeclaration
          {
           processType(npp,typech.element);
          }
        )*
    ;

packageDeclaration returns [NamespacePart element]
    :   pkgkw='package' qn=qualifiedName ';'
         {try{
           retval.element = new NamespacePart(getDefaultNamespace().getOrCreateNamespace($qn.text));
           setKeyword(retval.element,pkgkw);
         }
         catch(ModelException exc) {
           //this should not happen, something is wrong with the parser
           throw new ChameleonProgrammerException(exc);
         }
        }
    ;
    
importDeclaration returns [Import element]
    :   im='import' st='static'? qn=qualifiedName 
        {if(st == null) {
           retval.element = new TypeImport(typeRef($qn.text));
           setKeyword(retval.element,im);
         } else {
           retval.element = new SingleStaticImport(typeRef(Util.getAllButLastPart($qn.text)),Util.getLastPart($qn.text));
           setKeyword(retval.element,im);
         }
        } 
         ('.' '*' 
            {retval.element = new DemandImport(new NamespaceReference($qn.text));
             setKeyword(retval.element,im);
            })? ';'
    ;

    
typeDeclaration returns [Type element]
    :   cd=classOrInterfaceDeclaration {retval.element = cd.element;}
    |   ';'
    ;

//DONE

    
classOrInterfaceDeclaration returns [Type element]
@init{Token start = null; 
      Token end = null;}
@after{
  check_null(retval.element);
  setLocation(retval.element, start, end);
}
    :   mods=classOrInterfaceModifiers 
                {if(mods != null) {start=mods.start;}}
         (cd=classDeclaration 
                {retval.element=cd.element; end = cd.stop; if(mods == null) {start=cd.start;}} 
          | id=interfaceDeclaration 
                {retval.element=id.element; end=id.stop; if(mods == null) {start=id.start;}}) 
        {if(retval.element != null) {
           for(Modifier mod:mods.element) {
             retval.element.addModifier(mod);
           }
         }}
    ;
    
classOrInterfaceModifiers returns [List<Modifier> element]
@init {retval.element = new ArrayList<Modifier>();}
    :   (mod=classOrInterfaceModifier{retval.element.add(mod.element);})* 
    ;

classOrInterfaceModifier returns [Modifier element]
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    :   a=annotation {retval.element = a.element;}  // class or interface
    |   'public' {retval.element = new Public();}    // class or interface
    |   'protected' {retval.element = new Protected();} // class or interface
    |   'private' {retval.element = new Private();}   // class or interface
    |   'abstract' {retval.element = new Abstract();}  // class or interface
    |   'static' {retval.element = new Static();}    // class or interface
    |   'final' {retval.element = new Final();}     // class only -- does not apply to interfaces
    |   'strictfp' {retval.element = new StrictFP();}  // class or interface
    ;

modifiers returns [List<Modifier> element]
@init {retval.element = new ArrayList<Modifier>();}
    :   (mod=modifier{retval.element.add(mod.element);})*
    ;

classDeclaration returns [Type element]
@after{check_null(retval.element);}
    :   cd=normalClassDeclaration { retval.element = cd.element;}
    |   ed=enumDeclaration {retval.element = ed.element;}
    ;
    
normalClassDeclaration returns [RegularType element]
    :   clkw='class' name=Identifier {retval.element = createType(new SimpleNameSignature($name.text)); setLocation(retval.element,name,"__NAME");} (params=typeParameters{for(FormalTypeParameter par: params.element){retval.element.addParameter(TypeParameter.class,par);}})?
        (extkw='extends' sc=type 
            {SubtypeRelation extRelation = new SubtypeRelation(sc.element); 
             retval.element.addInheritanceRelation(extRelation);
             setKeyword(extRelation,extkw);
            })? 
        (impkw='implements' trefs=typeList 
            {for(TypeReference ref: trefs.element) {
                SubtypeRelation rel = new SubtypeRelation(ref);
                retval.element.addInheritanceRelation(rel);
                rel.addModifier(new Implements());
             }
            } )?
        body=classBody {retval.element.body().addAll(body.element.elements());}
        {
         setKeyword(retval.element,clkw);
         // FIXME: the implements keyword should not be attached to the class, but there is only one.
         setKeyword(retval.element,impkw);
        }
    ;
    
typeParameters returns [List<FormalTypeParameter> element]
@init{retval.element = new ArrayList<FormalTypeParameter>();}
    :   '<' par=typeParameter{retval.element.add(par.element);} (',' par=typeParameter{retval.element.add(par.element);})* '>'
    ;

typeParameter returns [FormalTypeParameter element]
@init{
Token stop = null;
}
    :   name=Identifier{retval.element = new FormalTypeParameter(new SimpleNameSignature($name.text)); stop=name;} (extkw='extends' bound=typeBound{retval.element.addConstraint(bound.element); stop=bound.stop;})?
        {setKeyword(retval.element,extkw);
         setLocation(retval.element, name, stop);
         setLocation(retval.element,name,"__NAME");
        }
    ;
        
typeBound returns [ExtendsConstraint element]
@init{retval.element = new ExtendsConstraint();
JavaIntersectionTypeReference ref = null;
}
    :   tp=type 
         {retval.element.setTypeReference(tp.element);}
         ('&' tpp=type 
          {
           if(ref == null) {
             ref = new JavaIntersectionTypeReference();
             ref.add(retval.element.typeReference());
             retval.element.setTypeReference(ref);
           }
           ref.add(tpp.element);
          }
         )*
    ;

enumDeclaration returns [RegularType element]
scope{
  Type enumType;
}
    :   ENUM name=Identifier {retval.element = createType(new SimpleNameSignature($name.text)); 
                              retval.element.addModifier(new Enum()); 
                              $enumDeclaration::enumType=retval.element;
                              setLocation(retval.element,name,"__NAME");}
                  ('implements' trefs=typeList 
                         {for(TypeReference ref: trefs.element)
                               {
                                SubtypeRelation rel = new SubtypeRelation(ref);
                                retval.element.addInheritanceRelation(rel);
                                rel.addModifier(new Implements());
                                } 
                          } 
                   )? 
                   body=enumBody {retval.element.setBody(body.element);}
    ;

// Nothing must be done here
enumBody returns [ClassBody element]
@init{retval.element = new ClassBody();}
    :   '{' (csts=enumConstants
            {
             for(EnumConstant el: csts.element) {
                retval.element.add(el);
             }
            })? ','? (decls=enumBodyDeclarations {for(TypeElement el: decls.element){retval.element.add(el);}})? '}'
    ;

enumConstants returns [List<EnumConstant> element]
    :   ct=enumConstant {retval.element = new ArrayList<EnumConstant>(); retval.element.add(ct.element);} (',' cst=enumConstant{retval.element.add(cst.element);})*
    ;
    
enumConstant returns [EnumConstant element]
    :   annotations? name=Identifier {retval.element = new EnumConstant(new SimpleNameSignature($name.text));} (args=arguments {retval.element.addAllParameters(args.element);})? (body=classBody {retval.element.setBody(body.element);})?
    ;
    
enumBodyDeclarations returns [List<TypeElement> element]
    :   ';' {retval.element= new ArrayList<TypeElement>();} (decl=classBodyDeclaration {retval.element.add(decl.element);})*
    ;
    
interfaceDeclaration returns [Type element]
@after{check_null(retval.element);}
    :   id=normalInterfaceDeclaration {retval.element = id.element;}
    |   ad=annotationTypeDeclaration {retval.element = ad.element;}
    ;
    
normalInterfaceDeclaration returns [RegularType element]
    :   ifkw='interface' name=Identifier {retval.element = createType(new SimpleNameSignature($name.text)); 
                                          retval.element.addModifier(new Interface());
                                          setLocation(retval.element,name,"__NAME");} 
         (params=typeParameters{for(TypeParameter par: params.element){retval.element.addParameter(TypeParameter.class,par);}})? 
         (extkw='extends' trefs=typeList 
           {
             for(TypeReference ref: trefs.element){
              retval.element.addInheritanceRelation(new SubtypeRelation(ref));
             } 
           }
         )? 
         body=classBody {retval.element.setBody(body.element);}
         {
          setKeyword(retval.element,extkw);
          setKeyword(retval.element,ifkw);
         }
    ;
    
typeList returns [List<TypeReference> element]
    :   tp=type {retval.element = new ArrayList<TypeReference>(); retval.element.add(tp.element);}(',' tpp=type {retval.element.add(tpp.element);})*
    ;
    
classBody returns [ClassBody element]
    :   '{' {retval.element = new ClassBody();} (decl=classBodyDeclaration {retval.element.add(decl.element);})* '}'
    ;
    
interfaceBody returns [ClassBody element]
    :   '{' {retval.element = new ClassBody();} (decl=interfaceBodyDeclaration {retval.element.add(decl.element);})* '}'
    ;

classBodyDeclaration returns [TypeElement element]
@init{
  Token start=null;
  Token stop=null;
}
@after{setLocation(retval.element, (CommonToken)start, (CommonToken)stop);}
    :   sckw=';' {retval.element = new EmptyTypeElement(); start=sckw; stop=sckw;}
    |   stkw='static'? bl=block {retval.element = new StaticInitializer(bl.element); start=stkw;stop=bl.stop;}
    |   mods=modifiers decl=memberDecl {retval.element = decl.element; retval.element.addModifiers(mods.element); start=mods.start; stop=decl.stop;}
    ;
    
memberDecl returns [TypeElement element]
    :   gen=genericMethodOrConstructorDecl {retval.element = gen.element;}
    |   mem=memberDeclaration {retval.element = mem.element;}
    |   vmd=voidMethodDeclaration {retval.element = vmd.element;}
    |   cs=constructorDeclaration {retval.element = cs.element;}
    |   id=interfaceDeclaration {retval.element=id.element; addNonTopLevelObjectInheritance(id.element);}
    |   cd=classDeclaration {retval.element=cd.element; addNonTopLevelObjectInheritance(cd.element);}
    ;
    
voidMethodDeclaration returns [Method element]
scope MethodScope;
@after{setLocation(retval.element, methodname, "__NAME");}
    	: vt=voidType methodname=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($methodname.text), vt.element); $MethodScope::method = retval.element;} voidMethodDeclaratorRest	
    	;

voidType returns [JavaTypeReference element]
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop, "__PRIMITIVE");}
     	:	 'void' {retval.element=typeRef("void");}
     	;
    	
constructorDeclaration returns [Method element]
scope MethodScope;
        : consname=Identifier 
            {
             retval.element = new NormalMethod(new SimpleNameMethodHeader($consname.text), typeRef($consname.text)); 
             retval.element.addModifier(new JavaConstructor());
             $MethodScope::method = retval.element;
            } 
             constructorDeclaratorRest
	;
    
memberDeclaration returns [TypeElement element]
    :   method=methodDeclaration {retval.element=method.element;}
    |   field=fieldDeclaration {retval.element=field.element;}
    ;

genericMethodOrConstructorDecl returns [Method element]
    :   params=typeParameters rest=genericMethodOrConstructorRest {retval.element = rest.element; retval.element.header().addAllTypeParameters(params.element);}
    ;
    
genericMethodOrConstructorRest returns [Method element]
scope MethodScope;
@init{TypeReference tref = null;}
@after{check_null(retval.element);}
    :   (t=type {tref=t.element;}| 'void' {tref = typeRef("void");}) name=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($name.text),tref); $MethodScope::method = retval.element;} methodDeclaratorRest
    |   name=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($name.text),typeRef($name.text)); $MethodScope::method = retval.element;} constructorDeclaratorRest
    ;

methodDeclaration returns [Method element]
scope MethodScope;
    :   t=type name=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($name.text),t.element); $MethodScope::method = retval.element;} methodDeclaratorRest
    ;

fieldDeclaration returns [MemberVariableDeclarator element]
    :   ref=type {retval.element = new MemberVariableDeclarator(ref.element);} decls=variableDeclarators {for(VariableDeclaration decl: decls.element) {retval.element.add(decl);}} ';'
    ;
        
interfaceBodyDeclaration returns [TypeElement element]
    :   mods=modifiers decl=interfaceMemberDecl {retval.element = decl.element; for(Modifier mod: mods.element){retval.element.addModifier(mod);}}
    |   ';'
    ;

interfaceMemberDecl returns [TypeElement element]
    :   decl=interfaceMethodOrFieldDecl {retval.element = decl.element;}
    |   decl2=interfaceGenericMethodDecl {retval.element = decl2.element;}
    |   decl5=voidInterfaceMethodDeclaration {retval.element = decl5.element;}
    |   decl3=interfaceDeclaration {retval.element = decl3.element; addNonTopLevelObjectInheritance(decl3.element);}
    |   decl4=classDeclaration {retval.element = decl4.element;  addNonTopLevelObjectInheritance(decl4.element);}
    ;
    
voidInterfaceMethodDeclaration  returns [Method element]
scope MethodScope;
    	: vt=voidType methodname=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($methodname.text), vt.element); $MethodScope::method = retval.element;} voidInterfaceMethodDeclaratorRest
    	;    
    
interfaceMethodOrFieldDecl returns [TypeElement element]
    :   cst=interfaceConstant {retval.element = cst.element;}
    |   m=interfaceMethod {retval.element = m.element;}
    ;
    
    
interfaceConstant returns [MemberVariableDeclarator element]
    :   ref=type {retval.element = new MemberVariableDeclarator(ref.element);} decl=constantDeclarator {retval.element.add(decl.element);}(',' dec=constantDeclarator {retval.element.add(dec.element);})* ';'
    ;

interfaceMethod returns [Method element]
scope MethodScope;
	: tref=type methodname=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($methodname.text), tref.element); $MethodScope::method = retval.element;} interfaceMethodDeclaratorRest
	;

    
methodDeclaratorRest
@init{int count = 0;}
    :   pars=formalParameters 
           {for(FormalParameter par: pars.element){
               $MethodScope::method.header().addFormalParameter(par);
            }
           } 
        ('[' ']' {count++;})* 
        {if(count > 0) {
           JavaTypeReference original = (JavaTypeReference)$MethodScope::method.returnTypeReference();
           $MethodScope::method.setReturnTypeReference(new ArrayTypeReference(original,count));
         }
        }
        (thrkw='throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
        (   body=methodBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
        |   ';' {$MethodScope::method.setImplementation(null);}
        )
        {setKeyword($MethodScope::method,thrkw);}
    ;
    
voidMethodDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}}
         (thrkw='throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
        (   body=methodBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
        |   ';' {$MethodScope::method.setImplementation(null);}
        )
        {setKeyword($MethodScope::method,thrkw);}
    ;
    
interfaceMethodDeclaratorRest
@init{int count = 0;}
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}}
       ('[' ']' {count++;})* 
       {if(count > 0) {
          JavaTypeReference original = (JavaTypeReference)$MethodScope::method.returnTypeReference();
          $MethodScope::method.setReturnTypeReference(new ArrayTypeReference(original,count));
        }
       }
        (thrkw='throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})? ';'
       {setKeyword($MethodScope::method,thrkw);}
    ;
    
interfaceGenericMethodDecl returns [TypeElement element]
    :   typeParameters (type | 'void') Identifier
        interfaceMethodDeclaratorRest
    ;
    
voidInterfaceMethodDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}}
     ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
      ';'
    ;
    
constructorDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}} 
    ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
     body=constructorBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
    ;

constantDeclarator returns [JavaVariableDeclaration element]
@init{int count = 0;}
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    :   name=Identifier (('[' ']' {count++;})* '=' init=variableInitializer) 
       {retval.element = new JavaVariableDeclaration($name.text);
        retval.element.setArrayDimension(count); 
        retval.element.setInitialization(init.element);
        setLocation(retval.element, name, "__NAME");
        }
    ;
    
variableDeclarators returns [List<VariableDeclaration> element]
    :   decl=variableDeclarator {retval.element = new ArrayList<VariableDeclaration>(); retval.element.add(decl.element);}(',' decll=variableDeclarator {retval.element.add(decll.element);})*
    ;

variableDeclarator returns [JavaVariableDeclaration element]
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    :   id=variableDeclaratorId 
             {retval.element = new JavaVariableDeclaration(id.element.name()); 
              retval.element.setArrayDimension(id.element.dimension());
              setLocation(retval.element, id.element.nameToken(), "__NAME");
              } ('=' init=variableInitializer {retval.element.setInitialization(init.element);})?
    ;
    

    
variableDeclaratorId returns [StupidVariableDeclaratorId element]
@init{int count = 0;}
    :   name=Identifier ('[' ']' {count++;})* { retval.element = new StupidVariableDeclaratorId($name.text, count,(CommonToken)name);}
    ;

variableInitializer returns [Expression element]
    :   init=arrayInitializer {retval.element = init.element;}
    |   expr=expression {retval.element = expr.element;}
    ;
        
arrayInitializer returns [ArrayInitializer element]
    :   '{' {retval.element = new ArrayInitializer();} (init=variableInitializer {retval.element.addInitializer(init.element);}(',' initt=variableInitializer{retval.element.addInitializer(initt.element);})* (',')? )? '}'
    ;

/**
   annotation            (not required I think)
    |
 */
modifier returns [Modifier element]
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    :   mod=classOrInterfaceModifier {retval.element = mod.element;}
    |   'native' {retval.element = new Native();}
    |   'synchronized' {retval.element = new Synchronized();}
    |   'transient' {retval.element = new Transient();}
    |   'volatile' {retval.element = new Volatile();}
    ;

enumConstantName returns [String element]
    :   id=Identifier {retval.element=$id.text;}
    ;

typeName returns [String element]
    :   name=qualifiedName {retval.element=name.element;}
    ;

type returns [JavaTypeReference element]
@init{int dimension=0;}
@after{setLocation(retval.element, retval.start, retval.stop);}
	:	cd=classOrInterfaceType ('[' ']' {dimension++;})* 
	        {
	         retval.element = cd.element.toArray(dimension);
	        }
	|	pt=primitiveType ('[' ']'{dimension++;})* {retval.element = pt.element.toArray(dimension);}
	;

classOrInterfaceType returns [JavaTypeReference element]
@init{NamespaceOrTypeReference target = null;
      Token stop = null;
     }
// We will process the different parts. The current type reference (return value) is kept in retval. Alongside that
// we keep a version of the latest namespace or type reference. If at any point after processing the first identifier
// target is null, we know that we have encountered a real type reference before, so anything after that becomes a type reference.
	:	name=Identifier 
	          {
	           retval.element = typeRef($name.text); 
	           target =  new NamespaceOrTypeReference($name.text);
	           stop=name; 
	          } 
	        (args=typeArguments 
	          {
	           // Add the type arguments
	           ((BasicJavaTypeReference)retval.element).addAllArguments(args.element);
	           // In this case, we know that the current element must be a type reference,
	           // so we set the target to the current type reference.
	           target = null;
	           stop=args.stop;
	          })?
	          {setLocation(retval.element,name,stop);}  
	        ('.' namex=Identifier 
	          {
	           if(target != null) {
	             retval.element = createTypeReference(target,$namex.text);
	             // We must clone the target here, or else it will be removed from the
	             // type reference we just created.
	             target = new NamespaceOrTypeReference(target.clone(),$namex.text);
	           } else {
	             retval.element = createTypeReference(retval.element,$namex.text);
	           }
	           stop=namex;
	          } 
	        (argsx=typeArguments 
	          {
	           // Add the type arguments
                   ((BasicJavaTypeReference)retval.element).addAllArguments(argsx.element);
	           // In this case, we know that the current element must be a type reference,
	           // so we se the target to the current type reference.
	           target = null;
	           stop = argsx.stop;
	          })? {setLocation(retval.element,name,stop);})*
	;

primitiveType returns [JavaTypeReference element]
@after{setLocation(retval.element, retval.start, retval.stop);}
    :   'boolean' {retval.element = typeRef("boolean");}
    |   'char' {retval.element = typeRef("char");}
    |   'byte' {retval.element = typeRef("byte");}
    |   'short' {retval.element = typeRef("short");}
    |   'int' {retval.element = typeRef("int");}
    |   'long' {retval.element = typeRef("long");}
    |   'float' {retval.element = typeRef("float");}
    |   'double' {retval.element = typeRef("double");}
    ;

variableModifier returns [Modifier element]
    :   'final' {retval.element = new Final();}
    |   annotation
    ;

typeArguments returns [List<ActualTypeArgument> element]
    :   '<' {retval.element = new ArrayList<ActualTypeArgument>();} arg=typeArgument {retval.element.add(arg.element);}(',' argx=typeArgument {retval.element.add(argx.element);})* '>'
    ;
    
typeArgument returns [ActualTypeArgument element]
@init{
boolean pure=true;
boolean ext=true;
}
    :   t=type {retval.element = java().createBasicTypeArgument(t.element);}
    |   '?'  
        (
          {pure=false;}
          ('extends' | 'super'{ext=false;}) 
          t=type
          {if(ext) {
            retval.element = java().createExtendsWildcard(t.element);
           } else {
            retval.element = java().createSuperWildcard(t.element);
           }
          }
        )?
        {if(pure) {
           retval.element = java().createPureWildcard();
         }
        }
    ;
    
qualifiedNameList returns[List<String> element]
@init{retval.element = new ArrayList<String>();}
    :   q=qualifiedName {retval.element.add($q.text);} (',' qn=qualifiedName {retval.element.add($qn.text);})*
    ;

formalParameters returns [List<FormalParameter> element]
@init{retval.element = new ArrayList<FormalParameter>();}
    :   '(' (pars=formalParameterDecls {retval.element=pars.element;})? ')'
    ;
    
formalParameterDecls returns [List<FormalParameter> element]
    :   mods=variableModifiers t=type id=variableDeclaratorId 
        (',' decls=formalParameterDecls {retval.element=decls.element; })?
        {if(retval.element == null) {
         retval.element=new ArrayList<FormalParameter>();}
         FormalParameter param = new FormalParameter(new SimpleNameSignature(id.element.name()),myToArray(t.element, id.element));
         param.addAllModifiers(mods.element);
         retval.element.add(0,param);
         setLocation(param, mods.start,id.stop);
         }
    |   modss=variableModifiers tt=type '...' idd=variableDeclaratorId 
        {retval.element = new ArrayList<FormalParameter>(); 
         FormalParameter param = new MultiFormalParameter(new SimpleNameSignature(idd.element.name()),myToArray(tt.element,idd.element));
         param.addAllModifiers(modss.element);
         retval.element.add(param);
         setLocation(param, modss.start, idd.stop);
         }
    ;
    
    
methodBody returns [Block element]
    :   b=block {retval.element = b.element;}
    ;

constructorBody returns [Block element]
    :   '{' {retval.element = new Block();} 
         (inv=explicitConstructorInvocation {retval.element.addStatement(new StatementExpression(inv.element));})? 
         (bs=blockStatement {retval.element.addStatement(bs.element);})* '}'
    ;

explicitConstructorInvocation returns [Invocation element]
@init{Expression target=null;}
    :   nonWildcardTypeArguments? 'this' args=arguments ';'
       {retval.element = new ThisConstructorDelegation();
        retval.element.addAllArguments(args.element);}
    | (prim=primary '.' {target=prim.element;})? nonWildcardTypeArguments? 'super' argsx=arguments ';' 
      {retval.element = new SuperConstructorDelegation();
       retval.element.addAllArguments(argsx.element);
       if(target != null) {
         retval.element.setTarget(target);
       }
      }
    ;


qualifiedName returns [String element]
@init{StringBuffer buffer = new StringBuffer();}
    :   id=Identifier {buffer.append($id.text);}('.' idx=Identifier {buffer.append($idx.text);})*
    ;
    
literal returns [Literal element]
    :   intl=integerLiteral {retval.element=intl.element;}
    |   fl=FloatingPointLiteral {retval.element=new RegularLiteral(typeRef("float"),$fl.text);}
    |   charl=CharacterLiteral {retval.element=new RegularLiteral(typeRef("char"),$charl.text);}
    |   strl=StringLiteral {retval.element=new RegularLiteral(typeRef("java.lang.String"),$strl.text);}
    |   booll=booleanLiteral {retval.element=booll.element;}
    |   'null' {retval.element = new NullLiteral();}
    ;

integerLiteral returns [Literal element]
    :   hexl=HexLiteral {retval.element=new RegularLiteral(typeRef("int"),$hexl.text);}
    |   octl=OctalLiteral {retval.element=new RegularLiteral(typeRef("int"),$octl.text);}
    |   decl=DecimalLiteral {retval.element=new RegularLiteral(typeRef("int"),$decl.text);}
    ;

booleanLiteral returns [Literal element]
    :   'true' {retval.element = new RegularLiteral(typeRef("boolean"),"true");}
    |   'false' {retval.element = new RegularLiteral(typeRef("boolean"),"false");}
    ;

// ANNOTATIONS

annotations returns [List<AnnotationModifier> element]
@init{retval.element = new ArrayList<AnnotationModifier>();}
    :   (a=annotation {retval.element.add(a.element);})+
    ;

annotation returns [AnnotationModifier element]
    :   '@' a=annotationName {retval.element=new AnnotationModifier($a.text);} ( '(' ( elementValuePairs | elementValue )? ')' )?
    ;
    
annotationName
    : Identifier ('.' Identifier)*
    ;

elementValuePairs
    :   elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    :   Identifier '=' elementValue
    ;
    
elementValue
    :   conditionalExpression
    |   annotation
    |   elementValueArrayInitializer
    ;
    
elementValueArrayInitializer
    :   '{' (elementValue (',' elementValue)*)? (',')? '}'
    ;
    
annotationTypeDeclaration returns [TypeWithBody element]
    :   '@' 'interface' name=Identifier 
             {
               retval.element = (TypeWithBody)createType(new SimpleNameSignature($name.text));
               retval.element.addModifier(new AnnotationType());
               setLocation(retval.element,name,"__NAME");
             } 
             body=annotationTypeBody {retval.element.setBody(body.element);}
    ;
    
annotationTypeBody returns [ClassBody element]
@init{retval.element = new ClassBody();}
    :   '{' (annotationTypeElementDeclaration)* '}'
    ;
    
annotationTypeElementDeclaration returns [TypeElement element]
    :   mods=modifiers rest=annotationTypeElementRest 
       {
         retval.element = rest.element;
         for(Modifier modifier: mods.element) {
           retval.element.addModifier(modifier);
         }
       }
    ;
    
annotationTypeElementRest returns [TypeElement element]
    :   t=type ann=annotationMethodOrConstantRest[$t.element] {retval.element = ann.element;} 
        
    ';' 
    |   cd=normalClassDeclaration { retval.element = cd.element; addNonTopLevelObjectInheritance(cd.element);}';'?
    |   id=normalInterfaceDeclaration { retval.element = id.element; addNonTopLevelObjectInheritance(id.element);}';'?
    |   en=enumDeclaration {retval.element = en.element;} ';'?
    |   an=annotationTypeDeclaration {retval.element = an.element;} ';'?
    ;
    
annotationMethodOrConstantRest[TypeReference type] returns [TypeElement element]
    :   a=annotationMethodRest[$type] {retval.element = a.element;}
    |   aa=annotationConstantRest[$type] {retval.element = aa.element;}
    ;
    
annotationMethodRest[TypeReference type] returns [Method element]
    :   name=Identifier '(' ')' {retval.element = new NormalMethod(new SimpleNameMethodHeader($name.text),type);} (defaultValue {})?
    ;
    
annotationConstantRest[TypeReference type] returns [MemberVariableDeclarator element]
    :   decls=variableDeclarators 
        {retval.element = new MemberVariableDeclarator(type);
         for(VariableDeclaration decl: decls.element) {
           retval.element.add(decl);
         } 
        }
    ;
    
defaultValue
    :   'default' elementValue
    ;

// STATEMENTS / BLOCKS

block returns [Block element]
    :   '{' {retval.element = new Block();} (stat=blockStatement {if(stat != null) {retval.element.addStatement(stat.element);}})* '}'
    ;
    
blockStatement returns [Statement element]
@after{assert(retval.element != null);}
    :   local=localVariableDeclarationStatement {retval.element = local.element;}
    |   cd=classOrInterfaceDeclaration {retval.element = new LocalClassStatement(cd.element);}
    |   stat=statement {retval.element = stat.element;}
    ;
    
localVariableDeclarationStatement returns [Statement element]
    :    local=localVariableDeclaration {retval.element=local.element;} ';'
    ;

localVariableDeclaration returns [LocalVariableDeclarator element]
    :   mods=variableModifiers ref=type {retval.element = new LocalVariableDeclarator(ref.element);} decls=variableDeclarators {for(VariableDeclaration decl: decls.element) {retval.element.add(decl);}}
        {for(Modifier mod : mods.element) {retval.element.addModifier(mod);}}
    ;
    
variableModifiers returns [List<Modifier> element]
@init{retval.element = new ArrayList<Modifier>();}
    :   (mod=variableModifier {retval.element.add(mod.element);})*
    ;

statement returns [Statement element]
@after{check_null(retval.element);
setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    : bl=block {retval.element = bl.element;}
    |   ASSERT asexpr=expression {retval.element=new AssertStatement(asexpr.element);}(':' asexprx=expression {((AssertStatement)retval.element).setMessageExpression(asexprx.element);})? ';'
    |   ifkey='if' ifexpr=parExpression ifif=statement (options {k=1;}:elsekey='else' ifelse=statement)? 
         {retval.element=new IfThenElseStatement(ifexpr.element, ifif.element, (ifelse == null ? null : ifelse.element));
          setKeyword(retval.element,ifkey);
          if(ifelse != null) {
            setKeyword(retval.element,elsekey);
          }
         }
    |   forkey='for' '(' forc=forControl ')' forstat=statement 
        {retval.element = new ForStatement(forc.element,forstat.element);
        setKeyword(retval.element,forkey);}
    |   whilkey='while' wexs=parExpression wstat=statement 
        {retval.element = new WhileStatement(wexs.element, wstat.element);
        setKeyword(retval.element,whilkey);}
    |   dokey='do' dostat=statement whilekey='while' doex=parExpression ';' 
        {retval.element= new DoStatement(doex.element, dostat.element);
        setKeyword(retval.element,dokey);
        setKeyword(retval.element,whilekey);}
    |   trykey='try' traaibl=block 
        {retval.element = new TryStatement(traaibl.element);
        setKeyword(retval.element,trykey);}
        ( cts=catches finkey='finally' trybl=block 
           {((TryStatement)retval.element).addAllCatchClauses(cts.element); 
            ((TryStatement)retval.element).setFinallyClause(new FinallyClause(trybl.element));
            setKeyword(retval.element,finkey);
           }
        | ctss=catches {((TryStatement)retval.element).addAllCatchClauses(ctss.element);}
        |   finnkey='finally' trybll=block 
           {((TryStatement)retval.element).setFinallyClause(new FinallyClause(trybll.element));
           setKeyword(retval.element,finnkey);}
        )
    |   switchkey='switch' swexpr=parExpression 
          {retval.element = new SwitchStatement(swexpr.element);
          setKeyword(retval.element,switchkey);}
          '{' cases=switchBlockStatementGroups {((SwitchStatement)retval.element).addAllCases(cases.element);}'}'
    |   synkey='synchronized' synexpr=parExpression synstat=block 
          {retval.element = new SynchronizedStatement(synexpr.element,synstat.element);
          setKeyword(retval.element,synkey);}
    |   retkey='return' 
            {retval.element = new ReturnStatement();
             setKeyword(retval.element,retkey);} 
          (retex=expression {((ReturnStatement)retval.element).setExpression(retex.element);})? ';'
    |   throwkey='throw' threx=expression 
        {retval.element = new ThrowStatement(threx.element);
        setKeyword(retval.element,throwkey);}
        ';'
    |   breakkey='break' 
        {retval.element = new BreakStatement();
        setKeyword(retval.element,breakkey);} 
        (name=Identifier {((BreakStatement)retval.element).setLabel($name.text);})? ';'
    |   continuekey='continue' 
        {retval.element = new ContinueStatement();
        setKeyword(retval.element,continuekey);} 
        (name=Identifier {((ContinueStatement)retval.element).setLabel($name.text);})? ';'
    |   ';' {retval.element = new EmptyStatement();}
    |   stattex=statementExpression {retval.element = new StatementExpression(stattex.element);} ';'
    |   name=Identifier ':' labstat=statement {retval.element = new LabeledStatement($name.text,labstat.element);}
    ;
    
catches returns [List<CatchClause> element]
@after{assert(retval.element != null);}
    :   {retval.element = new ArrayList<CatchClause>();} (ct=catchClause {retval.element.add(ct.element);})+
    ;
    
catchClause returns [CatchClause element]
@after{assert(retval.element != null);}
    :   catchkey='catch' '(' par=formalParameter ')' bl=block 
        {retval.element = new CatchClause(par.element, bl.element);
        setKeyword(retval.element,catchkey);}
    ;

formalParameter returns [FormalParameter element]
@after{assert(retval.element != null);}
    :   mods=variableModifiers tref=type name=variableDeclaratorId 
        {retval.element = new FormalParameter(new SimpleNameSignature(name.element.name()), myToArray(tref.element, name.element));
         setLocation(retval.element, mods.start, name.stop);
        }
    ;
        
switchBlockStatementGroups returns [List<SwitchCase> element]
@after{assert(retval.element != null);}
    :   {retval.element = new ArrayList<SwitchCase>();}(cs=switchCase {retval.element.add(cs.element);})*
    ;
    
/* The change here (switchLabel -> switchLabel+) technically makes this grammar
   ambiguous; but with appropriately greedy parsing it yields the most
   appropriate AST, one in which each group, except possibly the last one, has
   labels and statements. */
switchCase returns [SwitchCase element]
@after{assert(retval.element != null);}
    :   label=switchLabel {retval.element = new SwitchCase(label.element);} blockStatement*
    ;
    
switchLabel returns [SwitchLabel element]
@after{assert(retval.element != null);}
    :   'case' csexpr=constantExpression ':' {retval.element = new CaseLabel(csexpr.element);}
    |   'case' enumname=enumConstantName ':' {retval.element = new EnumLabel(enumname.element);}
    |   'default' ':'{retval.element = new DefaultLabel();}
    ;
    
forControl returns [ForControl element]
options {k=3;} // be efficient for common case: for (ID ID : ID) ...
@after{assert(retval.element != null);}
    :   enh=enhancedForControl {retval.element=enh.element;}
    |   in=forInit? ';' e=expression? ';' u=forUpdate? {retval.element = new SimpleForControl($in.element,$e.element,$u.element);}
    ;

forInit returns [ForInit element]
@after{assert(retval.element != null);}
    :   local=localVariableDeclaration {retval.element=local.element;}
    |   el=expressionList {retval.element = new StatementExprList(); for(Expression expr: el.element){((StatementExprList)retval.element).addStatement(new StatementExpression(expr));};}
    ;
    
enhancedForControl returns [ForControl element]
@after{assert(retval.element != null);}
    :   local=localVariableDeclaration ':' ex=expression {retval.element = new EnhancedForControl(local.element, ex.element);}
    ;

forUpdate returns [StatementExprList element]
    :   el=expressionList {retval.element = new StatementExprList(); for(Expression expr: el.element){((StatementExprList)retval.element).addStatement(new StatementExpression(expr));};}
    ;

// EXPRESSIONS

parExpression returns [Expression element]
@init{
Token start=null;
Token stop=null;
}
@after{
setLocation(retval.element,start,stop);
}
    :   s='(' expr=expression {retval.element = expr.element;} e=')'
        {
          start = s;
          stop = e;
        }
    ;
    
expressionList returns [List<Expression> element]
    :   {retval.element = new ArrayList<Expression>();} e=expression 
        {if(e.element == null) {System.out.println($e.text);throw new RuntimeException("parser error");}
         retval.element.add(e.element);}
         (',' ex=expression {retval.element.add(ex.element);})*
    ;

statementExpression returns [Expression element]
    :   e=expression {retval.element = e.element;}
    ;
    
constantExpression returns [Expression element]
    :   e=expression {retval.element = e.element;}
    ;
    
expression returns [Expression element]
    :   ex=conditionalExpression {retval.element=ex.element;} (op=assignmentOperator exx=expression 
        {String txt = $op.text; 
         if(txt.equals("=")) {
           retval.element = new AssignmentExpression(ex.element,exx.element);
         } else {
           retval.element = new InfixOperatorInvocation($op.text,ex.element);
           ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         }
         setLocation(retval.element,op.start,op.stop,"__NAME");
         setLocation(retval.element,retval.start,exx.stop);
        }
        )?
    ;
    
assignmentOperator
    :   '='
    |   '+='
    |   '-='
    |   '*='
    |   '/='
    |   '&='
    |   '|='
    |   '^='
    |   '%='
    |   ('<' '<' '=')=> t1='<' t2='<' t3='=' 
        { $t1.getLine() == $t2.getLine() &&
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   ('>' '>' '>' '=')=> t1='>' t2='>' t3='>' t4='='
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&
          $t3.getLine() == $t4.getLine() && 
          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() }?
    |   ('>' '>' '=')=> t1='>' t2='>' t3='='
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    ;

conditionalExpression returns [Expression element]
    :   ex=conditionalOrExpression {retval.element = ex.element;}( '?' exx=expression ':' exxx=expression 
        {retval.element = new ConditionalExpression(retval.element,exx.element,exxx.element);
         setLocation(retval.element,retval.start,exxx.stop);
        }
    )?
    ;

conditionalOrExpression returns [Expression element]
    :   ex=conditionalAndExpression {retval.element = ex.element;} ( '||' exx=conditionalAndExpression 
        {retval.element = new ConditionalOrExpression(retval.element, exx.element);
         setLocation(retval.element,retval.start,exx.stop);
        })*
    ;

conditionalAndExpression returns [Expression element]
    :   ex=inclusiveOrExpression {retval.element = ex.element;} ( '&&' exx=inclusiveOrExpression 
        {retval.element = new ConditionalAndExpression(retval.element, exx.element);
         setLocation(retval.element,retval.start,exx.stop);
        })*
    ;

inclusiveOrExpression returns [Expression element]
    :   ex=exclusiveOrExpression {retval.element = ex.element;} ( '|' exx=exclusiveOrExpression 
       {
         retval.element = new InfixOperatorInvocation("|", retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,retval.start,exx.stop);
        } )*
    ;

exclusiveOrExpression returns [Expression element]
    :   ex=andExpression {retval.element = ex.element;} ( '^' exx=andExpression
    {
         retval.element = new InfixOperatorInvocation("^", retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,retval.start,exx.stop);
        } )*
    ;

andExpression returns [Expression element]
    :   ex=equalityExpression {retval.element = ex.element;} ( '&' exx=equalityExpression
    {
         retval.element = new InfixOperatorInvocation("&", retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,retval.start,exx.stop);
        } )*
    ;

equalityExpression returns [Expression element]
@init{String op=null;}
    :   ex=instanceOfExpression  {retval.element = ex.element;} 
          ( ('==' {op="==";} | '!=' {op="!=";}) exx=instanceOfExpression 
        {
         retval.element = new InfixOperatorInvocation(op, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,retval.start,exx.stop);
        } )*
    ;

instanceOfExpression returns [Expression element]
@after{check_null(retval.element);}
    :   ex=relationalExpression {
            //if(ex.element == null) {throw new Error("retval is null");} 
            retval.element = ex.element;} 
       ('instanceof' tref=type {retval.element = new InstanceofExpression(ex.element, tref.element);
         setLocation(retval.element,ex.start,tref.stop);
       }
       )?
    ;

relationalExpression returns [Expression element]
    :   ex=shiftExpression {
              //if(ex.element == null) {throw new Error("retval is null");}
              retval.element = ex.element;} ( op=relationalOp exx=shiftExpression 
        {
         retval.element = new InfixOperatorInvocation($op.text, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,ex.start,exx.stop);
        }
    )*
    ;
    
relationalOp
    :   ('<' '=')=> t1='<' t2='=' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   ('>' '=')=> t1='>' t2='=' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   '<' 
    |   '>' 
    ;

shiftExpression returns [Expression element]
    :   ex=additiveExpression {check_null(ex.element); retval.element = ex.element;} ( op=shiftOp exx=additiveExpression 
    {
         retval.element = new InfixOperatorInvocation($op.text, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,ex.start,exx.stop);
        }
    )*
    ;

shiftOp
    :   ('<' '<')=> t1='<' t2='<' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   ('>' '>' '>')=> t1='>' t2='>' t3='>' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   ('>' '>')=> t1='>' t2='>'
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    ;


additiveExpression returns [Expression element]
@init{String op = null;}
    :   ex=multiplicativeExpression {check_null(ex.element); retval.element = ex.element;} ( ('+' {op="+";} | '-' {op="-";}) exx=multiplicativeExpression 
    {
         retval.element = new InfixOperatorInvocation(op, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,ex.start,exx.stop);
        })*
    ;

multiplicativeExpression returns [Expression element]
@init{String op = null;}
    :   ex=unaryExpression {check_null(ex.element); retval.element = ex.element;} ( ( '*' {op="*";} | '/' {op="/";} | '%' {op="\%";}) exx=unaryExpression 
    {
         retval.element = new InfixOperatorInvocation(op, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(new ActualArgument(exx.element));
         setLocation(retval.element,ex.start,exx.stop);
        })*
    ;
    
unaryExpression returns [Expression element]
    :   '+' ex=unaryExpression {retval.element = new PrefixOperatorInvocation("+",ex.element);
	setLocation(retval.element,retval.start,ex.stop);
    }
    |   '-' exx=unaryExpression {retval.element = new PrefixOperatorInvocation("-",exx.element);
	setLocation(retval.element,retval.start,exx.stop);
    }
    |   '++' exxx=unaryExpression {retval.element = new PrefixOperatorInvocation("++",exxx.element);
	setLocation(retval.element,retval.start,exxx.stop);
    }
    |   '--' exxxx=unaryExpression {retval.element = new PrefixOperatorInvocation("--",exxxx.element);
	setLocation(retval.element,retval.start,exxxx.stop);
    }
    |   eks=unaryExpressionNotPlusMinus {check_null(eks.element); retval.element = eks.element;} 
    ;

unaryExpressionNotPlusMinus returns [Expression element]
scope TargetScope;
@init{
Token start=null;
Token stop=null;
}
    :   a='~' {start=a;} ex=unaryExpression 
        {retval.element = new PrefixOperatorInvocation("~",ex.element); 
         stop=ex.stop;
         setLocation(retval.element,start,stop);
        }
    |   b='!' {start=b;} exx=unaryExpression 
        {retval.element = new PrefixOperatorInvocation("!",exx.element); 
         stop=exx.stop;
         setLocation(retval.element,start,stop);
        }
    |   castex=castExpression {check_null(castex.element); retval.element = castex.element;}
    |   prim=primary
           {check_null($prim.element);  
            $TargetScope::target=$prim.element; 
            retval.element=$prim.element;
            start=prim.start;
            $TargetScope::start = start;
           }
        (sel=selector 
           {check_null(sel.element); 
            $TargetScope::target=$sel.element; 
            retval.element = $sel.element; 
            stop=sel.stop;
            setLocation(retval.element,start,stop);}
        )*
        (
           c='++' {retval.element = new PostfixOperatorInvocation("++", retval.element); 
		   stop=c;
		   setLocation(retval.element,start,stop);}
         | d='--' {retval.element = new PostfixOperatorInvocation("--", retval.element); 
          	   stop=d;
          	   setLocation(retval.element,start,stop);}
        )?
    ;



// NEEDS_TARGET
selector returns [Expression element]
@init{
Token start=$TargetScope::start;
Token stop=null;
}
	:	
	'.' name=Identifier 
	        {
	         retval.element = new NamedTargetExpression($name.text,cloneTarget($TargetScope::target));
	         stop=name;
	        } 
	    (args=arguments 
	        {retval.element = invocation($name.text, $TargetScope::target);
	         ((RegularMethodInvocation)retval.element).addAllArguments(args.element);
	         stop=args.stop;
	        })?
	        {setLocation(retval.element,start,stop);}
    |   '.' thiskw='this' {retval.element = new ThisLiteral(createTypeReference((NamedTarget)$TargetScope::target));setLocation(retval.element,start,spkw);}
    |   '.' spkw='super'{$TargetScope::target= new SuperTarget($TargetScope::target);
                         setLocation($TargetScope::target,start,spkw);
                         setKeyword($TargetScope::target,spkw);
                        } 
            supsuf=superSuffix {check_null(supsuf.element); retval.element = supsuf.element;}
    |   '.' newkw='new' in=innerCreator {check_null(in.element); 
                                         retval.element = in.element;
                                         setKeyword(retval.element,newkw);}
    |   '[' arrex=expression bracket=']' 
          {retval.element = new ArrayAccessExpression((Expression)$TargetScope::target);
           ((ArrayAccessExpression)retval.element).addIndex(new FilledArrayIndex(arrex.element));
           setLocation(retval.element, start, bracket);
          }       
	;

castExpression returns [Expression element]
@after{setLocation(retval.element,retval.start,retval.stop);}
    :  '(' tref=primitiveType ')' unex=unaryExpression {retval.element = new ClassCastExpression(tref.element,unex.element);}
    |  '(' treff=type ')' unexx=unaryExpressionNotPlusMinus {retval.element = new ClassCastExpression(treff.element,unexx.element);}
    ;

primary returns [Expression element]
scope TargetScope;
@init{
Token start=null;
Token stop=null;
}
    :   parex=parExpression {retval.element = parex.element;}
    |   rubex=identifierSuffixRubbush {retval.element = rubex.element;}
    |    skw='super' {$TargetScope::target= new SuperTarget(); 
                     start=skw; stop=skw; 
                     $TargetScope::start=skw;
                     setKeyword($TargetScope::target,skw);
                     } 
        supsuf=superSuffix 
        {retval.element = supsuf.element; 
        setLocation($TargetScope::target,start,stop); // put locations on the SuperTarget.
        }
    |   lit=literal {retval.element = lit.element;}
    |   nkw='new' {start=nkw;} cr=creator {retval.element = cr.element;setKeyword(retval.element,nkw);}
    |   morerubex=moreIdentifierSuffixRubbish {retval.element = morerubex.element;}
    |   vt=voidType '.' clkw='class' {retval.element = new ClassLiteral(vt.element); start=vt.start;stop=clkw; setLocation(retval.element,start,stop);}
    |   tref=type '.' clkww='class' {retval.element = new ClassLiteral(tref.element);start=tref.start;stop=clkww; setLocation(retval.element,start,stop);}
    ;

moreIdentifierSuffixRubbish returns [Expression element]
scope TargetScope;
@init{
Token stop = null;
InvocationTarget scopeTarget = null;
}
@after {
if(! retval.element.descendants().contains(scopeTarget)) {
  scopeTarget.removeAllTags();
}
}
	:	id=Identifier 
	           {$TargetScope::target = new NamedTarget($id.text);
	            scopeTarget = $TargetScope::target;  
	            $TargetScope::start=id; 
	            stop=id;
	            setLocation($TargetScope::target,$TargetScope::start,stop);
	            }
	  ('.' idx=Identifier 
	       {$TargetScope::target = new NamedTarget($idx.text,$TargetScope::target);
	        scopeTarget = $TargetScope::target;
	        stop=idx;
	        setLocation($TargetScope::target, $TargetScope::start, idx);
	       }
	  )* 
	{retval.element = new NamedTargetExpression(((NamedTarget)$TargetScope::target).getName(),cloneTargetOfTarget(((NamedTarget)$TargetScope::target)));
	 setLocation(retval.element, $TargetScope::start, stop);
	 //The variable reference is only returned if none of the following subrules match.
	}
(       ('[' ']')+ '.' 'class'
    |   
        arr=arrayAccessSuffixRubbish {retval.element = arr.element;}
    |   arg=argumentsSuffixRubbish {retval.element.removeAllTags(); retval.element = arg.element;} // REMOVE VARIABLE REFERENCE POSITION!
    |   '.' clkw='class' 
         {retval.element.removeAllTags();
         retval.element = new ClassLiteral(createTypeReference((NamedTarget)$TargetScope::target));
          setLocation(retval.element, $TargetScope::start, clkw);
         }
    |   '.' gen=explicitGenericInvocation {retval.element.removeAllTags(); retval.element = gen.element;} // REMOVE VARIABLE REFERENCE POSITION!
    |   '.' thiskw='this' 
        {retval.element.removeAllTags();
          retval.element = new ThisLiteral(createTypeReference((NamedTarget)$TargetScope::target));
          setLocation(retval.element, $TargetScope::start, thiskw);
        }
    |   '.' supkw='super' {retval.element.removeAllTags();
                           $TargetScope::target= new SuperTarget($TargetScope::target);
                           setKeyword($TargetScope::target,supkw); 
                           setLocation($TargetScope::target,$TargetScope::start,supkw);
                           } 
            supsuf=superSuffix {
               retval.element = supsuf.element;
            }// REMOVE VARIABLE REFERENCE POSITION!
    |   '.' newkw='new' in=innerCreator {retval.element = in.element;setKeyword(retval.element,newkw);})?
	;

identifierSuffixRubbush returns [Expression element]
scope TargetScope;
	:	'this' {$TargetScope::target = new ThisLiteral();}('.' id=Identifier {$TargetScope::target = new NamedTarget($id.text,$TargetScope::target);})* 
	{if($TargetScope::target instanceof ThisLiteral) {
	  retval.element = (ThisLiteral)$TargetScope::target;
	 } else {
	  retval.element = new NamedTargetExpression(((NamedTarget)$TargetScope::target).getName(),cloneTargetOfTarget((NamedTarget)$TargetScope::target));
	 }}
   (
        arr=arrayAccessSuffixRubbish {retval.element = arr.element;}
    |   arg=argumentsSuffixRubbish {retval.element = arg.element;}
    |   '.' 'class' {retval.element = new ClassLiteral(createTypeReference((NamedTarget)$TargetScope::target));}
    |   '.' gen=explicitGenericInvocation {retval.element = gen.element;}
    |   '.' supkw='super' 
             {$TargetScope::target = new SuperTarget($TargetScope::target);
             setKeyword($TargetScope::target,supkw);
              setLocation($TargetScope::target, $TargetScope::start,supkw);
             }
             supsuf=superSuffix {retval.element = supsuf.element;}
    |   '.' newkw='new' in=innerCreator {retval.element = in.element;setKeyword(retval.element,newkw);}
   )?
	;

// NEEDS_TARGET
argumentsSuffixRubbish returns [RegularMethodInvocation element]
// the last part of target is the method name
	:	args=arguments 
	        {String name = ((NamedTarget)$TargetScope::target).getName();
	         $TargetScope::target = ((NamedTarget)$TargetScope::target).getTarget(); //chop off head
	         retval.element = invocation(name, $TargetScope::target);
	         retval.element.addAllArguments(args.element);
	         setLocation(retval.element, $TargetScope::start, args.stop);
	        }
	;

// NEEDS_TARGET
arrayAccessSuffixRubbish returns [Expression element]
@after{setLocation(retval.element, $TargetScope::start, retval.stop);}
	:	{retval.element = new ArrayAccessExpression(new NamedTargetExpression(((NamedTarget)$TargetScope::target).getName(),cloneTargetOfTarget((NamedTarget)$TargetScope::target)));} 
	        (open='[' arrex=expression close=']' 
	          { FilledArrayIndex index = new FilledArrayIndex(arrex.element);
	           ((ArrayAccessExpression)retval.element).addIndex(index);
	           setLocation(index, open, close);
	          } 
	        )+ // can also be matched by selector, but do here

	;

// NEEDS_TARGET
creator returns [Expression element]
//GEN_METH
@init{int count = 0;}
    :   targs=nonWildcardTypeArguments tx=createdName restx=classCreatorRest
         {retval.element = new ConstructorInvocation((BasicJavaTypeReference)tx.element,$TargetScope::target);
          ((ConstructorInvocation)retval.element).setBody(restx.element.body());
          ((ConstructorInvocation)retval.element).addAllArguments(restx.element.arguments());
          ((ConstructorInvocation)retval.element).addAllTypeArguments(targs.element);
         }
    |    tt=createdName  ('[' ']' {count++;})+ init=arrayInitializer 
        {if(count > 0) {tt.element = new ArrayTypeReference(tt.element,count);} 
         retval.element = new ArrayCreationExpression(tt.element);
         ((ArrayCreationExpression)retval.element).setInitializer(init.element);}
    |    ttt=createdName  {retval.element = new ArrayCreationExpression(ttt.element);} 
          ('[' exx=expression ']' {((ArrayCreationExpression)retval.element).addDimensionInitializer(new FilledArrayIndex(exx.element));})+ 
            ('[' ']' {((ArrayCreationExpression)retval.element).addDimensionInitializer(new EmptyArrayIndex(1));})*
    |   t=createdName rest=classCreatorRest 
         {retval.element = new ConstructorInvocation((BasicJavaTypeReference)t.element,$TargetScope::target);
          ((ConstructorInvocation)retval.element).setBody(rest.element.body());
          ((ConstructorInvocation)retval.element).addAllArguments(rest.element.arguments());
         }
           
    ;

createdName returns [JavaTypeReference element]
    :   cd=classOrInterfaceType {retval.element = cd.element;}
    |   prim=primitiveType {retval.element = prim.element;}
    ;

// NEEDS_TARGET
//GEN_METH
innerCreator returns [ConstructorInvocation element]
    :   (targs=nonWildcardTypeArguments)? 
        name=Identifier rest=classCreatorRest 
        {BasicJavaTypeReference tref = (BasicJavaTypeReference)typeRef($name.text);
         setLocation(tref,name,name);
         retval.element = new ConstructorInvocation(tref,$TargetScope::target);
         retval.element.setBody(rest.element.body());
         retval.element.addAllArguments(rest.element.arguments());
         if(targs != null) {
           retval.element.addAllTypeArguments(targs.element);
         }
        }
    ;


classCreatorRest returns [ClassCreatorRest element]
    :   args=arguments {retval.element = new ClassCreatorRest(args.element);}(body=classBody {retval.element.setBody(body.element);})?
    ;
    
// NEEDS_TARGET
explicitGenericInvocation returns [Expression element]
    :   targs=nonWildcardTypeArguments name=Identifier args=arguments
          {retval.element = invocation($name.text,$TargetScope::target);
           ((RegularMethodInvocation)retval.element).addAllArguments(args.element);
           ((RegularMethodInvocation)retval.element).addAllTypeArguments(targs.element);
          }
    ;
    
nonWildcardTypeArguments returns [List<ActualTypeArgument> element]
    :   '<' list=typeList {retval.element = new ArrayList<ActualTypeArgument>();for(TypeReference tref:list.element){retval.element.add(java().createBasicTypeArgument(tref));}}'>'
    ;
    
// NEEDS_TARGET
superSuffix returns [Expression element]
@init{
   Token start=null;
   Token stop=null;
}
    :   //arguments
        //|   
    '.' name=Identifier {retval.element = new NamedTargetExpression($name.text,cloneTarget($TargetScope::target));
                         start = name;
                         stop = name;} 
        (args=arguments
          {retval.element = invocation($name.text,$TargetScope::target);
          ((RegularMethodInvocation)retval.element).addAllArguments(args.element);
          stop = args.stop;
          }
        )?
        {setLocation(retval.element,start,stop);}
    ;

arguments returns [List<ActualArgument> element]
@init{retval.element = new ArrayList<ActualArgument>();}
    :   '(' (list=expressionList { for(Expression ex: list.element) {retval.element.add(new ActualArgument(ex));}} )? ')'
    ;

// LEXER

