package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;




import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.declaration.Signature;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.member.SimpleNameDeclarationWithParametersSignature;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.support.member.simplename.SimpleNameMethodInvocation;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;

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
