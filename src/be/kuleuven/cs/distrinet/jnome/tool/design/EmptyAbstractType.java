package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.util.List;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class EmptyAbstractType extends Analysis<Type,Verification, LookupException> {

	public EmptyAbstractType() {
	super(Type.class, Valid.create());
	}

	@Override
	protected void analyze(Type type) throws LookupException {
		if(type.isTrue(type.language(Java7.class).ABSTRACT)) {
			List<Method> methods = type.directlyDeclaredMembers(Method.class);
			if(methods.isEmpty()) {
				setResult(result().and(new EmptyAbstractTypeProblem(type, "Abstract type "+type.getFullyQualifiedName()+" does not defined any method.")));
			}
		}
	}
	
	public class EmptyAbstractTypeProblem extends BasicProblem {
		public EmptyAbstractTypeProblem(Element element, String message) {
			super(element, message);
		}
	}
}
