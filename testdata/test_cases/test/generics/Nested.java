package test.generics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nested {

	public void nested() {
		List<List<Nested>> nested = new ArrayList<List<Nested>>();
		List<List<Nested>> unmodifiable = Collections.unmodifiableList(nested);
	}
}
