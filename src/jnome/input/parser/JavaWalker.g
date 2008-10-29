tree grammar JavaWalker;
options
{
	ASTLabelType = CommonTree ;
	tokenVocab = Java ;
}

/*
 * This ends up at the beginning of the parser class.
 * Set the namespace of the parser, and provide imports for the Chameleon elements.
 */ 
@treeparser::header {
package jnome.input.parser;

import chameleon.core.MetamodelException;

import chameleon.core.element.ChameleonProgrammerException;

import chameleon.core.compilationunit.CompilationUnit;

import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.RootNamespace;

import chameleon.core.context.ContextFactory;

import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.namespacepart.Import;
import chameleon.core.namespacepart.TypeImport;
import chameleon.core.namespacepart.DemandImport;

import chameleon.core.language.Language;

import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;

import jnome.core.language.Java;

import jnome.core.type.JavaTypeReference;
}

@members {
  /* Sandbox the model until parsing is done. This way,
   * we are certain that the model will not throw MetamodelExceptions 
   * because the model contains multiple elements with the same name. This
   * can only happen if the model is corrupted by the parser or by methods of the
   * metamodel invoked by the parser. We convert these exceptions to ChameleonProgrammerExceptions
   */ 
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


compilationUnit returns [CompilationUnit cu = new CompilationUnit(new NamespacePart(language().defaultNamespace()))]
    :   ('@')=> annotations //TODO: add support for annotations
        (   packageDeclaration{processPackageDeclaration(cu,np);} (imp=importDeclaration{processImport(cu,imp);})* (type=typeDeclaration {processType(cu,type);})*
        |   classOrInterfaceDeclaration (type=typeDeclaration {processType(cu,type);})*
       )
    |
       
       np=packageDeclaration?{processPackageDeclaration(cu,np);}
       (imp=importDeclaration{processImport(cu,imp);})* 
       (type=typeDeclaration {processType(cu,type);})*
    ;

packageDeclaration returns [NamespacePart np=null]
    :   'package' qn=qualifiedName ';' 
        {try{
           np = new NamespacePart(root.getOrCreateNamespace(qn));
         }
         catch(MetamodelException exc) {
           //this should not happen, something is wrong with the tree parser
           throw new ChameleonProgrammerException(exc);
         }
        }
    ;
    
importDeclaration returns [Import imp=null]
    :   'import' st='static'? qn=qualifiedName star=('.' '*')? ';'
        {
         if(star==null) {
           imp = new TypeImport(typeRef(qn));
           // type import
         } else {
           // demand import
           imp = new DemandImport(typeRef(qn));
         }
        }
    ;
    
typeDeclaration returns [Type t=null]
    :  classOrInterfaceDeclaration
    |  ';'
    ;

classOrInterfaceDeclaration
    :  'qqqq' // classOrInterfaceModifiers (classDeclaration | interfaceDeclaration)
    ;


qualifiedName returns [String result=null]
    :   Identifier ('.' Identifier)*
    ;

// ANNOTATIONS

annotations
    :   annotation+
    ;

annotation
    :   '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
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
    : 'error'//   conditionalExpression
     //|   annotation
     //|   elementValueArrayInitializer
    ;
