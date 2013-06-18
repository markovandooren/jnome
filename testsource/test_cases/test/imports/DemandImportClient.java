package test.imports;

import static test.imports.ClassWithNestedClasses.*;

public class DemandImportClient {

	public InnerClass innerClass() {
		return new InnerClass();
	}
	
	public InnerInterface innerInterface() {
		m();
		return new InnerInterface() {
		};
	}
}
