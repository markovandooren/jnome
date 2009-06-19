
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
grammar Java;
options {backtrack=true; memoize=true;output=AST;}

scope MethodScope {
  Method method;
}

@parser::header {
package jnome.input.parser;

import chameleon.core.MetamodelException;

import chameleon.core.context.ContextFactory;

import chameleon.core.compilationunit.CompilationUnit;

import chameleon.core.declaration.SimpleNameSignature;

import chameleon.core.element.ChameleonProgrammerException;

import chameleon.core.expression.ActualArgument;
import chameleon.core.expression.Expression;
import chameleon.core.expression.Invocation;
import chameleon.core.expression.Literal;

import chameleon.core.language.Language;

import chameleon.core.member.Member;

import chameleon.core.method.Method;
import chameleon.core.method.Implementation;
import chameleon.core.method.RegularImplementation;

import chameleon.core.method.exception.ExceptionClause;
import chameleon.core.method.exception.TypeExceptionDeclaration;

import chameleon.core.modifier.Modifier;

import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;

import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.namespacepart.Import;
import chameleon.core.namespacepart.TypeImport;
import chameleon.core.namespacepart.DemandImport;

import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;

import chameleon.core.type.ClassBody;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.TypeElement;

import chameleon.core.type.generics.GenericParameter;
import chameleon.core.type.generics.GenericArgument;
import chameleon.core.type.generics.TypeConstraint;
import chameleon.core.type.generics.ExtendsConstraint;

import chameleon.core.type.inheritance.SubtypeRelation;

import chameleon.core.variable.Variable;
import chameleon.core.variable.FormalParameter;

import chameleon.support.expression.RegularLiteral;
import chameleon.support.expression.NullLiteral;
import chameleon.support.expression.ThisConstructorDelegation;
import chameleon.support.expression.SuperConstructorDelegation;

import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.SimpleNameMethodHeader;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;

import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Protected;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Enum;
import chameleon.support.modifier.Interface;

import chameleon.support.statement.StatementExpression;
import chameleon.support.statement.LocalClassStatement;
import chameleon.support.statement.AssertStatement;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ForStatement;
import chameleon.support.statement.ForControl;
import chameleon.support.statement.ForInit;
import chameleon.support.statement.SimpleForControl;
import chameleon.support.statement.EnhancedForControl;
import chameleon.support.statement.StatementExprList;

import chameleon.support.type.EmptyTypeElement;
import chameleon.support.type.StaticInitializer;

import chameleon.support.variable.VariableDeclaration;
import chameleon.support.variable.LocalVariableDeclarator;
import chameleon.support.variable.VariableDeclarator;

import jnome.core.expression.ArrayInitializer;

import jnome.core.language.Java;

import jnome.core.modifier.StrictFP;
import jnome.core.modifier.Transient;
import jnome.core.modifier.Volatile;
import jnome.core.modifier.Synchronized;

import jnome.core.type.JavaTypeReference;

import jnome.core.enumeration.EnumConstant;

import jnome.core.variable.JavaVariableDeclaration;
import jnome.core.variable.MultiFormalParameter;

import java.util.List;
import java.util.ArrayList;


}
@lexer::header {
package jnome.input.parser;
}

@lexer::members {
  protected boolean enumIsKeyword = true;
  protected boolean assertIsKeyword = true;
}

@parser::members {

  public static class StupidVariableDeclaratorId {
       public StupidVariableDeclaratorId(String name, int dimension) {
         _name = name;
         _dimension = dimension;
       }
       private final String _name;
       private final int _dimension;
       
       public String name() {
         return _name;
       }
       
       public int dimension() {
         return _dimension;
       }
  }
  
  Language lang;
  
  public Language language() {
    return lang;
  }
  
  public void setLanguage(Java language) {
    lang = language;
  }
  
  RootNamespace root = lang.defaultNamespace();
  ContextFactory contextFactory = root.language().contextFactory();

  public Namespace getDefaultNamespace() {
    return root;
  }

  public void processImport(CompilationUnit cu, Import imp){
    if(imp instanceof TypeImport){cu.getDefaultNamespacePart().addImport((TypeImport)imp);}
    else if(imp instanceof DemandImport){cu.getDefaultNamespacePart().addImport((DemandImport)imp);}
  }
  public void processType(CompilationUnit cu, Type type){
    cu.getDefaultNamespacePart().addType(type);
  }
  public void processPackageDeclaration(CompilationUnit cu, NamespacePart np){
    if(np!=null){
      cu.getDefaultNamespacePart().addNamespacePart(np);
    }
  }
  
  public TypeReference typeRef(String qn) {
    return new JavaTypeReference(qn);
  }

}

