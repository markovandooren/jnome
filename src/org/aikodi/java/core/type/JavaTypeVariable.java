package org.aikodi.java.core.type;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;

import com.google.common.collect.ImmutableList;

public class JavaTypeVariable extends TypeVariable implements JavaType {

  public JavaTypeVariable(String name, Type aliasedType, FormalTypeParameter param) {
    super(name, aliasedType, param);
  }

  @Override
  public Type erasure() {
    return this;
  }
  
  @Override
  public TypeVariable cloneSelf() {
    return new JavaTypeVariable(name(), indirectionTarget(), parameter());
  }

  @Override
  public List<Declaration> implicitMembers() {
  	if(_implicits == null) {
  		synchronized(this) {
  	  	if(_implicits == null) {
  	  		_implicits = ImmutableList.<Declaration>builder().add(AbstractJavaType.getClassMethod(this)).build();
  	  	}  			
  		}
  	}
  	return _implicits;
  }
  
  private List<Declaration> _implicits;
  
  // 
//  @Override
//  public boolean uniSupertypeOf(Type other, TypeFixer trace) throws LookupException {
//    if(trace.contains(other, parameter())) {
//      return true;
//    }
//    trace.add(other, parameter());
//    return other.subtypeOf(aliasedType(), trace);
//  }

//  @Override
//  public Type upperBound() throws LookupException {
//    return parameter().upperBound();
//  }
//
//  @Override
//  public Type lowerBound() throws LookupException {
//    return parameter().lowerBound();
//  }
}
