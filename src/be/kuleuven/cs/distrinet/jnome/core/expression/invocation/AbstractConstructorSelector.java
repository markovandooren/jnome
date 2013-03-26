package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Signature;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;

public abstract class AbstractConstructorSelector extends AbstractJavaMethodSelector {

	public AbstractConstructorSelector() {
		super();
	}

	public List<NormalMethod> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
		Java language = invocation().language(Java.class);
		List<Declaration> tmp = new ArrayList<Declaration>();
		for(Declaration decl: selectionCandidates) {
			if(decl.isTrue(language.CONSTRUCTOR)) {
				tmp.add(decl);
			}
		}
		return super.selection(tmp);
	}
	
	@Override
	public boolean correctSignature(Signature signature) throws LookupException {
		return true;
	}

}
