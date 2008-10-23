/*
 * Copyright 2000-2004 the Jnome development team.
 *
 * @author Marko van Dooren
 * @author Nele Smeets
 * @author Kristof Mertens
 * @author Jan Dockx
 *
 * This file is part of Jnome.
 *
 * Jnome is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Jnome is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Jnome; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package jnome.core.type;

import chameleon.core.MetamodelException;
import chameleon.core.namespace.NamespaceOrType;
import chameleon.core.namespace.NamespaceOrTypeReference;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;

/**
 * @author marko
 */
public class JavaTypeReference extends TypeReference {

  public JavaTypeReference(String name) {
    super(name);
  }

  
  public Type getType() throws MetamodelException {
    Type result = null;

    result = getCache();
    if (result != null) {
      return result;
    }

    if (getTarget() != null) {
      NamespaceOrType target = getTarget().getNamespaceOrType();
      if (target != null) {
        result = target.targetContext().lookUp(selector());// findType(getName());
      }
    } else {
      result = getParent().lexicalContext(this).lookUp(selector()); // (getName());
    }

    if ((getArrayDimension() != 0) && (result != null)) {
      result = new ArrayType(result,getArrayDimension());
    }

    if (result != null) {
      setCache(result);
      return result;
    } else {
      throw new MetamodelException();
    }
  }

  
//  public Type getType() throws MetamodelException {
//  	Type result = null;
//  	
//    result = getCache();
//    if(result != null) {
//    	return result;
//    }
//
//    if (getArrayDimension() == 0) {
//      if(getTarget() == null) {
//        result = getParent().getContext(this).findType(getName());
//      }
//      else {
//    	  NamespaceOrType target = getTarget().getNamespaceOrType();
//        if(target != null) {
//          result = target.getTargetContext().findType(getName());
//        }
//      }
//    }
//    else {
//      if(getTarget() == null) {
//        result = new ArrayType((Type)getParent().getContext(this).findType(getComponentName()), getArrayDimension());
//      }
//      else {
//    	  NamespaceOrType target = getTarget().getNamespaceOrType();
//        if(target != null) {
//          result = new ArrayType((Type)target.getTargetContext().findType(getComponentName()), getArrayDimension());
//        } 
//      }
//    }
//    setCache(result);
//    return result;
//  }

//  public Type getSuperType() throws NotResolvedException {
//    if (getArrayDimension() == 0) {
//      return getParent().getContext().getParentContext().findType(getName());
//    }
//    else {
//      return new ArrayType((Type)getParent().getContext().getParentContext().findType(getComponentName()), getArrayDimension());
//    }
//  }

  private int getArrayDimension() {
    int result = 0;
    String name = getName();
    int index = name.indexOf("[", 0);
    while (index >= 0) {
      result++;
      index = name.indexOf("[", index + 1);
    }
    return result;
  }

  private String getComponentName() {
    if (getArrayDimension() == 0) {
      return getName();
    }
    else {
      try {
        return getName().substring(0, getName().indexOf("["));
      }
      catch (RuntimeException e) {
        e.printStackTrace();
        getArrayDimension();
        throw e;
      }
    }
  }
  
  public JavaTypeReference clone() {
    return new JavaTypeReference(getName());
  }
  
}
