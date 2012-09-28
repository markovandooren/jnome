package jnome.tool.design;

import java.io.IOException;

import jnome.core.language.Java;
import jnome.core.type.ArrayType;
import jnome.input.JavaFactory;
import jnome.workspace.JavaProjectFactory;

import org.rejuse.predicate.SafePredicate;

import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.reference.CrossReference;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.input.ParseException;
import chameleon.oo.expression.Expression;
import chameleon.oo.method.Method;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.oo.type.Type;
import chameleon.oo.variable.MemberVariable;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.tool.ModelBuilder;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.workspace.ConfigException;
import chameleon.workspace.Project;


public class DesignAnalyzer {
	
	public DesignAnalyzer(Project project, ElementProvider<Namespace> namespaceProvider) {
		_sourceProject = project;
		_sourceProject.language().setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		_typeProvider = new BasicDescendantProvider<Type>(namespaceProvider, Type.class);
	}
	
	private Project _sourceProject;
	
	public Project sourceProject() {
		return _sourceProject;
	}

	public ElementProvider<? extends Type> typeProvider() {
		return _typeProvider;
	}

	private ElementProvider<Type> _typeProvider;

	public VerificationResult analyze() {
		VerificationResult result = Valid.create();
		for(Type type: typeProvider().elements(sourceProject())) {
			result = result.and(analyze(type));
		}
		return result;
	}

	public VerificationResult analyze(Type type) {
		VerificationResult result = Valid.create();
		for(Method method: type.descendants(Method.class)) {
			result = result.and(analyze(method));
		}
		return result;
	}
	
	public VerificationResult analyze(Method method) {
		EncapsulationViolatingStatement predicate = new EncapsulationViolatingStatement(method);
		method.descendants(ReturnStatement.class,predicate);
		return predicate.result();
	}
	
	public boolean mutableCollectionType(Type type) throws LookupException {
		Java lang = type.language(Java.class);
		Type coll = lang.findType("java.util.Collection");
		
		return (type.subTypeOf(coll) || (type instanceof ArrayType));
	}
	
	public class EncapsulationViolatingStatement extends SafePredicate<ReturnStatement> {
		
		public EncapsulationViolatingStatement(Method method) {
			_method = method;
		}
		
		private VerificationResult _result = Valid.create();

		private Method _method;
		
		public VerificationResult result() {
			return _result;
		}
		
		@Override
		public boolean eval(ReturnStatement object) {
			try {
				Expression expression = object.getExpression();
				if(expression instanceof CrossReference) {
					Declaration target = ((CrossReference)expression).getElement();
					if(target instanceof MemberVariable) {
						MemberVariable var = (MemberVariable) target;
						Type variableType = var.getType();
						if(mutableCollectionType(variableType)) {
							_result = _result.and(new BasicProblem(object, "A return statement of method "+_method.nearestAncestor(Type.class).getFullyQualifiedName()+"."+_method.name()+" directly exposes a member variable of type "+variableType.getFullyQualifiedName()));
						}
					}
				}
			return false;
			} catch(LookupException exc) {
				return false;
			}
		}
	}

	public static void main(String[] args) throws ConfigException {
    if(args.length < 2) {
      System.out.println("Usage: java .... JavaTranslator xmlConfigFile @recursivePackageFQN* #packageFQN* $typeFQN*");
    }
    Config.setCaching(true);
		ModelBuilder provider = new ModelBuilder(new JavaProjectFactory(),args);
    long start = System.currentTimeMillis();
    VerificationResult result = new DesignAnalyzer(provider.project(), provider.namespaceProvider()).analyze();
    System.out.println(result.message());
    long stop = System.currentTimeMillis();
    System.out.println("Translation took "+(stop - start) + " milliseconds.");

	}

}