// starting point for parsing a java file
/* The annotations are separated out to make parsing faster, but must be associated with
   a packageDeclaration or a typeDeclaration (and not an empty one). */
compilationUnit returns [CompilationUnit element] 
@init{ retval.element = new CompilationUnit(new NamespacePart(language().defaultNamespace()));}
    :    annotations
        (   np=packageDeclaration{processPackageDeclaration(retval.element,np.element);} (imp=importDeclaration{processImport(retval.element,imp.element);})* (typech=typeDeclaration{processType(retval.element,typech.element);})*
        |   cd=classOrInterfaceDeclaration{processType(retval.element,cd.element);} (typech=typeDeclaration{processType(retval.element,typech.element);})*
        )
    |   (np=packageDeclaration{processPackageDeclaration(retval.element,np.element);})? (imp=importDeclaration{processImport(retval.element,imp.element);})* (typech=typeDeclaration{processType(retval.element,typech.element);})*
    ;

packageDeclaration returns [NamespacePart element]
    :   'package' qn=qualifiedName ';'
         {try{
           retval.element = new NamespacePart(root.getOrCreateNamespace($qn.text));
         }
         catch(MetamodelException exc) {
           //this should not happen, something is wrong with the tree parser
           throw new ChameleonProgrammerException(exc);
         }
        }
    ;
    
importDeclaration returns [Import element]
    :   'import' st='static'? qn=qualifiedName star=('.' '*')? ';'
    {
         if(star==null) {
           retval.element = new TypeImport(typeRef($qn.text));
           // type import
         } else {
           // demand import
           retval.element = new DemandImport(typeRef($qn.text));
         }
    }
    ;

    
typeDeclaration returns [Type element]
    :   cd=classOrInterfaceDeclaration {retval.element = cd.element;}
    |   ';'
    ;

//DONE

    
classOrInterfaceDeclaration returns [Type element]
    :   mods=classOrInterfaceModifiers (cd=classDeclaration {retval.element=cd.element;} | id=interfaceDeclaration {retval.element=id.element;}) 
        {for(Modifier mod:mods.element) {
          retval.element.addModifier(mod);
        }}
    ;
    
classOrInterfaceModifiers returns [List<Modifier> element]
@init {retval.element = new ArrayList<Modifier>();}
    :   (mod=classOrInterfaceModifier{retval.element.add(mod.element);})* 
    ;

classOrInterfaceModifier returns [Modifier element]
    :   annotation   // class or interface
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
    :   cd=normalClassDeclaration { retval.element = cd.element;}
    |   ed=enumDeclaration {retval.element = ed.element;}
    ;
    
normalClassDeclaration returns [RegularType element]
    :   'class' name=Identifier {retval.element = new RegularType(new SimpleNameSignature($name.text));} (params=typeParameters{for(GenericParameter par: params.element){retval.element.add(par);}})?
        ('extends' sc=type{retval.element.addInheritanceRelation(new SubtypeRelation(sc.element));})?
        ('implements' trefs=typeList {for(TypeReference ref: trefs.element){retval.element.addInheritanceRelation(new SubtypeRelation(ref));} } )?
        body=classBody {retval.element.setBody(body.element);}
    ;
    
typeParameters returns [List<GenericParameter> element]
@init{retval.element = new ArrayList<GenericParameter>();}
    :   '<' par=typeParameter{retval.element.add(par.element);} (',' par=typeParameter{retval.element.add(par.element);})* '>'
    ;

