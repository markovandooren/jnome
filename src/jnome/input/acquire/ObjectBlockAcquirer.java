/*
 * Created on 1-mrt-2005
*/
package org.jnome.input.acquire;

import org.jnome.input.parser.ExtendedAST;

import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;

/**
 * @author jef-g
 * @author Manuel Van Wesemael
 */
public class ObjectBlockAcquirer extends Acquirer  {
	  public ObjectBlockAcquirer(Factory factory, ILinkage linkage) {
	    super(factory, linkage);
	  }
	  
	  public void acquire(ExtendedAST BlockAST, Type type){
		  try {
			//System.out.println("Acquiring "+BlockAST.getText());
			//	Acquire (for testing this acquirer - another acquirer can be desired in other cases - depends on type)
			JavaClassAcquirer localTA = getFactory().getJavaClassAcquirer(_linkage);
			localTA.acquireObjectBlockAgain(type, BlockAST);
		} catch (NullPointerException e) {
			System.err.println("ObjectBlockAcquirer acquire errr***");
		}
	  }
}
