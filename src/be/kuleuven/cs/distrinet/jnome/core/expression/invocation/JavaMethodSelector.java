package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;




import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Signature;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.member.SimpleNameDeclarationWithParametersSignature;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.SimpleNameMethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;

public class JavaMethodSelector extends AbstractJavaMethodSelector {

  	/**
		 * 
		 */
		private final SimpleNameMethodInvocation _javaMethodInvocation;

		/**
		 * @param javaMethodInvocation
		 */
		JavaMethodSelector(SimpleNameMethodInvocation javaMethodInvocation) {
			_javaMethodInvocation = javaMethodInvocation;
		}



		protected NormalMethod selection(Declaration declarator) throws LookupException {
  		throw new ChameleonProgrammerException();
  	}
  	
  	
  	
    public boolean correctSignature(Signature signature) throws LookupException {
  		boolean result = false;
  		if(signature instanceof SimpleNameDeclarationWithParametersSignature) {
  			SimpleNameDeclarationWithParametersSignature sig = (SimpleNameDeclarationWithParametersSignature)signature;
  			result = sig.name().equals(invocation().name());
  		}
  		return result;
    }

    @Override
		public String selectionName(DeclarationContainer container) {
			return invocation().name();
		}



		protected SimpleNameMethodInvocation invocation() {
			return _javaMethodInvocation;
		}
  }