typeParameter returns [GenericParameter element]
    :   name=Identifier{retval.element = new GenericParameter(new SimpleNameSignature($name.text));} ('extends' bound=typeBound{retval.element.addConstraint(bound.element);})?
    ;
        
typeBound returns [ExtendsConstraint element]
@init{retval.element = new ExtendsConstraint();}
    :   tp=type {retval.element.add(tp.element);}('&' tpp=type {retval.element.add(tpp.element);})*
    ;

enumDeclaration returns [RegularType element]
scope{
  Type enumType;
}
    :   ENUM name=Identifier {retval.element = new RegularType(new SimpleNameSignature($name.text)); retval.element.addModifier(new Enum()); $enumDeclaration::enumType=retval.element;}('implements' trefs=typeList {for(TypeReference ref: trefs.element){retval.element.addInheritanceRelation(new SubtypeRelation(ref));} } )? body=enumBody {retval.element.setBody(body.element);}
    ;

// Nothing must be done here
enumBody returns [ClassBody element]
    :   '{' (csts=enumConstants{for(EnumConstant el: csts.element){retval.element.add(el);}})? ','? (decls=enumBodyDeclarations {for(TypeElement el: decls.element){retval.element.add(el);}})? '}'
    ;

enumConstants returns [List<EnumConstant> element]
    :   ct=enumConstant {retval.element = new ArrayList<EnumConstant>(); retval.element.add(ct.element);} (',' cst=enumConstant{$enumDeclaration::enumType.add(cst.element);})*
    ;
    
enumConstant returns [EnumConstant element]
    :   annotations? name=Identifier {retval.element = new EnumConstant(new SimpleNameSignature($name.text));} (args=arguments {retval.element.addAllParameters(args.element);})? (body=classBody {retval.element.setBody(body.element);})?
    ;
    
enumBodyDeclarations returns [List<TypeElement> element]
    :   ';' {retval.element= new ArrayList<TypeElement>();} (decl=classBodyDeclaration {retval.element.add(decl.element);})*
    ;
    
interfaceDeclaration returns [Type element]
    :   id=normalInterfaceDeclaration {retval.element = id.element;}
    |   ad=annotationTypeDeclaration {retval.element = ad.element;}
    ;
    
normalInterfaceDeclaration returns [RegularType element]
    :   'interface' name=Identifier {retval.element = new RegularType(new SimpleNameSignature($name.text)); retval.element.addModifier(new Interface());} (params=typeParameters{for(GenericParameter par: params.element){retval.element.add(par);}})? ('extends' trefs=typeList {for(TypeReference ref: trefs.element){retval.element.addInheritanceRelation(new SubtypeRelation(ref));} } )? body=classBody {retval.element.setBody(body.element);}
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
    :   ';' {retval.element = new EmptyTypeElement();}
    |   'static'? bl=block {retval.element = new StaticInitializer(bl.element);}
    |   mods=modifiers decl=memberDecl {retval.element = decl.element; retval.element.addModifiers(mods.element);}
    ;
    
memberDecl returns [TypeElement element]
    :   gen=genericMethodOrConstructorDecl {retval.element = gen.element;}
    |   mem=memberDeclaration {retval.element = mem.element;}
    |   vmd=voidMethodDeclaration {retval.element = vmd.element;}
    |   cs=constructorDeclaration {retval.element = cs.element;}
    |   id=interfaceDeclaration {retval.element=id.element;}
    |   cd=classDeclaration {retval.element=cd.element;}
    ;
    
voidMethodDeclaration returns [Method element]
scope MethodScope;
    	: 'void' methodname=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($methodname.text), new JavaTypeReference("void")); $MethodScope::method = retval.element;} voidMethodDeclaratorRest	
    	;
    	
constructorDeclaration returns [Method element]
scope MethodScope;
        : consname=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($consname.text), new JavaTypeReference($consname.text)); $MethodScope::method = retval.element;} constructorDeclaratorRest
	;
    
memberDeclaration returns [TypeElement element]
    :   method=methodDeclaration {retval.element=method.element;}
    |   field=fieldDeclaration {retval.element=field.element;}
    ;

