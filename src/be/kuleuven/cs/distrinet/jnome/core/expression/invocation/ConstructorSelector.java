package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;

	public class ConstructorSelector extends AbstractConstructorSelector {
    
		private ConstructorInvocation _invocation;
		
		protected ConstructorInvocation invocation() {
			return _invocation;
		}
		
		/**
		 * 
		 */
		public ConstructorSelector(ConstructorInvocation constructorInvocation, String name) {
			_invocation = constructorInvocation;
			__name = name;
  	}
  	
  	String __name;

		@Override
		public String selectionName(DeclarationContainer container) {
			return __name;
		}
  }
