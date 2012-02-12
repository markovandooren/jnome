grammar Java;
options {
  backtrack=true; 
  memoize=true;
  output=AST;
  superClass = ChameleonParser;
import JavaL,JavaP;
@members{
  public void setLanguage(Language language) {
    gJavaP.setLanguage(language);
  public Language language() {
    return gJavaP.language();
  public CompilationUnit getCompilationUnit() {
    return gJavaP.getCompilationUnit();
  public void setCompilationUnit(CompilationUnit compilationUnit) {
    gJavaP.setCompilationUnit(compilationUnit);
  public Namespace getDefaultNamespace() {
    return gJavaP.getDefaultNamespace();
  public void setFactory(JavaFactory factory) {
    gJavaP.setFactory(factory);
  public JavaFactory factory() {
    return gJavaP.factory();
stuff	:	Identifier;
parser grammar JavaP;
options {
  backtrack=true; 
  memoize=true;
  output=AST;
  superClass = ChameleonParser;
scope MethodScope {
  Method method;
  Token start;
scope TargetScope {
  InvocationTarget target;
  Token start;
@parser::members {
  public RegularMethodInvocation invocation(String name, InvocationTarget target) {
    return new JavaMethodInvocation(name, target);
  private JavaFactory _javaFactory = new JavaFactory();
  public JavaFactory factory() {
    return _javaFactory;
  public void setFactory(JavaFactory factory) {
    _javaFactory = factory;
  public InvocationTarget cloneTargetOfTarget(NamedTarget target) {
    InvocationTarget result = null;
    if(target != null) {
      InvocationTarget targetOfTarget = target.getTarget();
      if(targetOfTarget != null) {
        result = targetOfTarget.clone();
    return result;
  public RegularType createType(SimpleNameSignature signature) {
     return factory().createRegularType(signature);
  public NormalMethod createNormalMethod(MethodHeader header) {
     return factory().createNormalMethod(header);
  public InvocationTarget cloneTarget(InvocationTarget target) {
    InvocationTarget result = null;
    if(target != null) {
        result = target.clone();
    return result;
  public static class ClassCreatorRest {
    public ClassCreatorRest(List<Expression> args) {
      _args = args; // NO ENCAPSULATION, BUT IT IS JUST THE PARSER.
    public List<Expression> arguments() {
      return _args;
    private List<Expression> _args;
    public void setBody(ClassBody body) {
      _body = body;
    public ClassBody body() {
      return _body;
    private ClassBody _body;
  public static class StupidVariableDeclaratorId {
       public StupidVariableDeclaratorId(String name, int dimension, CommonToken nameToken) {
         _name = name;
         _dimension = dimension;
         _token = nameToken;
       private final String _name;
       private final int _dimension;
       public CommonToken nameToken() {
         return _token;
       private CommonToken _token;
       public String name() {
         return _name;
       public int dimension() {
         return _dimension;
  public void processType(NamespacePart np, Type type){
    if(np == null) {throw new IllegalArgumentException("namespace part given to processType is null.");}
    if(type == null) {return;}  //throw new IllegalArgumentException("type given to processType is null.");}
    np.add(type);
    String fqn = type.getFullyQualifiedName();
    if(fqn != null) {
      if(type.nonMemberInheritanceRelations().isEmpty() && (! fqn.equals("java.lang.Object"))){
        type.addInheritanceRelation(new SubtypeRelation(createTypeReference(new NamespaceOrTypeReference("java.lang"),"Object")));
  public JavaTypeReference myToArray(JavaTypeReference ref, StupidVariableDeclaratorId id) {
    int dim = id.dimension(); 
    if(dim > 0) {
      return new ArrayTypeReference(ref,dim);
    } else {
      return ref;
  public void addNonTopLevelObjectInheritance(Type type) {
    if(type.nonMemberInheritanceRelations().isEmpty()){
      type.addInheritanceRelation(new SubtypeRelation(createTypeReference(new NamespaceOrTypeReference("java.lang"),"Object")));
  public JavaTypeReference typeRef(String qn) {
    return ((Java)language()).createTypeReference(qn);
  public JavaTypeReference createTypeReference(CrossReference<?, ? extends TargetDeclaration> target, String name) {
    return ((Java)language()).createTypeReference(target,name);
  public JavaTypeReference createTypeReference(CrossReference<?, ? extends TargetDeclaration> target, SimpleNameSignature signature) {
    return ((Java)language()).createTypeReference(target,signature);
  public JavaTypeReference createTypeReference(NamedTarget target) {
    return ((Java)language()).createTypeReference(target);
  public Java java() {
    return (Java)language();
identifierRule returns [String element]
    : id=Identifier {retval.element = $id.text;} 
compilationUnit returns [CompilationUnit element] 
@init{ 
NamespacePart npp = null;
retval.element = getCompilationUnit();
    :    annotations
        (   np=packageDeclaration
                {npp=np.element;
                 retval.element.add(npp);
                 npp.addImport(new DemandImport(new NamespaceReference("java.lang")));
            (imp=importDeclaration{npp.addImport(imp.element);})* 
            (typech=typeDeclaration
                {processType(npp,typech.element);
        |   cd=classOrInterfaceDeclaration
               {npp = new NamespacePart(language().defaultNamespace());
                retval.element.add(npp);
                npp.addImport(new DemandImport(new NamespaceReference("java.lang")));
                processType(npp,cd.element);
            (typech=typeDeclaration
               {processType(npp,typech.element);
    |   (np=packageDeclaration
              npp=np.element;
         if(npp == null) {
           npp = new NamespacePart(language().defaultNamespace());
         npp.addImport(new DemandImport(new NamespaceReference("java.lang")));
         retval.element.add(npp);
        (imp=importDeclaration
          {npp.addImport(imp.element);}
        (typech=typeDeclaration
           processType(npp,typech.element);
packageDeclaration returns [NamespacePart element]
    :   pkgkw='package' qn=qualifiedName ';'
         {try{
           retval.element = new NamespacePart(getDefaultNamespace().getOrCreateNamespace($qn.text));
           setKeyword(retval.element,pkgkw);
         catch(ModelException exc) {
           throw new ChameleonProgrammerException(exc);
importDeclaration returns [Import element]
    :   im='import' st='static'? qn=qualifiedName 
        {if(st == null) {
           retval.element = new TypeImport(typeRef($qn.text));
           setKeyword(retval.element,im);
         } else {
           retval.element = new SingleStaticImport(typeRef(Util.getAllButLastPart($qn.text)),Util.getLastPart($qn.text));
           setKeyword(retval.element,im);
            {retval.element = new DemandImport(new NamespaceReference($qn.text));
             setKeyword(retval.element,im);
typeDeclaration returns [Type element]
    :   cd=classOrInterfaceDeclaration {retval.element = cd.element;}
classOrInterfaceDeclaration returns [Type element]
@init{Token start = null; 
      Token end = null;}
@after{
  check_null(retval.element);
  setLocation(retval.element, start, end);
    :   mods=classOrInterfaceModifiers 
                {if(mods != null) {start=mods.start;}}
         (cd=classDeclaration 
                {retval.element=cd.element; end = cd.stop; if(mods == null) {start=cd.start;}} 
          | id=interfaceDeclaration 
                {retval.element=id.element; end=id.stop; if(mods == null) {start=id.start;}}) 
        {if(retval.element != null) {
           for(Modifier mod:mods.element) {
             retval.element.addModifier(mod);
classOrInterfaceModifiers returns [List<Modifier> element]
@init {retval.element = new ArrayList<Modifier>();}
    :   (mod=classOrInterfaceModifier{retval.element.add(mod.element);})* 
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
modifiers returns [List<Modifier> element]
@init {retval.element = new ArrayList<Modifier>();}
    :   (mod=modifier{retval.element.add(mod.element);})*
classDeclaration returns [Type element]
@after{check_null(retval.element);}
    :   cd=normalClassDeclaration { retval.element = cd.element;}
    |   ed=enumDeclaration {retval.element = ed.element;}
normalClassDeclaration returns [RegularType element]
    :   clkw='class' t=nameAndParams {retval.element=t.element;}
        (extkw='extends' sc=type 
            {SubtypeRelation extRelation = new SubtypeRelation(sc.element); 
             retval.element.addInheritanceRelation(extRelation);
             setKeyword(extRelation,extkw);
        (impkw='implements' trefs=typeList 
            {for(TypeReference ref: trefs.element) {
                SubtypeRelation rel = new SubtypeRelation(ref);
                retval.element.addInheritanceRelation(rel);
                rel.addModifier(new Implements());
                setKeyword(rel, impkw);
        body=classBody {
              if(body.element != null) {
                retval.element.body().addAll(body.element.elements());
         setKeyword(retval.element,clkw);
         setKeyword(retval.element,impkw);
nameAndParams returns [RegularType element]
    tt=createClassHereBecauseANTLRisAnnoying {retval.element=tt.element;} (params=typeParameters{for(FormalTypeParameter par: params.element){retval.element.addParameter(TypeParameter.class,par);}})?
createClassHereBecauseANTLRisAnnoying returns [RegularType element]
   :  name=identifierRule {retval.element = createType(new SimpleNameSignature($name.text)); setName(retval.element,name.start);}
typeParameters returns [List<FormalTypeParameter> element]
@init{retval.element = new ArrayList<FormalTypeParameter>();}
    :   '<' par=typeParameter{retval.element.add(par.element);} (',' par=typeParameter{retval.element.add(par.element);})* '>'
typeParameter returns [FormalTypeParameter element]
@init{
Token stop = null;
    :   name=identifierRule{retval.element = new FormalTypeParameter(new SimpleNameSignature($name.text)); stop=name.start;} (extkw='extends' bound=typeBound{retval.element.addConstraint(bound.element); stop=bound.stop;})?
        {setKeyword(retval.element,extkw);
         setLocation(retval.element, name.start, stop);
         setName(retval.element,name.start);
typeBound returns [ExtendsConstraint element]
@init{retval.element = new ExtendsConstraint();
JavaIntersectionTypeReference ref = null;
    :   tp=type 
         {retval.element.setTypeReference(tp.element);}
         ('&' tpp=type 
           if(ref == null) {
             ref = new JavaIntersectionTypeReference();
             ref.add(retval.element.typeReference());
             retval.element.setTypeReference(ref);
           ref.add(tpp.element);
enumDeclaration returns [RegularType element]
scope{
  Type enumType;
    :   ENUM name=identifierRule {retval.element = createType(new SimpleNameSignature($name.text)); 
                              retval.element.addModifier(new Enum()); 
                              $enumDeclaration::enumType=retval.element;
                              setName(retval.element,name.start);}
                  ('implements' trefs=typeList 
                         {for(TypeReference ref: trefs.element)
                                SubtypeRelation rel = new SubtypeRelation(ref);
                                retval.element.addInheritanceRelation(rel);
                                rel.addModifier(new Implements());
                   body=enumBody {retval.element.setBody(body.element);}
enumBody returns [ClassBody element]
@init{retval.element = new ClassBody();}
    :   '{' (csts=enumConstants
             for(EnumConstant el: csts.element) {
                retval.element.add(el);
            })? ','? (decls=enumBodyDeclarations {for(TypeElement el: decls.element){retval.element.add(el);}})? '}'
enumConstants returns [List<EnumConstant> element]
    :   ct=enumConstant {retval.element = new ArrayList<EnumConstant>(); retval.element.add(ct.element);} (',' cst=enumConstant{retval.element.add(cst.element);})*
enumConstant returns [EnumConstant element]
    :   annotations? name=identifierRule {retval.element = new EnumConstant(new SimpleNameSignature($name.text));} (args=arguments {retval.element.addAllParameters(args.element);})? (body=classBody {retval.element.setBody(body.element);})?
enumBodyDeclarations returns [List<TypeElement> element]
    :   ';' {retval.element= new ArrayList<TypeElement>();} (decl=classBodyDeclaration {retval.element.add(decl.element);})*
interfaceDeclaration returns [Type element]
@after{check_null(retval.element);}
    :   id=normalInterfaceDeclaration {retval.element = id.element;}
    |   ad=annotationTypeDeclaration {retval.element = ad.element;}
normalInterfaceDeclaration returns [RegularType element]
    :   ifkw='interface' name=identifierRule {retval.element = createType(new SimpleNameSignature($name.text)); 
                                          retval.element.addModifier(new Interface());
                                          setName(retval.element,name.start);} 
         (params=typeParameters{for(TypeParameter par: params.element){retval.element.addParameter(TypeParameter.class,par);}})? 
         (extkw='extends' trefs=typeList 
             for(TypeReference ref: trefs.element){
              retval.element.addInheritanceRelation(new SubtypeRelation(ref));
         body=classBody {retval.element.setBody(body.element);}
          setKeyword(retval.element,extkw);
          setKeyword(retval.element,ifkw);
typeList returns [List<TypeReference> element]
    :   tp=type {retval.element = new ArrayList<TypeReference>(); retval.element.add(tp.element);}(',' tpp=type {retval.element.add(tpp.element);})*
classBody returns [ClassBody element]
    :   '{' {retval.element = new ClassBody();} (decl=classBodyDeclaration {retval.element.add(decl.element);})* '}'
interfaceBody returns [ClassBody element]
    :   '{' {retval.element = new ClassBody();} 
            (decl=interfaceBodyDeclaration 
               {if(decl != null && decl.element != null) {retval.element.add(decl.element);}}
classBodyDeclaration returns [TypeElement element]
@init{
  Token start=null;
  Token stop=null;
@after{setLocation(retval.element, (CommonToken)start, (CommonToken)stop);}
    :   sckw=';' {retval.element = new EmptyTypeElement(); start=sckw; stop=sckw;}
    |   stkw='static'? bl=block {retval.element = new StaticInitializer(bl.element); start=stkw;stop=bl.stop;}
    |   mods=modifiers decl=memberDecl 
       {retval.element = decl.element;
        if(retval.element != null) { 
          retval.element.addModifiers(mods.element); start=mods.start; stop=decl.stop;
memberDecl returns [TypeElement element]
    :   gen=genericMethodOrConstructorDecl {retval.element = gen.element;}
    |   mem=memberDeclaration {retval.element = mem.element;}
    |   vmd=voidMethodDeclaration {retval.element = vmd.element;}
    |   cs=constructorDeclaration {retval.element = cs.element;}
    |   id=interfaceDeclaration {retval.element=id.element; addNonTopLevelObjectInheritance(id.element);}
    |   cd=classDeclaration {retval.element=cd.element; addNonTopLevelObjectInheritance(cd.element);}
voidMethodDeclaration returns [Method element]
scope MethodScope;
@after{setName(retval.element, methodname.start);}
    	: vt=voidType methodname=identifierRule 
    	 {retval.element = createNormalMethod(new SimpleNameMethodHeader($methodname.text, vt.element)); 
    	  $MethodScope::method = retval.element;
    	  setName(retval.element,methodname.start);
    	  } voidMethodDeclaratorRest	
voidType returns [JavaTypeReference element]
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop, "__PRIMITIVE");}
     	:	 'void' {retval.element=typeRef("void");}
constructorDeclaration returns [Method element]
scope MethodScope;
        : consname=identifierRule 
             retval.element = createNormalMethod(new SimpleNameMethodHeader($consname.text, typeRef($consname.text))); 
             retval.element.addModifier(new JavaConstructor());
             $MethodScope::method = retval.element;
             setName(retval.element, consname.start);
             constructorDeclaratorRest
memberDeclaration returns [TypeElement element]
    :   method=methodDeclaration {retval.element=method.element;}
    |   field=fieldDeclaration {retval.element=field.element;}
genericMethodOrConstructorDecl returns [Method element]
    :   params=typeParameters rest=genericMethodOrConstructorRest {retval.element = rest.element; retval.element.header().addAllTypeParameters(params.element);}
genericMethodOrConstructorRest returns [Method element]
scope MethodScope;
@init{TypeReference tref = null;}
@after{check_null(retval.element);}
    :   (t=type {tref=t.element;}| 'void' {tref = typeRef("void");}) name=identifierRule 
        {retval.element = createNormalMethod(new SimpleNameMethodHeader($name.text,tref)); 
         $MethodScope::method = retval.element;
         setName(retval.element,name.start);
        } methodDeclaratorRest
    |   name=identifierRule 
        {retval.element = createNormalMethod(new SimpleNameMethodHeader($name.text,typeRef($name.text))); 
         $MethodScope::method = retval.element;
         setName(retval.element,name.start);
        } constructorDeclaratorRest
methodDeclaration returns [Method element]
scope MethodScope;
    :   t=type name=identifierRule 
        {retval.element = createNormalMethod(new SimpleNameMethodHeader($name.text,t.element)); 
         $MethodScope::method = retval.element;
         setName(retval.element,name.start);
         } methodDeclaratorRest
fieldDeclaration returns [MemberVariableDeclarator element]
    :   ref=type {retval.element = new MemberVariableDeclarator(ref.element);} decls=variableDeclarators {for(VariableDeclaration decl: decls.element) {retval.element.add(decl);}} ';'
interfaceBodyDeclaration returns [TypeElement element]
    :   mods=modifiers decl=interfaceMemberDecl {retval.element = decl.element; for(Modifier mod: mods.element){retval.element.addModifier(mod);}}
interfaceMemberDecl returns [TypeElement element]
    :   decl=interfaceMethodOrFieldDecl {retval.element = decl.element;}
    |   decl2=interfaceGenericMethodDecl {retval.element = decl2.element;}
    |   decl5=voidInterfaceMethodDeclaration {retval.element = decl5.element;}
    |   decl3=interfaceDeclaration {retval.element = decl3.element; addNonTopLevelObjectInheritance(decl3.element);}
    |   decl4=classDeclaration {retval.element = decl4.element;  addNonTopLevelObjectInheritance(decl4.element);}
voidInterfaceMethodDeclaration  returns [Method element]
scope MethodScope;
    	: vt=voidType methodname=identifierRule 
    	  {retval.element = createNormalMethod(new SimpleNameMethodHeader($methodname.text, vt.element)); 
    	   $MethodScope::method = retval.element;
    	   setName(retval.element,methodname.start);
    	   } voidInterfaceMethodDeclaratorRest
interfaceMethodOrFieldDecl returns [TypeElement element]
    :   cst=interfaceConstant {retval.element = cst.element;}
    |   m=interfaceMethod {retval.element = m.element;}
interfaceConstant returns [MemberVariableDeclarator element]
    :   ref=type {retval.element = new MemberVariableDeclarator(ref.element);} decl=constantDeclarator {retval.element.add(decl.element);}(',' dec=constantDeclarator {retval.element.add(dec.element);})* ';'
interfaceMethod returns [Method element]
scope MethodScope;
	: tref=type methodname=identifierRule 
	   {retval.element = createNormalMethod(new SimpleNameMethodHeader($methodname.text, tref.element)); 
	    $MethodScope::method = retval.element;
	    setName(retval.element,methodname.start);
	   interfaceMethodDeclaratorRest
methodDeclaratorRest
@init{int count = 0;}
    :   pars=formalParameters 
           {for(FormalParameter par: pars.element){
               $MethodScope::method.header().addFormalParameter(par);
        ('[' ']' {count++;})* 
        {if(count > 0) {
           JavaTypeReference original = (JavaTypeReference)$MethodScope::method.returnTypeReference();
           $MethodScope::method.setReturnTypeReference(new ArrayTypeReference(original,count));
        (thrkw='throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
        (   body=methodBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
        |   ';' {$MethodScope::method.setImplementation(null);}
        {setKeyword($MethodScope::method,thrkw);}
voidMethodDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}}
         (thrkw='throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
        (   body=methodBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
        |   ';' {$MethodScope::method.setImplementation(null);}
        {setKeyword($MethodScope::method,thrkw);}
interfaceMethodDeclaratorRest
@init{int count = 0;}
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}}
       ('[' ']' {count++;})* 
       {if(count > 0) {
          JavaTypeReference original = (JavaTypeReference)$MethodScope::method.returnTypeReference();
          $MethodScope::method.setReturnTypeReference(new ArrayTypeReference(original,count));
        (thrkw='throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})? ';'
       {setKeyword($MethodScope::method,thrkw);}
interfaceGenericMethodDecl returns [TypeElement element]
    :   typeParameters (type | 'void') identifierRule
        interfaceMethodDeclaratorRest
voidInterfaceMethodDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}}
     ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
constructorDeclaratorRest
    :   pars=formalParameters {for(FormalParameter par: pars.element){$MethodScope::method.header().addFormalParameter(par);}} 
    ('throws' names=qualifiedNameList { ExceptionClause clause = new ExceptionClause(); for(String name: names.element){clause.add(new TypeExceptionDeclaration(typeRef(name)));$MethodScope::method.setExceptionClause(clause);}})?
     body=constructorBody {$MethodScope::method.setImplementation(new RegularImplementation(body.element));}
constantDeclarator returns [JavaVariableDeclaration element]
@init{int count = 0;}
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    :   name=identifierRule (('[' ']' {count++;})* '=' init=variableInitializer) 
       {retval.element = new JavaVariableDeclaration($name.text);
        retval.element.setArrayDimension(count); 
        retval.element.setInitialization(init.element);
        setName(retval.element, name.start);
variableDeclarators returns [List<VariableDeclaration> element]
    :   decl=variableDeclarator {retval.element = new ArrayList<VariableDeclaration>(); retval.element.add(decl.element);}(',' decll=variableDeclarator {retval.element.add(decll.element);})*
variableDeclarator returns [JavaVariableDeclaration element]
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    :   id=variableDeclaratorId 
             {retval.element = new JavaVariableDeclaration(id.element.name()); 
              retval.element.setArrayDimension(id.element.dimension());
              setName(retval.element, id.element.nameToken());
              } ('=' init=variableInitializer {retval.element.setInitialization(init.element);})?
variableDeclaratorId returns [StupidVariableDeclaratorId element]
@init{int count = 0;}
    :   name=identifierRule ('[' ']' {count++;})* { retval.element = new StupidVariableDeclaratorId($name.text, count,(CommonToken)name.start);}
variableInitializer returns [Expression element]
    :   init=arrayInitializer {retval.element = init.element;}
    |   expr=expression {retval.element = expr.element;}
arrayInitializer returns [ArrayInitializer element]
    :   '{' {retval.element = new ArrayInitializer();} (init=variableInitializer {retval.element.addInitializer(init.element);}(',' initt=variableInitializer{retval.element.addInitializer(initt.element);})* (',')? )? '}'
   annotation            (not required I think)
modifier returns [Modifier element]
@after{setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    :   mod=classOrInterfaceModifier {retval.element = mod.element;}
    |   'native' {retval.element = new Native();}
    |   'synchronized' {retval.element = new Synchronized();}
    |   'transient' {retval.element = new Transient();}
    |   'volatile' {retval.element = new Volatile();}
enumConstantName returns [String element]
    :   id=identifierRule {retval.element=$id.text;}
typeName returns [String element]
    :   name=qualifiedName {retval.element=name.element;}
type returns [JavaTypeReference element]
@init{int dimension=0;}
@after{setLocation(retval.element, retval.start, retval.stop);}
	:	cd=classOrInterfaceType ('[' ']' {dimension++;})* 
	         retval.element = cd.element.toArray(dimension);
	|	pt=primitiveType ('[' ']'{dimension++;})* {retval.element = pt.element.toArray(dimension);}
classOrInterfaceType returns [JavaTypeReference element]
@init{NamespaceOrTypeReference target = null;
      Token stop = null;
	:	name=identifierRule 
	           retval.element = typeRef($name.text); 
	           target =  new NamespaceOrTypeReference($name.text);
	           stop=name.start; 
	        (args=typeArguments 
	           ((BasicJavaTypeReference)retval.element).addAllArguments(args.element);
	           target = null;
	           stop=args.stop;
	          {setLocation(retval.element,name.start,stop);}  
	        ('.' namex=identifierRule 
	           if(target != null) {
	             retval.element = createTypeReference(target,$namex.text);
	             target = new NamespaceOrTypeReference(target.clone(),$namex.text);
	           } else {
	             throw new Error();
	           stop=namex.start;
	        (argsx=typeArguments 
             ((BasicJavaTypeReference)retval.element).addAllArguments(argsx.element);
	           target = null;
	           stop = argsx.stop;
	          })? {setLocation(retval.element,name.start,stop);})*
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
variableModifier returns [Modifier element]
    :   'final' {retval.element = new Final();}
    |   annotation
typeArguments returns [List<ActualTypeArgument> element]
@init{retval.element = new ArrayList<ActualTypeArgument>();}
        arg=typeArgument {retval.element.add(arg.element);}
        (',' argx=typeArgument {retval.element.add(argx.element);})* 
typeArgument returns [ActualTypeArgument element]
@init{
boolean pure=true;
boolean ext=true;
    :   t=type {retval.element = java().createBasicTypeArgument(t.element);}
          {pure=false;}
          ('extends' | 'super'{ext=false;}) 
          t=type
          {if(ext) {
            retval.element = java().createExtendsWildcard(t.element);
           } else {
            retval.element = java().createSuperWildcard(t.element);
        {if(pure) {
           retval.element = java().createPureWildcard();
qualifiedNameList returns[List<String> element]
@init{retval.element = new ArrayList<String>();}
    :   q=qualifiedName {retval.element.add($q.text);} (',' qn=qualifiedName {retval.element.add($qn.text);})*
formalParameters returns [List<FormalParameter> element]
@init{retval.element = new ArrayList<FormalParameter>();}
    :   '(' (pars=formalParameterDecls {retval.element=pars.element;})? ')'
formalParameterDecls returns [List<FormalParameter> element]
    :   mods=variableModifiers t=type id=variableDeclaratorId 
        (',' decls=formalParameterDecls {retval.element=decls.element; })?
        {if(retval.element == null) {
         retval.element=new ArrayList<FormalParameter>();}
         FormalParameter param = new FormalParameter(new SimpleNameSignature(id.element.name()),myToArray(t.element, id.element));
         param.addAllModifiers(mods.element);
         retval.element.add(0,param);
         setLocation(param, mods.start,id.stop);
    |   modss=variableModifiers tt=type '...' idd=variableDeclaratorId 
        {retval.element = new ArrayList<FormalParameter>(); 
         FormalParameter param = new MultiFormalParameter(new SimpleNameSignature(idd.element.name()),myToArray(tt.element,idd.element));
         param.addAllModifiers(modss.element);
         retval.element.add(param);
         setLocation(param, modss.start, idd.stop);
methodBody returns [Block element]
    :   b=block {retval.element = b.element;}
constructorBody returns [Block element]
    :   '{' {retval.element = new Block();} 
         (inv=explicitConstructorInvocation {retval.element.addStatement(new StatementExpression(inv.element));})? 
         (bs=blockStatement {retval.element.addStatement(bs.element);})* '}'
explicitConstructorInvocation returns [MethodInvocation element]
@init{Expression target=null;}
    :   nonWildcardTypeArguments? 'this' args=arguments ';'
       {retval.element = new ThisConstructorDelegation();
        retval.element.addAllArguments(args.element);}
    | (prim=primary '.' {target=prim.element;})? nonWildcardTypeArguments? 'super' argsx=arguments ';' 
      {retval.element = new SuperConstructorDelegation();
       retval.element.addAllArguments(argsx.element);
       if(target != null) {
         retval.element.setTarget(target);
qualifiedName returns [String element]
@init{StringBuffer buffer = new StringBuffer();}
    :   id=identifierRule {buffer.append($id.text);}('.' idx=identifierRule {buffer.append($idx.text);})*
literal returns [Literal element]
    :   intl=integerLiteral {retval.element=intl.element;}
    |   fl=FloatingPointLiteral {
           String text = $fl.text;
           if(text.endsWith("f") || text.endsWith("F")) { 
             retval.element=new RegularLiteral(typeRef("float"),text);
           } else {
             retval.element=new RegularLiteral(typeRef("double"),text);
    |   charl=CharacterLiteral {retval.element=new RegularLiteral(typeRef("char"),$charl.text);}
    |   strl=StringLiteral {retval.element=new RegularLiteral(typeRef("java.lang.String"),$strl.text);}
    |   booll=booleanLiteral {retval.element=booll.element;}
    |   'null' {retval.element = new NullLiteral();}
integerLiteral returns [Literal element]
    :   hexl=HexLiteral {retval.element=new RegularLiteral(typeRef("int"),$hexl.text);}
    |   octl=OctalLiteral {retval.element=new RegularLiteral(typeRef("int"),$octl.text);}
    |   decl=DecimalLiteral {retval.element=new RegularLiteral(typeRef("int"),$decl.text);}
booleanLiteral returns [Literal element]
    :   'true' {retval.element = new RegularLiteral(typeRef("boolean"),"true");}
    |   'false' {retval.element = new RegularLiteral(typeRef("boolean"),"false");}
annotations returns [List<AnnotationModifier> element]
@init{retval.element = new ArrayList<AnnotationModifier>();}
    :   (a=annotation {retval.element.add(a.element);})+
annotation returns [AnnotationModifier element]
    :   '@' a=annotationName {retval.element=new AnnotationModifier($a.text);} ( '(' ( elementValuePairs | elementValue )? ')' )?
annotationName
    : identifierRule ('.' identifierRule)*
elementValuePairs
    :   elementValuePair (',' elementValuePair)*
elementValuePair
    :   identifierRule '=' elementValue
elementValue
    :   conditionalExpression
    |   annotation
    |   elementValueArrayInitializer
elementValueArrayInitializer
    :   '{' (elementValue (',' elementValue)*)? (',')? '}'
annotationTypeDeclaration returns [TypeWithBody element]
    :   '@' 'interface' name=identifierRule 
               retval.element = (TypeWithBody)createType(new SimpleNameSignature($name.text));
               retval.element.addModifier(new AnnotationType());
               setName(retval.element,name.start);
             body=annotationTypeBody {retval.element.setBody(body.element);}
annotationTypeBody returns [ClassBody element]
@init{retval.element = new ClassBody();}
    :   '{' (annotationTypeElementDeclaration)* '}'
annotationTypeElementDeclaration returns [TypeElement element]
    :   mods=modifiers rest=annotationTypeElementRest 
         retval.element = rest.element;
         for(Modifier modifier: mods.element) {
           retval.element.addModifier(modifier);
annotationTypeElementRest returns [TypeElement element]
    :   t=type ann=annotationMethodOrConstantRest[$t.element] {retval.element = ann.element;} 
    |   cd=normalClassDeclaration { retval.element = cd.element; addNonTopLevelObjectInheritance(cd.element);}';'?
    |   id=normalInterfaceDeclaration { retval.element = id.element; addNonTopLevelObjectInheritance(id.element);}';'?
    |   en=enumDeclaration {retval.element = en.element;} ';'?
    |   an=annotationTypeDeclaration {retval.element = an.element;} ';'?
annotationMethodOrConstantRest[TypeReference type] returns [TypeElement element]
    :   a=annotationMethodRest[$type] {retval.element = a.element;}
    |   aa=annotationConstantRest[$type] {retval.element = aa.element;}
annotationMethodRest[TypeReference type] returns [Method element]
    :   name=identifierRule '(' ')' 
        {retval.element = createNormalMethod(new SimpleNameMethodHeader($name.text,type));
         setName(retval.element,name.start);
        } (defaultValue {})?
annotationConstantRest[TypeReference type] returns [MemberVariableDeclarator element]
    :   decls=variableDeclarators 
        {retval.element = new MemberVariableDeclarator(type);
         for(VariableDeclaration decl: decls.element) {
           retval.element.add(decl);
defaultValue
    :   'default' elementValue
block returns [Block element]
    :   '{' {retval.element = new Block();} (stat=blockStatement {if(stat != null) {retval.element.addStatement(stat.element);}})* '}'
blockStatement returns [Statement element]
@after{assert(retval.element != null);}
    :   local=localVariableDeclarationStatement {retval.element = local.element;}
    |   cd=classOrInterfaceDeclaration {retval.element = new LocalClassStatement(cd.element);}
    |   stat=statement {retval.element = stat.element;}
localVariableDeclarationStatement returns [Statement element]
    :    local=localVariableDeclaration {retval.element=local.element;} ';'
localVariableDeclaration returns [LocalVariableDeclarator element]
    :   mods=variableModifiers ref=type {retval.element = new LocalVariableDeclarator(ref.element);} decls=variableDeclarators {for(VariableDeclaration decl: decls.element) {retval.element.add(decl);}}
        {for(Modifier mod : mods.element) {retval.element.addModifier(mod);}}
variableModifiers returns [List<Modifier> element]
@init{retval.element = new ArrayList<Modifier>();}
    :   (mod=variableModifier {retval.element.add(mod.element);})*
statement returns [Statement element]
@after{check_null(retval.element);
setLocation(retval.element, (CommonToken)retval.start, (CommonToken)retval.stop);}
    : bl=block {retval.element = bl.element;}
    |   ASSERT asexpr=expression {retval.element=new AssertStatement(asexpr.element);}(':' asexprx=expression {((AssertStatement)retval.element).setMessageExpression(asexprx.element);})? ';'
    |   ifkey='if' ifexpr=parExpression ifif=statement (options {k=1;}:elsekey='else' ifelse=statement)? 
         {retval.element=new IfThenElseStatement(ifexpr.element, ifif.element, (ifelse == null ? null : ifelse.element));
          setKeyword(retval.element,ifkey);
          if(ifelse != null) {
            setKeyword(ifelse.element,elsekey);
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
        | ctss=catches {((TryStatement)retval.element).addAllCatchClauses(ctss.element);}
        |   finnkey='finally' trybll=block 
           {((TryStatement)retval.element).setFinallyClause(new FinallyClause(trybll.element));
           setKeyword(retval.element,finnkey);}
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
    |   breakkey='break' 
        {retval.element = new BreakStatement();
        setKeyword(retval.element,breakkey);} 
        (name=identifierRule {((BreakStatement)retval.element).setLabel($name.text);})? ';'
    |   continuekey='continue' 
        {retval.element = new ContinueStatement();
        setKeyword(retval.element,continuekey);} 
        (name=identifierRule {((ContinueStatement)retval.element).setLabel($name.text);})? ';'
    |   ';' {retval.element = new EmptyStatement();}
    |   stattex=statementExpression {retval.element = new StatementExpression(stattex.element);} ';'
    |   name=identifierRule ':' labstat=statement {retval.element = new LabeledStatement($name.text,labstat.element);}
catches returns [List<CatchClause> element]
@after{assert(retval.element != null);}
    :   {retval.element = new ArrayList<CatchClause>();} (ct=catchClause {retval.element.add(ct.element);})+
catchClause returns [CatchClause element]
@after{assert(retval.element != null);}
    :   catchkey='catch' '(' par=formalParameter ')' bl=block 
        {retval.element = new CatchClause(par.element, bl.element);
        setKeyword(retval.element,catchkey);}
formalParameter returns [FormalParameter element]
@after{assert(retval.element != null);}
    :   mods=variableModifiers tref=type name=variableDeclaratorId 
        {retval.element = new FormalParameter(new SimpleNameSignature(name.element.name()), myToArray(tref.element, name.element));
         setLocation(retval.element, mods.start, name.stop);
switchBlockStatementGroups returns [List<SwitchCase> element]
@after{assert(retval.element != null);}
    :   {retval.element = new ArrayList<SwitchCase>();}(cs=switchCase {retval.element.add(cs.element);})*
   ambiguous; but with appropriately greedy parsing it yields the most
   appropriate AST, one in which each group, except possibly the last one, has
   labels and statements. */
switchCase returns [SwitchCase element]
@after{assert(retval.element != null);}
    :   label=switchLabel {retval.element = new SwitchCase(label.element);} blockStatement*
switchLabel returns [SwitchLabel element]
@after{assert(retval.element != null);}
    :   'case' csexpr=constantExpression ':' {retval.element = new CaseLabel(csexpr.element);}
    |   'case' enumname=enumConstantName ':' {retval.element = new EnumLabel(enumname.element);}
    |   'default' ':'{retval.element = new DefaultLabel();}
forControl returns [ForControl element]
options {k=3;} // be efficient for common case: for (ID ID : ID) ...
@after{assert(retval.element != null);}
    :   enh=enhancedForControl {retval.element=enh.element;}
    |   in=forInit? ';' e=expression? ';' u=forUpdate? {retval.element = new SimpleForControl($in.element,$e.element,$u.element);}
forInit returns [ForInit element]
@after{assert(retval.element != null);}
    :   local=localVariableDeclaration {retval.element=local.element;}
    |   el=expressionList {retval.element = new StatementExprList(); for(Expression expr: el.element){((StatementExprList)retval.element).addStatement(new StatementExpression(expr));};}
enhancedForControl returns [ForControl element]
@after{assert(retval.element != null);}
    :   local=localVariableDeclaration ':' ex=expression {retval.element = new EnhancedForControl(local.element, ex.element);}
forUpdate returns [StatementExprList element]
    :   el=expressionList {retval.element = new StatementExprList(); for(Expression expr: el.element){((StatementExprList)retval.element).addStatement(new StatementExpression(expr));};}
parExpression returns [Expression element]
@init{
Token start=null;
Token stop=null;
@after{
setLocation(retval.element,start,stop);
    :   s='(' expr=expression {retval.element = expr.element;} e=')'
          start = s;
          stop = e;
expressionList returns [List<Expression> element]
    :   {retval.element = new ArrayList<Expression>();} e=expression 
        {if(e.element == null) {System.out.println($e.text);throw new RuntimeException("parser error");}
         retval.element.add(e.element);}
         (',' ex=expression {retval.element.add(ex.element);})*
statementExpression returns [Expression element]
    :   e=expression {retval.element = e.element;}
constantExpression returns [Expression element]
    :   e=expression {retval.element = e.element;}
expression returns [Expression element]
    :   ex=conditionalExpression {retval.element=ex.element;} (op=assignmentOperator exx=expression 
        {String txt = $op.text; 
         if(txt.equals("=")) {
           retval.element = new AssignmentExpression(ex.element,exx.element);
         } else {
           retval.element = new InfixOperatorInvocation($op.text,ex.element);
           ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,retval.start,exx.stop);
assignmentOperator
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
conditionalExpression returns [Expression element]
    :   ex=conditionalOrExpression {retval.element = ex.element;}( '?' exx=expression ':' exxx=expression 
        {retval.element = new ConditionalExpression(retval.element,exx.element,exxx.element);
         setLocation(retval.element,retval.start,exxx.stop);
conditionalOrExpression returns [Expression element]
    :   ex=conditionalAndExpression {retval.element = ex.element;} ( '||' exx=conditionalAndExpression 
        {retval.element = new ConditionalOrExpression(retval.element, exx.element);
         setLocation(retval.element,retval.start,exx.stop);
conditionalAndExpression returns [Expression element]
    :   ex=inclusiveOrExpression {retval.element = ex.element;} ( '&&' exx=inclusiveOrExpression 
        {retval.element = new ConditionalAndExpression(retval.element, exx.element);
         setLocation(retval.element,retval.start,exx.stop);
inclusiveOrExpression returns [Expression element]
    :   ex=exclusiveOrExpression {retval.element = ex.element;} ( '|' exx=exclusiveOrExpression 
         retval.element = new InfixOperatorInvocation("|", retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,retval.start,exx.stop);
exclusiveOrExpression returns [Expression element]
    :   ex=andExpression {retval.element = ex.element;} ( '^' exx=andExpression
         retval.element = new InfixOperatorInvocation("^", retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,retval.start,exx.stop);
andExpression returns [Expression element]
    :   ex=equalityExpression {retval.element = ex.element;} ( '&' exx=equalityExpression
         retval.element = new InfixOperatorInvocation("&", retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,retval.start,exx.stop);
equalityExpression returns [Expression element]
@init{String op=null;}
    :   ex=instanceOfExpression  {retval.element = ex.element;} 
          ( ('==' {op="==";} | '!=' {op="!=";}) exx=instanceOfExpression 
         retval.element = new InfixOperatorInvocation(op, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,retval.start,exx.stop);
instanceOfExpression returns [Expression element]
@after{check_null(retval.element);}
    :   ex=relationalExpression {
            retval.element = ex.element;} 
       ('instanceof' tref=type {retval.element = new InstanceofExpression(ex.element, tref.element);
         setLocation(retval.element,ex.start,tref.stop);
relationalExpression returns [Expression element]
    :   ex=shiftExpression {
              //if(ex.element == null) {throw new Error("retval is null");}
              retval.element = ex.element;} ( op=relationalOp exx=shiftExpression 
         retval.element = new InfixOperatorInvocation($op.text, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,ex.start,exx.stop);
relationalOp
    :   ('<' '=')=> t1='<' t2='=' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   ('>' '=')=> t1='>' t2='=' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
shiftExpression returns [Expression element]
    :   ex=additiveExpression {check_null(ex.element); retval.element = ex.element;} ( op=shiftOp exx=additiveExpression 
         retval.element = new InfixOperatorInvocation($op.text, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,ex.start,exx.stop);
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
additiveExpression returns [Expression element]
@init{String op = null;}
    :   ex=multiplicativeExpression {check_null(ex.element); retval.element = ex.element;} ( ('+' {op="+";} | '-' {op="-";}) exx=multiplicativeExpression 
         retval.element = new InfixOperatorInvocation(op, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,ex.start,exx.stop);
multiplicativeExpression returns [Expression element]
@init{String op = null;}
    :   ex=unaryExpression {check_null(ex.element); retval.element = ex.element;} ( ( '*' {op="*";} | '/' {op="/";} | '%' {op="\%";}) exx=unaryExpression 
         retval.element = new InfixOperatorInvocation(op, retval.element);
         ((InfixOperatorInvocation)retval.element).addArgument(exx.element);
         setLocation(retval.element,ex.start,exx.stop);
unaryExpression returns [Expression element]
    :   '+' ex=unaryExpression {retval.element = new PrefixOperatorInvocation("+",ex.element);
	setLocation(retval.element,retval.start,ex.stop);
    |   '-' exx=unaryExpression {retval.element = new PrefixOperatorInvocation("-",exx.element);
	setLocation(retval.element,retval.start,exx.stop);
    |   '++' exxx=unaryExpression {retval.element = new PrefixOperatorInvocation("++",exxx.element);
	setLocation(retval.element,retval.start,exxx.stop);
    |   '--' exxxx=unaryExpression {retval.element = new PrefixOperatorInvocation("--",exxxx.element);
	setLocation(retval.element,retval.start,exxxx.stop);
    |   eks=unaryExpressionNotPlusMinus {check_null(eks.element); retval.element = eks.element;} 
unaryExpressionNotPlusMinus returns [Expression element]
scope TargetScope;
@init{
Token start=null;
Token stop=null;
    :   a='~' {start=a;} ex=unaryExpression 
        {retval.element = new PrefixOperatorInvocation("~",ex.element); 
         stop=ex.stop;
         setLocation(retval.element,start,stop);
    |   b='!' {start=b;} exx=unaryExpression 
        {retval.element = new PrefixOperatorInvocation("!",exx.element); 
         stop=exx.stop;
         setLocation(retval.element,start,stop);
    |   castex=castExpression {check_null(castex.element); retval.element = castex.element;}
    |   prim=primary
           {check_null($prim.element);  
            $TargetScope::target=$prim.element; 
            retval.element=$prim.element;
            start=prim.start;
            $TargetScope::start = start;
        (sel=selector 
           {check_null(sel.element); 
            $TargetScope::target=$sel.element; 
            retval.element = $sel.element; 
            stop=sel.stop;
            setLocation(retval.element,start,stop);}
           c='++' {retval.element = new PostfixOperatorInvocation("++", retval.element); 
		   stop=c;
		   setLocation(retval.element,start,stop);}
         | d='--' {retval.element = new PostfixOperatorInvocation("--", retval.element); 
          	   stop=d;
          	   setLocation(retval.element,start,stop);}
selector returns [Expression element]
@init{
Token start=$TargetScope::start;
Token stop=null;
InvocationTarget old = $TargetScope::target;
	'.' name=identifierRule 
	         retval.element = new NamedTargetExpression($name.text,cloneTarget($TargetScope::target));
	         stop=name.start;
	    (args=arguments 
	        {retval.element = invocation($name.text, $TargetScope::target);
	         ((RegularMethodInvocation)retval.element).addAllArguments(args.element);
	         stop=args.stop;
	        {setLocation(retval.element,start,stop);}
    |   '.' thiskw='this' {retval.element = new ThisLiteral(createTypeReference((NamedTarget)$TargetScope::target));setLocation(retval.element,start,spkw);}
    |   '.' spkw='super' 
            supsuf=superSuffix 
              check_null(supsuf.element); 
              retval.element = supsuf.element;
              InvocationTarget tar = new SuperTarget(old);
              ((TargetedExpression)retval.element).setTarget(tar);
              setKeyword(tar,spkw);
              setLocation(old,start,spkw);
    |   '.' newkw='new' in=innerCreator {check_null(in.element); 
                                         retval.element = in.element;
                                         setKeyword(retval.element,newkw);}
    |   '[' arrex=expression bracket=']' 
          {retval.element = new ArrayAccessExpression((Expression)$TargetScope::target);
           ((ArrayAccessExpression)retval.element).addIndex(new FilledArrayIndex(arrex.element));
           setLocation(retval.element, start, bracket);
castExpression returns [Expression element]
@after{setLocation(retval.element,retval.start,retval.stop);}
    :  '(' tref=primitiveType ')' unex=unaryExpression {retval.element = new ClassCastExpression(tref.element,unex.element);}
    |  '(' treff=type ')' unexx=unaryExpressionNotPlusMinus {retval.element = new ClassCastExpression(treff.element,unexx.element);}
primary returns [Expression element]
scope TargetScope;
@init{
Token start=null;
Token stop=null;
    :   parex=parExpression {retval.element = parex.element;}
    |   rubex=identifierSuffixRubbush {retval.element = rubex.element;}
    |    skw='super' { 
                     start=skw; stop=skw; 
                     $TargetScope::start=skw;
        supsuf=superSuffix 
        {InvocationTarget tar = new SuperTarget();
         setKeyword(tar,skw);
         retval.element = supsuf.element;
         ((TargetedExpression)retval.element).setTarget(tar); 
        setLocation(tar,start,stop); // put locations on the SuperTarget.
    |   nt=nonTargetPrimary {retval.element=nt.element;}
    |   nkw='new' {start=nkw;} cr=creator {retval.element = cr.element;setKeyword(retval.element,nkw);}
    |   morerubex=moreidentifierRuleSuffixRubbish {retval.element = morerubex.element;}
    |   vt=voidType '.' clkw='class' {retval.element = new ClassLiteral(vt.element); start=vt.start;stop=clkw; setLocation(retval.element,start,stop);}
    |   tref=type '.' clkww='class' {retval.element = new ClassLiteral(tref.element);start=tref.start;stop=clkww; setLocation(retval.element,start,stop);}
nonTargetPrimary returns [Expression element]
     lit=literal {retval.element = lit.element;}
moreidentifierRuleSuffixRubbish returns [Expression element]
scope TargetScope;
@init{
Token stop = null;
InvocationTarget scopeTarget = null;
@after {
if(! retval.element.descendants().contains(scopeTarget)) {
  scopeTarget.removeAllTags();
	:	id=identifierRule 
	           {$TargetScope::target = new NamedTarget($id.text);
	            scopeTarget = $TargetScope::target;  
	            $TargetScope::start=id.start; 
	            stop=id.start;
	            setLocation($TargetScope::target,$TargetScope::start,stop);
	  ('.' idx=identifierRule 
	       {$TargetScope::target = new NamedTarget($idx.text,$TargetScope::target);
	        scopeTarget = $TargetScope::target;
	        stop=idx.start;
	        setLocation($TargetScope::target, $TargetScope::start, idx.start);
	{retval.element = new NamedTargetExpression(((NamedTarget)$TargetScope::target).name(),cloneTargetOfTarget(((NamedTarget)$TargetScope::target)));
	 setLocation(retval.element, $TargetScope::start, stop);
(       ('[' ']')+ '.' 'class'
        arr=arrayAccessSuffixRubbish {retval.element = arr.element;}
    |   arg=argumentsSuffixRubbish {retval.element.removeAllTags(); retval.element = arg.element;} // REMOVE VARIABLE REFERENCE POSITION!
    |   '.' clkw='class' 
         {retval.element.removeAllTags();
         retval.element = new ClassLiteral(createTypeReference((NamedTarget)$TargetScope::target));
          setLocation(retval.element, $TargetScope::start, clkw);
    |   '.' gen=explicitGenericInvocation {retval.element.removeAllTags(); retval.element = gen.element;} // REMOVE VARIABLE REFERENCE POSITION!
    |   '.' thiskw='this' 
        {retval.element.removeAllTags();
          retval.element = new ThisLiteral(createTypeReference((NamedTarget)$TargetScope::target));
          setLocation(retval.element, $TargetScope::start, thiskw);
    |   '.' supkw='super'  
            supsuf=superSuffix {
               retval.element.removeAllTags();
               InvocationTarget tar = new SuperTarget($TargetScope::target);
               setKeyword(tar,supkw); 
               setLocation(tar,$TargetScope::start,supkw);
               retval.element = supsuf.element;
               ((TargetedExpression)retval.element).setTarget(tar);
    |   '.' newkw='new' in=innerCreator {retval.element = in.element;setKeyword(retval.element,newkw);})?
identifierSuffixRubbush returns [Expression element]
scope TargetScope;
	:	'this' {$TargetScope::target = new ThisLiteral();}('.' id=identifierRule {$TargetScope::target = new NamedTarget($id.text,$TargetScope::target);})* 
	{if($TargetScope::target instanceof ThisLiteral) {
	  retval.element = (ThisLiteral)$TargetScope::target;
	 } else {
	  retval.element = new NamedTargetExpression(((NamedTarget)$TargetScope::target).name(),cloneTargetOfTarget((NamedTarget)$TargetScope::target));
        arr=arrayAccessSuffixRubbish {retval.element = arr.element;}
    |   arg=argumentsSuffixRubbish {retval.element = arg.element;}
    |   '.' 'class' {retval.element = new ClassLiteral(createTypeReference((NamedTarget)$TargetScope::target));}
    |   '.' gen=explicitGenericInvocation {retval.element = gen.element;}
    |   '.' supkw='super' supsuf=superSuffix {
              InvocationTarget tar = new SuperTarget($TargetScope::target);
              setKeyword(tar,supkw);
              setLocation(tar, $TargetScope::start,supkw);
              retval.element = supsuf.element;
               ((TargetedExpression)retval.element).setTarget(tar);
    |   '.' newkw='new' in=innerCreator {retval.element = in.element;setKeyword(retval.element,newkw);}
argumentsSuffixRubbish returns [RegularMethodInvocation element]
	:	args=arguments 
	        {String name = ((NamedTarget)$TargetScope::target).name();
	         $TargetScope::target = ((NamedTarget)$TargetScope::target).getTarget(); //chop off head
	         retval.element = invocation(name, $TargetScope::target);
	         retval.element.addAllArguments(args.element);
	         setLocation(retval.element, $TargetScope::start, args.stop);
arrayAccessSuffixRubbish returns [Expression element]
@after{setLocation(retval.element, $TargetScope::start, retval.stop);}
	:	{retval.element = new ArrayAccessExpression(new NamedTargetExpression(((NamedTarget)$TargetScope::target).name(),cloneTargetOfTarget((NamedTarget)$TargetScope::target)));} 
	        (open='[' arrex=expression close=']' 
	          { FilledArrayIndex index = new FilledArrayIndex(arrex.element);
	           ((ArrayAccessExpression)retval.element).addIndex(index);
	           setLocation(index, open, close);
	        )+ // can also be matched by selector, but do here
creator returns [Expression element]
@after{setLocation(retval.element, retval.start, retval.stop);}
    :   targs=nonWildcardTypeArguments tx=createdName restx=classCreatorRest
         {retval.element = new ConstructorInvocation((BasicJavaTypeReference)tx.element,$TargetScope::target);
          ((ConstructorInvocation)retval.element).setBody(restx.element.body());
          ((ConstructorInvocation)retval.element).addAllArguments(restx.element.arguments());
          ((ConstructorInvocation)retval.element).addAllTypeArguments(targs.element);
    |    tt=createdName {retval.element = new ArrayCreationExpression(tt.element);} 
             ('[' ']' {((ArrayCreationExpression)retval.element).addDimensionInitializer(new EmptyArrayIndex(1));})+ init=arrayInitializer 
        {((ArrayCreationExpression)retval.element).setInitializer(init.element);}
    |    ttt=createdName  {retval.element = new ArrayCreationExpression(ttt.element);} 
          ('[' exx=expression ']' {((ArrayCreationExpression)retval.element).addDimensionInitializer(new FilledArrayIndex(exx.element));})+ 
            ('[' ']' {((ArrayCreationExpression)retval.element).addDimensionInitializer(new EmptyArrayIndex(1));})*
    |   t=createdName rest=classCreatorRest 
         {retval.element = new ConstructorInvocation((BasicJavaTypeReference)t.element,$TargetScope::target);
          ((ConstructorInvocation)retval.element).setBody(rest.element.body());
          ((ConstructorInvocation)retval.element).addAllArguments(rest.element.arguments());
createdName returns [JavaTypeReference element]
    :   cd=classOrInterfaceType {retval.element = cd.element;}
    |   prim=primitiveType {retval.element = prim.element;}
innerCreator returns [ConstructorInvocation element]
    :   (targs=nonWildcardTypeArguments)? 
        name=identifierRule rest=classCreatorRest 
        {BasicJavaTypeReference tref = (BasicJavaTypeReference)typeRef($name.text);
         setLocation(tref,name.start,name.start);
         retval.element = new ConstructorInvocation((BasicJavaTypeReference)tref,$TargetScope::target);
         retval.element.setBody(rest.element.body());
         retval.element.addAllArguments(rest.element.arguments());
         if(targs != null) {
           retval.element.addAllTypeArguments(targs.element);
classCreatorRest returns [ClassCreatorRest element]
    :   args=arguments {retval.element = new ClassCreatorRest(args.element);}(body=classBody {retval.element.setBody(body.element);})?
explicitGenericInvocation returns [Expression element]
    :   targs=nonWildcardTypeArguments name=identifierRule args=arguments
          {retval.element = invocation($name.text,$TargetScope::target);
           ((RegularMethodInvocation)retval.element).addAllArguments(args.element);
           ((RegularMethodInvocation)retval.element).addAllTypeArguments(targs.element);
nonWildcardTypeArguments returns [List<ActualTypeArgument> element]
    :   '<' list=typeList {retval.element = new ArrayList<ActualTypeArgument>();for(TypeReference tref:list.element){retval.element.add(java().createBasicTypeArgument(tref));}}'>'
superSuffix returns [TargetedExpression element]
@init{
   Token start=null;
   Token stop=null;
    '.' name=identifierRule {retval.element = new NamedTargetExpression($name.text);
                         start = name.start;
                         stop = name.start;} 
        (args=arguments
          {retval.element = invocation($name.text,null);
          ((RegularMethodInvocation)retval.element).addAllArguments(args.element);
          stop = args.stop;
        {setLocation(retval.element,start,stop);}
arguments returns [List<Expression> element]
@init{retval.element = new ArrayList<Expression>();}
    :   '(' (list=expressionList { for(Expression ex: list.element) {retval.element.add(ex);}} )? ')'
lexer grammar JavaL;
@header {
  package jnome.input.parser;
@members {
  protected boolean enumIsKeyword = true;
  protected boolean assertIsKeyword = true;
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
fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;
fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;
CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
fragment
OctalEscape
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
ENUM:   'enum' {if (!enumIsKeyword) $type=Identifier;}
ASSERT
    :   'assert' {if (!assertIsKeyword) $type=Identifier;}
Identifier 
    :   Letter (Letter|JavaIDDigit)*
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
WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
