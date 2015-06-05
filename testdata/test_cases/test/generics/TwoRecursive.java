package test.generics;

public class TwoRecursive<X,P extends TwoRecursive<X,P>> {

  public void x(P p) {
    m(p);
  }

  public void m(TwoRecursive<X,?> p) {

  }


}
