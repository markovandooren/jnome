package test.enumerations;

import java.util.EnumMap;

public class TestMap {
	
	enum A {
		X(){
			
		},
		Y() {
			
		};
	}
	EnumMap<A, Object> map = new EnumMap<>(A.class);
}