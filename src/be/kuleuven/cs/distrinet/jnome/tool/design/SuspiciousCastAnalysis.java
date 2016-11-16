package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;
import org.aikodi.chameleon.support.expression.ClassCastExpression;
import org.aikodi.chameleon.support.member.simplename.method.RegularMethodInvocation;
import org.aikodi.chameleon.util.Util;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class SuspiciousCastAnalysis extends Analysis<Method,Verification, LookupException>{

	public SuspiciousCastAnalysis() {
		super(Method.class, Valid.create());
	}

	@Override
	protected void analyze(Method element) throws LookupException {
		List<RegularMethodInvocation> invocations = element.descendants(RegularMethodInvocation.class);
		List<CrossReferenceTarget> filter = invocations.stream().map(i -> i.getTarget()).filter(t -> t != null).collect(Collectors.toList());
		Set<Type> targetTypes = new LinkedHashSet<>();
		List<ClassCastExpression> casts = element.descendants(ClassCastExpression.class);
		Set<Type> castTypes = new LinkedHashSet<>();
		for(ClassCastExpression cast : casts) {
			Type type = cast.getType();
			if(! (type instanceof TypeVariable)) {
				castTypes.add(type);
			}
		}
		for(MethodInvocation invocation: invocations) {
			Type type = invocation.getElement().nearestAncestor(Type.class);
			CrossReferenceTarget target = invocation.getTarget();
			if(target != null) {
				Type container = target.targetContext().declarationContainer().lexical().nearestAncestorOrSelf(Type.class);
				ClassCastExpression surroundingCast = invocation.nearestAncestor(ClassCastExpression.class);
				boolean subtype = false;
				for(Type c: castTypes) {
					subtype = subtype || container.subtypeOf(c);
				}
				if(surroundingCast == null && (! subtype)) {//|| (!type.subtypeOf(surroundingCast.getType()))
					targetTypes.add(type);
				}
			}
		}
		Type thisType = element.nearestAncestor(Type.class);
		targetTypes.removeAll(thisType.getSelfAndAllSuperTypesView());
		for(Type cType: castTypes) {
			for(Type tType: targetTypes) {
				if(cType.isTrue(element.language(Java7.class).REFERENCE_TYPE) && cType.subtypeOf(tType) && !tType.subtypeOf(cType)) {
					BasicProblem problem = new SuspiciousCast(element,"Method "+thisType.getFullyQualifiedName()+"."+element.name()+""
							+ " calls a method on a target of type "+tType.getFullyQualifiedName()+" and performs a cast to "+cType.getFullyQualifiedName()+
							", which is a subtype of "+tType.name()+". This is suspicious.");
					setResult(result().and(problem));
				}
			}
		}
	}

	public static class SuspiciousCast extends BasicProblem {

		public SuspiciousCast(Element element, String message) {
			super(element, message);
		}

	}


}
