package test.generics;

import java.util.List;
import java.util.Collection;

public class WildcardInference {

  public <F> void m(Collection<F> m) {

  }

  public void f() {
    List<? extends Number> v = null;
    m(v);
  }

}
