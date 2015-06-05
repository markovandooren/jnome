package test.generics;

public class Recursive<P extends Recursive<P>> {

  public void x(P p) {
    m(p);
  }

  public void m(Recursive<?> p) {

  }


}
