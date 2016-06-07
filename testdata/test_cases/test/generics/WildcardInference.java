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

  public <T> void one(Collection<T> only) {
  	
  }
  public <T> void two(Collection<T> first, Collection<T> second) {
  	
  }
  
  public void callK() {
  	List<? extends Number> arg1 = null;
  	List<?> arg2 = null;
//  	two(arg1,arg2);
  	one(arg1);
  	one(arg2);
  }
  
  public void numbers(Collection<Number> numbers) {
  	
  }
  
  public <T> T id(T t) {
  	return t;
  }
  
//  public void callNumbers() {
//  	List<? extends Number> list = null;
//  	List<? extends Number> id = id(list);
//  	id.add(list.get(0));
//  	numbers(id);
//  }
}
