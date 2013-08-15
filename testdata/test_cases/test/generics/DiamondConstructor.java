package test.generics;

public class DiamondConstructor {

	public static class A<T> {
		
		public A(T t) {
			
		}
		
		public T t() {
			return _t;
		}
		
		private T _t;
		
	}
	
	
	public void m() {
//		A<DiamondConstructor> x = new A<>(new Object());
		A<DiamondConstructor> x = new A<>(new DiamondConstructor());
		
	}
	
}