//TODO parse generics parameters for methods and constructors
genericMethodOrConstructorDecl returns [Member element]
    :   params=typeParameters rest=genericMethodOrConstructorRest {retval.element = rest.element;}
    ;
    
genericMethodOrConstructorRest returns [Method element]
scope MethodScope;
@init{TypeReference tref = null;}
    :   (t=type {tref=t.element;}| 'void' {tref = new JavaTypeReference("void");}) name=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($name.text),tref); $MethodScope::method = retval.element;} methodDeclaratorRest
    |   name=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($name.text),new JavaTypeReference($name.text)); $MethodScope::method = retval.element;} constructorDeclaratorRest
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
    |   decl3=interfaceDeclaration {retval.element = decl3.element;}
    |   decl4=classDeclaration {retval.element = decl4.element;}
    ;
    
voidInterfaceMethodDeclaration  returns [Method element]
scope MethodScope;
    	: 'void' methodname=Identifier {retval.element = new NormalMethod(new SimpleNameMethodHeader($methodname.text), new JavaTypeReference("void")); $MethodScope::method = retval.element;} voidInterfaceMethodDeclaratorRest
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
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addParameter(par);}} ('[' ']' {count++;})* {((JavaTypeReference)$MethodScope::method.getReturnTypeReference()).setArrayDimension(count);}
        ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(new JavaTypeReference(name)));}})?
        (   body=methodBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
        |   ';' {$MethodScope::method.setImplementation(null);}
        )
    ;
    
voidMethodDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addParameter(par);}}
         ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(new JavaTypeReference(name)));}})?
        (   body=methodBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
        |   ';' {$MethodScope::method.setImplementation(null);}
        )
    ;
    
interfaceMethodDeclaratorRest
@init{int count = 0;}
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addParameter(par);}}
       ('[' ']' {count++;})* {((JavaTypeReference)$MethodScope::method.getReturnTypeReference()).setArrayDimension(count);}
       ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(new JavaTypeReference(name)));}})? ';'
    ;
    
interfaceGenericMethodDecl returns [TypeElement element]
    :   typeParameters (type | 'void') Identifier
        interfaceMethodDeclaratorRest
    ;
    
voidInterfaceMethodDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addParameter(par);}}
     ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(new JavaTypeReference(name)));}})?
      ';'
    ;
    
constructorDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addParameter(par);}} 
    ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(new JavaTypeReference(name)));}})?
     body=constructorBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
    ;

constantDeclarator returns [JavaVariableDeclaration element]
@init{int count = 0;}
    :   name=Identifier (('[' ']' {count++;})* '=' init=variableInitializer) 
       {retval.element = new JavaVariableDeclaration($name.text); retval.element.setArrayDimension(count); retval.element.setExpression(init.element);}
    ;
    
variableDeclarators returns [List<VariableDeclaration> element]
    :   decl=variableDeclarator {retval.element = new ArrayList<VariableDeclaration>(); retval.element.add(decl.element);}(',' decll=variableDeclarator {retval.element.add(decll.element);})*
    ;

variableDeclarator returns [JavaVariableDeclaration element]
    :   id=variableDeclaratorId {retval.element = new JavaVariableDeclaration(id.element.name()); retval.element.setArrayDimension(id.element.dimension());} ('=' init=variableInitializer {retval.element.setExpression(init.element);})?
    ;
    

    
variableDeclaratorId returns [StupidVariableDeclaratorId element]
@init{int count = 0;}
    :   name=Identifier ('[' ']' {count++;})* { retval.element = new StupidVariableDeclaratorId($name.text, count);}
    ;

variableInitializer returns [Expression element]
    :   init=arrayInitializer {retval.element = init.element;}
    |   expr=expression {retval.element = expr.element;}
    ;
        
arrayInitializer returns [ArrayInitializer element]
    :   '{' {retval.element = new ArrayInitializer();} (init=variableInitializer {retval.element.addInitializer(init.element);}(',' initt=variableInitializer{retval.element.addInitializer(initt.element);})* (',')? )? '}'
    ;

