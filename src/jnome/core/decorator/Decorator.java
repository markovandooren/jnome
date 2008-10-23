package jnome.core.decorator;


public class Decorator {

	/** Decorator spanning an entire element **/
	public final static String ALL_DECORATOR= "__ALL";
	/** Decorator spanning the name of an element **/
	public final static String NAME_DECORATOR= "__Name";
	/** Decorator spanning the access modifiers of an element (public private protected final static) **/
	public static final String ACCESSTYPE_DECORATOR = "__AccessType";
	/** Decorator spanning special statement codewords (for, do, while, return,  ...) **/
	public static final String CODEWORD_DECORATOR = "__Codeword";
	/** Decorator spanning the type (void, int, String, ...) of a variable or method **/
	public static final String TYPE_DECORATOR = "__Type";
	/** Decorator spanning the expressions in the document (literals, casts, operators,...) **/
	//public static final String EXPRESSION_DECORATOR = "__Expression";

	public static final String COMMENT_DECORATOR = "__Comment";
	public static final String OPERATOR_DECORATOR = "__Operator";
	public static final String IMPLEMENTATION_DECORATOR = "__Implementation";
	public static final String IF_CONDITION_DECORATOR = "__IfCondition";
	public static final String IF_BODY = "__IfBody";
	public static final String ELSE_BODY = "__ElseBody";
	public static final String VARIABLE_DECLARATION_DECORATOR = "__VariableDecl";
	public static final String OPERATOR_INVOCATION_DECORATOR = "__OperatorInvocation";
	public static final String INVOCATION_TARGET_DECORATOR = "__InvocationTarget";
	public static final String FULLYQUALIFIEDNAME_DECORATOR = "__FullyQualifiedName";
	public static final String ASSIGNMENT_DECORATOR = "__Assignment";
	public static final String REFERENCE_DECORATOR = "__Reference";
	public static final String EXCEPTION_DECORATOR= "__Exception";
	public static final String ITERATION_INIT_DECORATOR = "__ForInit"; // 
	public static final String ITERATION_CONDITION_DECORATOR = "__ForCondition";
	public static final String ITERATION_ITERATION_DECORATOR = "__ForIterator";
	public static final String ITERATION_BODY = "__ForBody";

}
