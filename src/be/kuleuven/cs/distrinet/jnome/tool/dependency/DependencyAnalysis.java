package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.util.HashSet;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analysis;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.action.SafeAction;

public class DependencyAnalysis extends Analysis<Type, DependencyResult> {

	public DependencyAnalysis() {
		super(Type.class);
	}

	@Override
	protected DependencyResult analyse(Type type) {
		Set<Type> deps = new HashSet<Type>();
		type.apply(createAction(deps));
		deps.remove(type);
		return new DependencyResult(type,deps);
	}

	protected SafeAction<CrossReference> createAction(final Set<Type> accumulator) {
		return new SafeAction<CrossReference>(CrossReference.class) {
			@Override
			public void perform(CrossReference cref) throws Nothing {
				Declaration decl;
				try {
					decl = cref.getElement();
					Type container = decl.nearestAncestorOrSelf(Type.class);
					while(container instanceof ArrayType) {
						container = ((ArrayType)container).elementType();
					}
					if(container != null) {
						accumulator.add(container);
					}
				} catch (LookupException e) {
				}
			}
		};
	}

}
