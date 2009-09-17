package test;

public class AList<X extends AList<X>> extends List<X> {
	
	
	public void m() {
		get(0).get(0).get(0).size();
	}
}