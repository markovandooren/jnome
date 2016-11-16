package be.kuleuven.cs.distrinet.jnome.tool;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.oo.type.IntersectionType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeInstantiation;
import org.aikodi.chameleon.oo.type.UnionType;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;
import org.aikodi.chameleon.util.Lists;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.function.Function;

import be.kuleuven.cs.distrinet.jnome.core.type.AnonymousType;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;

public class JavaDeclarationDecomposer implements Function<Declaration, List<Declaration>, Nothing> {
  
  @Override
  public List<Declaration> apply(Declaration declaration) {
    List<Declaration> result;
    if(declaration instanceof Type) {
      result = decomposeType((Type) declaration);
    } else {
      result = Lists.create(declaration);
    }
    return result;
  }

  protected List<Declaration> decomposeType(Type type) {
    List<Declaration> result = new ArrayList<>();
    if(type instanceof UnionType) {
      ((UnionType)type).types().forEach(t -> result.addAll(decomposeType(t)));
    } else if(type instanceof IntersectionType) {
      ((UnionType)type).types().forEach(t -> result.addAll(decomposeType(t)));
    } else {
      while(type instanceof ArrayType) {
        type = ((ArrayType)type).elementType();
      }
      while(type instanceof TypeInstantiation) {
        type = ((TypeInstantiation)type).baseType();
      }
      while(type instanceof TypeVariable) {
        type = type.nearestAncestor(Type.class);
      }
      AnonymousType anon = type.lexical().farthestAncestorOrSelf(AnonymousType.class);
      if(anon != null) {
        type = anon.nearestAncestor(Type.class);
      }
    }
    return Lists.create(type);
  }
}