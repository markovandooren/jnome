package jnome.core.expression.invocation;

import java.util.Iterator;

import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.lookup.LookupException;

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
		public String selectionName(DeclarationContainer<?> container) {
			return __name;
		}
  }