package test.generics;

public class Recursive<X,P extends Recursive<X,P>> {

  public void x(P p) {
    m(p);
  }

  public void m(Recursive<X,?> p) {

  }


}
