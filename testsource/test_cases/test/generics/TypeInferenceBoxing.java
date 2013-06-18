
public class TypeInferenceBoxing {

	/**
	 * Static because the original bug also used a static method.
	 */
	public static <T> T m(T t) {
		return t;
	}
	
	public void m() {
		int i = 3;
		// Correct: This calls <Integer> Integer m(Integer)
		// False: before the fix, this resulted in <int> int m(int), which caused
		//        the bounds check of the inferred type arguments to fail since int
		//        is not a subtype of Object.
		n(m(i));
	}
	
	public void n(int i) {
		
	}
}
