package test.lookup;

public class FieldWithSameNameAsTopPackage {
	
	public test.lookup.FieldWithSameNameAsTopPackage test;
	
	public void m() {
		Object x = test;
	}
	
}