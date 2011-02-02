package jnome.core.expression.invocation;




import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersSignature;
import chameleon.core.lookup.LookupException;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.support.member.simplename.SimpleNameMethodInvocation;
import chameleon.support.member.simplename.method.NormalMethod;

public class JavaMethodSelector extends AbstractJavaMethodSelector {

  	/**
		 * 
		 */
		private final SimpleNameMethodInvocation<?,?> _javaMethodInvocation;

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
		public String selectionName(DeclarationContainer<?> container) {
			return invocation().name();
		}



		protected SimpleNameMethodInvocation<?,?> invocation() {
			return _javaMethodInvocation;
		}
  }