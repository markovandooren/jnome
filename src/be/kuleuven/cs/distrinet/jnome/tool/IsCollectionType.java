package be.kuleuven.cs.distrinet.jnome.tool;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

public class IsCollectionType {

  public final static Predicate<Type, LookupException> PREDICATE = type -> {
    View view = type.view();
    Java language = view.language(Java.class);
    Type collection = language.erasure(language.findType("java.util.Collection",view.namespace()));
    Type map = language.erasure(language.findType("java.util.Map",view.namespace()));
    return type instanceof ArrayType || type.subTypeOf(collection) || type.subTypeOf(map);
  };
  
}
