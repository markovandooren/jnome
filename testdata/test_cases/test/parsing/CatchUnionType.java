package test.parsing;

public class CatchUnionType {

	void m() {
		try {
			
		}catch(Error | RuntimeException e) {
			
		}
	}
}
