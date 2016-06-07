package test.enumerations;

import java.util.EnumMap;

public class Map {
	
	enum A {
		X(){
			
		},
		Y() {
			
		};
	}
	EnumMap<A, Object> map = new EnumMap<>(A.class);
}