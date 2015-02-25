package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.Signature;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;

public abstract class AbstractConstructorSelector extends AbstractJavaMethodSelector<NormalMethod> {

	public AbstractConstructorSelector() {
		super(NormalMethod.class);
	}

	public List<Declaration> withoutNonConstructors(List<? extends Declaration> selectionCandidates) throws LookupException {
		Java language = invocation().language(Java.class);
		List<Declaration> tmp = new ArrayList<Declaration>();
		for(Declaration decl: selectionCandidates) {
			if(decl.isTrue(language.CONSTRUCTOR)) {
				tmp.add(decl);
			}
		}
		return tmp;
	}
	
	@Override
	public boolean correctSignature(Signature signature) throws LookupException {
		return true;
	}

}
