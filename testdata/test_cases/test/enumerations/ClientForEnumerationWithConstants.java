package test.enumerations;

public class ClientForEnumerationWithConstants {

	public void f(EnumerationWithConstants x) {
		x.m();
		f(EnumerationWithConstants.FIRST);
		EnumerationWithConstants.FIRST.m();
		EnumerationWithConstants.valueOf("FIRST");
	}
}
