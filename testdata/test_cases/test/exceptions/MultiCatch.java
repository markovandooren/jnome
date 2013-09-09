package test.exceptions;

public class MultiCatch {
	
	public void m() {
		try {
			
		}catch(A | B e) {
			e.getMessage();
		}
		
	}
	
	public static class A extends Exception {
		
		
	}
	
	public static class B extends RuntimeException {
		
	}
	
}