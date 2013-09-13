package test.expression;

import java.io.Serializable;

public class Conditional {
	
	public void m() {
		a((true ? new Double(0) : new String()));
		b((true ? new Double(0) : new String()));
		c((true ? new Double(0) : new String()));
	}
	
	public void a(Object x) {
	}

	public void b(Comparable<?> y) {
	}

	public void c(Serializable z) {
	}

}