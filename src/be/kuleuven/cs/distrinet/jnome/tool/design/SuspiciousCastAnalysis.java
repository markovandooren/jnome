package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.expression.ClassCastExpression;
import org.aikodi.chameleon.support.member.simplename.method.RegularMethodInvocation;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class SuspiciousCastAnalysis extends Analysis<Method,Verification, LookupException>{

	public SuspiciousCastAnalysis() {
		super(Method.class, Valid.create());
	}

	@Override
	protected void analyze(Method element) throws LookupException {
		List<CrossReferenceTarget> filter = element.descendants(RegularMethodInvocation.class).stream().map(i -> i.getTarget()).filter(t -> t != null).collect(Collectors.toList());
		Iterator<CrossReferenceTarget> iterator = filter.iterator();
		Set<Type> targetTypes = new LinkedHashSet<>();
		while(iterator.hasNext()) {
			DeclarationContainer container = iterator.next().targetContext().declarationContainer();
			if(container instanceof Type) {
				targetTypes.add((Type) container);
			}
		}
		Type thisType = element.nearestAncestor(Type.class);
		targetTypes.removeAll(thisType.getSelfAndAllSuperTypesView());
		List<ClassCastExpression> casts = element.descendants(ClassCastExpression.class);
		Set<Type> castTypes = new LinkedHashSet<>();
		for(ClassCastExpression cast : casts) {
			castTypes.add(cast.getType());
		}
		for(Type cType: castTypes) {
			for(Type tType: targetTypes) {
				if(cType.isTrue(element.language(Java7.class).REFERENCE_TYPE) && cType.subtypeOf(tType) && !tType.subtypeOf(cType)) {
					BasicProblem problem = new SuspiciousCast(element,"Method "+thisType.getFullyQualifiedName()+"."+element.name()+""
					+ " calls a method on a target of type "+tType.getFullyQualifiedName()+" and performs a cast to "+cType.getFullyQualifiedName()+
					", which is a subtype of "+cType.name()+". This is suspicious.");
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
