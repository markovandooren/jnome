package be.kuleuven.cs.distrinet.jnome.tool.dependency;

import java.util.HashSet;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analysis;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.plugin.output.Syntax;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.action.SafeAction;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

import com.google.common.base.Function;

public class DependencyAnalysis<D extends Type> extends Analysis<D, DependencyResult<D>> {

	public DependencyAnalysis(Class<D> kind, 
														Predicate<Pair<D, Set<D>>> declarationPredicate, 
														Predicate<CrossReference> crossReferencePredicate,
														Function<D,D> declarationMapper) {
		super(kind);
		if(crossReferencePredicate == null) {
			throw new IllegalArgumentException("The cross reference predicate should not be null");
		}
		if(declarationPredicate == null) {
			throw new IllegalArgumentException("The declaration predicate should not be null");
		}
		if(declarationMapper == null) {
			throw new IllegalArgumentException("The declaration mapper should not be null");
		}
		_crossReferencePredicate = crossReferencePredicate;
		_declarationPredicate = declarationPredicate;
		_declarationMapper = declarationMapper;
	}

	
	private Predicate<Pair<D, Set<D>>> _declarationPredicate;
	
	private Predicate<CrossReference> _crossReferencePredicate;
	
	private Function<D, D> _declarationMapper;

	@Override
	protected DependencyResult analyse(D d) {
		try {
				Set<D> deps = findDependencies(d);
				deps.remove(d);
				if(_declarationPredicate.eval(new Pair<D, Set<D>>(d, deps))) {
					return new DependencyResult(d,deps);
				} else {
					return new DependencyResult();
				}
		} catch (Exception e) {
			e.printStackTrace();
			return new DependencyResult();
		}
	}

	protected Set<D> findDependencies(D declaration) {
		final Set<D> deps = new HashSet<D>();
		declaration.apply(new SafeAction<CrossReference>(CrossReference.class) {
			@Override
			public void perform(CrossReference cref) throws Nothing {
				try {
					if(_crossReferencePredicate.eval(cref)) {
						Declaration decl = cref.getElement();
						D container = decl.nearestAncestorOrSelf(DependencyAnalysis.this.type());
						if(container != null) {
							D apply = _declarationMapper.apply(container);
							deps.add(apply);
						}
					}
				} catch (Exception e) {
					// Only print the stacke trace when an exception is thrown. Don't want the analysis to crash.
					e.printStackTrace();
				}
			}
		});
		return deps;
	}

}