modifier returns [Modifier element]
    :   annotation
    |   mod=classOrInterfaceModifier {retval.element = mod.element;}
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
	:	cd=classOrInterfaceType ('[' ']' {dimension++;})* {retval.element = cd.element.toArray(dimension);}
	|	pt=primitiveType ('[' ']'{dimension++;})* {retval.element = pt.element.toArray(dimension);}
	;

classOrInterfaceType returns [JavaTypeReference element]
	:	name=Identifier {retval.element = new JavaTypeReference($name.text);} (args=typeArguments {retval.element.addAllArguments(args.element);})?  ('.' namex=Identifier {retval.element = new JavaTypeReference(retval.element,$namex.text);} (argsx=typeArguments {retval.element.addAllArguments(argsx.element);})? )*
	;

primitiveType returns [JavaTypeReference element]
    :   'boolean' {retval.element = new JavaTypeReference("boolean");}
    |   'char' {retval.element = new JavaTypeReference("char");}
    |   'byte' {retval.element = new JavaTypeReference("byte");}
    |   'short' {retval.element = new JavaTypeReference("short");}
    |   'int' {retval.element = new JavaTypeReference("int");}
    |   'long' {retval.element = new JavaTypeReference("long");}
    |   'float' {retval.element = new JavaTypeReference("float");}
    |   'double' {retval.element = new JavaTypeReference("double");}
    ;

variableModifier returns [Modifier element]
    :   'final' {retval.element = new Final();}
    |   annotation
    ;

typeArguments returns [List<GenericArgument> element]
    :   '<' {retval.element = new ArrayList<GenericArgument>();} arg=typeArgument {retval.element.add(arg.element);}(',' argx=typeArgument {retval.element.add(argx.element);})* '>'
    ;
    
typeArgument returns [GenericArgument element]
    :   t=type {retval.element = new GenericArgument(t.element);}
    |   '?' {throw new Error("We do not currently support wildcards.");} (('extends' | 'super') type)?
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
        {if(retval.element == null) {retval.element=new ArrayList<FormalParameter>();}
         FormalParameter param = new FormalParameter(new SimpleNameSignature(idd.element.name()),tt.element.toArray(idd.element.dimension()));
         param.addAllModifiers(modss.element);
         retval.element.add(param);}
    |   modss=variableModifiers tt=type '...' idd=variableDeclaratorId 
        {retval.element = new ArrayList<FormalParameter>(); 
         FormalParameter param = new MultiFormalParameter(new SimpleNameSignature(idd.element.name()),tt.element.toArray(idd.element.dimension()));
         param.addAllModifiers(modss.element);
         retval.element.add(param);}
    ;
    
    
methodBody returns [Block element]
    :   b=block {retval.element = b.element;}
    ;

constructorBody returns [Block element]
    :   '{' {retval.element = new Block();} (inv=explicitConstructorInvocation {retval.element.addStatement(new StatementExpression(inv.element));})? blockStatement* '}'
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
    |   fl=FloatingPointLiteral {retval.element=new RegularLiteral(new JavaTypeReference("float"),$fl.text);}
    |   charl=CharacterLiteral {retval.element=new RegularLiteral(new JavaTypeReference("char"),$charl.text);}
    |   strl=StringLiteral {retval.element=new RegularLiteral(new JavaTypeReference("java.lang.String"),$strl.text);}
    |   booll=booleanLiteral {retval.element=booll.element;}
    |   'null' {retval.element = new NullLiteral();}
    ;

integerLiteral returns [Literal element]
    :   hexl=HexLiteral {retval.element=new RegularLiteral(new JavaTypeReference("int"),$hexl.text);}
    |   octl=OctalLiteral {retval.element=new RegularLiteral(new JavaTypeReference("int"),$octl.text);}
    |   decl=DecimalLiteral {retval.element=new RegularLiteral(new JavaTypeReference("int"),$decl.text);}
    ;

booleanLiteral returns [Literal element]
    :   'true' {retval.element = new RegularLiteral(new JavaTypeReference("boolean"),"true");}
    |   'false' {retval.element = new RegularLiteral(new JavaTypeReference("boolean"),"false");}
    ;

