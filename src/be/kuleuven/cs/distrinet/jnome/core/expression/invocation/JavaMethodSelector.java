package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;




import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.declaration.Signature;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.member.SignatureWithParameters;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.support.member.simplename.SimpleNameMethodInvocation;

public class JavaMethodSelector<M extends Method> extends AbstractJavaMethodSelector<M> {

  	/**
		 * 
		 */
		private final SimpleNameMethodInvocation _javaMethodInvocation;

		/**
		 * @param javaMethodInvocation
		 */
		JavaMethodSelector(SimpleNameMethodInvocation javaMethodInvocation, Class<M> type) {
			super(type);
			_javaMethodInvocation = javaMethodInvocation;
		}

		@Override
		public boolean isGreedy() {
		  return _javaMethodInvocation.nbActualParameters() == 0;
		}


		protected M selection(Declaration declarator) throws LookupException {
  		throw new ChameleonProgrammerException();
  	}
  	
  	
  	
    public boolean correctSignature(Signature signature) throws LookupException {
    	return signature.name().equals(invocation().name());
    }

    @Override
		public String selectionName(DeclarationContainer container) {
			return invocation().name();
		}



		protected SimpleNameMethodInvocation invocation() {
			return _javaMethodInvocation;
		}
  }
