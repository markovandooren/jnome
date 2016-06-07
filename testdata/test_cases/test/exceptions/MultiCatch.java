package test.exceptions;

public class MultiCatch {
	
	public void m() throws A, B {
		try {
			m();
		}catch(A | B e) {
			e.getMessage();
		}
		
	}
	
	public static class A extends Exception {
		
		
	}
	
	public static class B extends RuntimeException {
		
	}
	
}