// ANNOTATIONS

annotations
    :   annotation+
    ;

annotation
    :   {throw new Error("annotations are not yet supported");} '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
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
    
annotationTypeDeclaration returns [Type element]
    :   '@' 'interface' Identifier annotationTypeBody
    ;
    
annotationTypeBody
    :   '{' (annotationTypeElementDeclaration)* '}'
    ;
    
annotationTypeElementDeclaration
    :   modifiers annotationTypeElementRest
    ;
    
annotationTypeElementRest
    :   type annotationMethodOrConstantRest ';'
    |   normalClassDeclaration ';'?
    |   normalInterfaceDeclaration ';'?
    |   enumDeclaration ';'?
    |   annotationTypeDeclaration ';'?
    ;
    
annotationMethodOrConstantRest
    :   annotationMethodRest
    |   annotationConstantRest
    ;
    
annotationMethodRest
    :   Identifier '(' ')' defaultValue?
    ;
    
annotationConstantRest
    :   variableDeclarators
    ;
    
defaultValue
    :   'default' elementValue
    ;

// STATEMENTS / BLOCKS

block returns [Block element]
    :   '{' {retval.element = new Block();} (stat=blockStatement {retval.element.addStatement(stat.element);})* '}'
    ;
    
blockStatement returns [Statement element]
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
    : bl=block {retval.element = bl.element;}
    |   ASSERT asexpr=expression {retval.element=new AssertStatement(asexpr.element);}(':' asexprx=expression {((AssertStatement)retval.element).setMessageExpression(asexprx.element);})? ';'
    |   'if' ifexpr=parExpression ifif=statement (options {k=1;}:'else' ifelse=statement)? {retval.element=new IfThenElseStatement(ifexpr.element, ifif.element,ifelse.element);}
    |   'for' '(' forc=forControl ')' forstat=statement {retval.element = new ForStatement(forc.element,forstat.element);}
    |   'while' parExpression statement
    |   'do' statement 'while' parExpression ';'
    |   'try' block
        ( catches 'finally' block
        | catches
        |   'finally' block
        )
    |   'switch' parExpression '{' switchBlockStatementGroups '}'
    |   'synchronized' parExpression block
    |   'return' expression? ';'
    |   'throw' expression ';'
    |   'break' Identifier? ';'
    |   'continue' Identifier? ';'
    |   ';' 
    |   statementExpression ';'
    |   Identifier ':' statement
    ;
    
catches
    :   catchClause (catchClause)*
    ;
    
catchClause
    :   'catch' '(' formalParameter ')' block
    ;

formalParameter
    :   variableModifiers type variableDeclaratorId
    ;
        
switchBlockStatementGroups
    :   (switchBlockStatementGroup)*
    ;
    
/* The change here (switchLabel -> switchLabel+) technically makes this grammar
   ambiguous; but with appropriately greedy parsing it yields the most
   appropriate AST, one in which each group, except possibly the last one, has
   labels and statements. */
switchBlockStatementGroup
    :   switchLabel+ blockStatement*
    ;
    
switchLabel
    :   'case' constantExpression ':'
    |   'case' enumConstantName ':'
    |   'default' ':'
    ;
    
forControl returns [ForControl element]
options {k=3;} // be efficient for common case: for (ID ID : ID) ...
    :   enh=enhancedForControl {retval.element=enh.element;}
    |   in=forInit? ';' e=expression? ';' u=forUpdate? {retval.element = new SimpleForControl(in.element,e.element,u.element);}
    ;

forInit returns [ForInit element]
    :   local=localVariableDeclaration {retval.element=local.element;}
    |   el=expressionList {retval.element = new StatementExprList(); for(Expression expr: el.element){((StatementExprList)retval.element).addStatement(new StatementExpression(expr));};}
    ;
    
enhancedForControl returns [ForControl element]
    :   local=localVariableDeclaration ':' ex=expression {retval.element = new EnhancedForControl(local.element, ex.element);}
    ;

