package test.generics;

public class Inference {

	public class A<X, Y extends A<X,Y>> {
		
	}
	
	public <Xf, Yf extends A<Xf,Yf>> Xf f(Yf a) {
		return null;
	}
	
	public <Xg, Yg extends A<Xg,Yg>> Xg g(Yg a) {
	   return f(a);
	}
}
