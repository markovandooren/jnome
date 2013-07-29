package test.enumerations;

public enum EnumerationWithConstants {

	FIRST {
		public Object f() {
			return null;
		}
		@Override
		public void n() {
			f();
		}
	},
	
	SECOND {
		@Override
		public void n() {
			
		}
	};
	
	public void m() {
		n();
	}
	
	public abstract void n();
}