forUpdate returns [StatementExprList element]
    :   el=expressionList {retval.element = new StatementExprList(); for(Expression expr: el.element){((StatementExprList)retval.element).addStatement(new StatementExpression(expr));};}
    ;

// EXPRESSIONS

parExpression returns [Expression element]
    :   '(' expr=expression {retval.element = expr.element;} ')'
    ;
    
expressionList returns [List<Expression> element]
    :   {retval.element = new ArrayList<Expression>();} e=expression {retval.element.add(e.element);}(',' ex=expression {retval.element.add(ex.element);})*
    ;

statementExpression returns [Expression element]
    :   expression
    ;
    
constantExpression
    :   expression
    ;
    
expression returns [Expression element]
    :   conditionalExpression (assignmentOperator expression)?
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

conditionalExpression
    :   conditionalOrExpression ( '?' expression ':' expression )?
    ;

conditionalOrExpression
    :   conditionalAndExpression ( '||' conditionalAndExpression )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression ( '&&' inclusiveOrExpression )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression ( '|' exclusiveOrExpression )*
    ;

exclusiveOrExpression
    :   andExpression ( '^' andExpression )*
    ;

andExpression
    :   equalityExpression ( '&' equalityExpression )*
    ;

equalityExpression
    :   instanceOfExpression ( ('==' | '!=') instanceOfExpression )*
    ;

instanceOfExpression
    :   relationalExpression ('instanceof' type)?
    ;

relationalExpression
    :   shiftExpression ( relationalOp shiftExpression )*
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

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
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


additiveExpression
    :   multiplicativeExpression ( ('+' | '-') multiplicativeExpression )*
    ;

multiplicativeExpression
    :   unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
    ;
    
unaryExpression
    :   '+' unaryExpression
    |   '-' unaryExpression
    |   '++' unaryExpression
    |   '--' unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   '~' unaryExpression
    |   '!' unaryExpression
    |   castExpression
    |   primary selector* ('++'|'--')?
    ;

castExpression
    :  '(' primitiveType ')' unaryExpression
    |  '(' (type | expression) ')' unaryExpressionNotPlusMinus
    ;

primary returns [Expression element]
    :   parExpression
    |   'this' ('.' Identifier)* identifierSuffix?
    |   'super' superSuffix
    |   literal
    |   'new' creator
    |   Identifier ('.' Identifier)* identifierSuffix?
    |   primitiveType ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
    ;

identifierSuffix
    :   ('[' ']')+ '.' 'class'
    |   ('[' expression ']')+ // can also be matched by selector, but do here
    |   arguments
    |   '.' 'class'
    |   '.' explicitGenericInvocation
    |   '.' 'this'
    |   '.' 'super' arguments
    |   '.' 'new' innerCreator
    ;

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :   classOrInterfaceType
    |   primitiveType
    ;
    
innerCreator
    :   nonWildcardTypeArguments? Identifier classCreatorRest
    ;

arrayCreatorRest
    :   '['
        (   ']' ('[' ']')* arrayInitializer
        |   expression ']' ('[' expression ']')* ('[' ']')*
        )
    ;

classCreatorRest
    :   arguments classBody?
    ;
    
explicitGenericInvocation
    :   nonWildcardTypeArguments Identifier arguments
    ;
    
nonWildcardTypeArguments
    :   '<' typeList '>'
    ;
    
selector
    :   '.' Identifier arguments?
    |   '.' 'this'
    |   '.' 'super' superSuffix
    |   '.' 'new' innerCreator
    |   '[' expression ']'
    ;
    
superSuffix
    :   arguments
    |   '.' Identifier arguments?
    ;

arguments returns [List<ActualArgument> element]
    :   '(' expressionList? ')'
    ;

// LEXER

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    ;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

ENUM:   'enum' {if (!enumIsKeyword) $type=Identifier;}
    ;
    
ASSERT
    :   'assert' {if (!assertIsKeyword) $type=Identifier;}
    ;
    
Identifier 
    :   Letter (Letter|JavaIDDigit)*
    ;

/**I found this char range in JavaCC's grammar, but Letter and Digit overlap.
   Still works, but...
 */
fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;

WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;
