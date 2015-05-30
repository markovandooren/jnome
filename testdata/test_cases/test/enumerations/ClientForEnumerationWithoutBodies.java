package test.enumerations;

public class ClientForEnumerationWithoutBodies {

	public void f(EnumerationWithoutBodies x) {
		x.m();
		f(EnumerationWithoutBodies.FIRST);
		EnumerationWithoutBodies.FIRST.m();
		EnumerationWithoutBodies.valueOf("FIRST");
	}
}
