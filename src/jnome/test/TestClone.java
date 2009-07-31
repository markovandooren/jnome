package jnome.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;

public abstract class TestClone extends MetaModelTest {



	public abstract List<Namespace> getTestNamespaces() throws LookupException;
	
	@Test
	public void testClone() throws LookupException {
		for(Namespace namespace:getTestNamespaces()) {
			assertTrue(namespace != null);
		  for(Element element : namespace.descendants()) {
		  	test(element);
		  }
		}
	}

	public void test(Element element) {
		String msg = "element type:"+element.getClass().getName();
		assertFalse(element.isDerived());
		List<Element> children = element.children();
		assertNotNull(msg,children);
		assertFalse(msg,children.contains(null));
		Element clone = element.clone();
		assertNotNull(msg,clone);
		List<Element> clonedChildren = clone.children();
		List<Element> newChildren = element.children();
		assertNotNull(msg,clonedChildren);
		assertFalse(msg,clonedChildren.contains(null));
		assertEquals(msg,children.size(), newChildren.size());
		assertEquals(msg,children, newChildren);
		assertEquals(msg,children.size(), clonedChildren.size());
	}
	
}
