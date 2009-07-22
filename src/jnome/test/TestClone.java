package jnome.test;

import java.util.List;

import static org.junit.Assert.*;
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
