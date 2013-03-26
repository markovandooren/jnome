package be.kuleuven.cs.distrinet.jnome.tool.design;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.input.JavaFactory;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.chameleon.core.Config;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.validation.BasicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.plugin.ObjectOrientedFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ReturnStatement;
import be.kuleuven.cs.distrinet.chameleon.support.tool.ModelBuilder;
import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicDescendantProvider;
import be.kuleuven.cs.distrinet.chameleon.test.provider.ElementProvider;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.LanguageRepository;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;


public class DesignAnalyzer {
	
	public DesignAnalyzer(View view, ElementProvider<Namespace> namespaceProvider) {
		_sourceProject = view;
		_sourceProject.language().setPlugin(ObjectOrientedFactory.class, new JavaFactory());
		_typeProvider = new BasicDescendantProvider<Type>(namespaceProvider, Type.class);
	}
	
	private View _sourceProject;
	
	public View sourceProject() {
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
		Type coll = lang.findType("java.util.Collection",type.view().namespace());
		
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
    LanguageRepository repo = new LanguageRepository();
		Workspace workspace = new Workspace(repo);
    repo.add(new JavaLanguageFactory().create());
		ModelBuilder provider = new ModelBuilder(args, workspace);
    long start = System.currentTimeMillis();
    VerificationResult result = new DesignAnalyzer(provider.project().views().get(0), provider.namespaceProvider()).analyze();
    System.out.println(result.message());
    long stop = System.currentTimeMillis();
    System.out.println("Translation took "+(stop - start) + " milliseconds.");

	}

}
