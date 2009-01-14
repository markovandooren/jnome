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
package jnome.core.language;

import jnome.core.modifier.PackageProperty;
import jnome.core.type.NullType;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.property.Property;
import org.rejuse.property.PropertyMutex;
import org.rejuse.property.StaticProperty;

import chameleon.core.MetamodelException;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.language.Language;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.method.MethodSignature;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.variable.MemberVariable;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.modifier.PrivateProperty;
import chameleon.support.modifier.ProtectedProperty;
import chameleon.support.modifier.PublicProperty;

/**
 * @author Marko van Dooren
 */
public class Java extends Language {

	
	protected NullType _nullType;
	public final Property<Element> STRICTFP = new StaticProperty<Element>("strictfp", this);
	public final Property<Element> SYNCHRONIZED = new StaticProperty<Element>("synchronized", this);
	public final Property<Element> TRANSIENT = new StaticProperty<Element>("transient", this);
	public final Property<Element> VOLATILE = new StaticProperty<Element>("volatile", this);
	public final PropertyMutex<Element> ACCESSIBILITY_MUTEX = new PropertyMutex<Element>();
	public final Property<Element> PROTECTED = new ProtectedProperty(this, ACCESSIBILITY_MUTEX);
	public final Property<Element> PRIVATE = new PrivateProperty(this, ACCESSIBILITY_MUTEX);
	public final Property<Element> PUBLIC = new PublicProperty(this, ACCESSIBILITY_MUTEX);
	public final Property<Element> PACKAGE_ACCESSIBLE = new PackageProperty(this, ACCESSIBILITY_MUTEX);
	
	
	
	public Java(){
		super("Java");
		_nullType = new NullType(this);
		new RootNamespace(new SimpleNameSignature(""), this);
		//NamespacePart np = new NamespacePart(getDefaultNamespace(), getContextFactory().getNamespacePartLocalContext());
		//np.addType(_nullType);
		//new CompilationUnit(np);
		
		this.defaultNamespace().setNullType();
		
	}
	
	public Type getNullType(){
		return _nullType;
	}
 /*@
   @ also public behavior
   @
   @ post \result.equals(getDefaultPackage().findType("java.lang.NullPointerException")); 
   @*/
  public Type getNullInvocationException() throws MetamodelException {
    return defaultNamespace().findType("java.lang.NullPointerException");
  }

 /*@
   @ also public behavior
   @
   @ post \result.equals(getDefaultPackage().findType("java.lang.RuntimeException")); 
   @*/
  public Type getUncheckedException() throws MetamodelException {
    return defaultNamespace().findType("java.lang.RuntimeException");
  }
  
  public Type getTopCheckedException() throws MetamodelException {
    return defaultNamespace().findType("java.lang.Throwable");
  }

  public boolean isCheckedException(Type type) throws MetamodelException{
    Type error = defaultNamespace().findType("java.lang.Error");
    Type runtimeExc = defaultNamespace().findType("java.lang.RuntimeException");
    return isException(type) && (! type.assignableTo(error)) && (! type.assignableTo(runtimeExc));
  }

  public boolean isException(Type type) throws MetamodelException {
    return type.assignableTo(defaultNamespace().findType("java.lang.Throwable"));
  }
  
	  public String getDefaultSuperClassFQN() {
	    return "java.lang.Object";
	  }
	  
    @Override
    public StrictPartialOrder<Member> overridesRelation() {
     return new StrictPartialOrder<Member>() {

      @Override
      public boolean contains(Member first, Member second)
          throws MetamodelException {
        boolean result;
        
        if((first instanceof Method) && (second instanceof Method)) {
          assert first != null;
          assert second != null;
          Method<? extends Method,? extends MethodSignature> method1 = (Method<? extends Method,? extends MethodSignature>) first;
          Method<? extends Method,? extends MethodSignature> method2 = (Method<? extends Method,? extends MethodSignature>) second;
          Ternary temp = method2.is(OVERRIDABLE);
          boolean overridable;
          if(temp == Ternary.TRUE) {
            overridable = true;
          } else if (temp == Ternary.FALSE) {
            overridable = false;
          } else {
            throw new MetamodelException("The overridability of the other method could not be determined.");
          }
          result = overridable && 
                   method1.signature().equals(method2.signature()) && 
                   method1.getNearestType().subTypeOf(method2.getNearestType()) && 
                   method1.sameKind(method2);
        } else if ((first instanceof MemberVariable) && (second instanceof MemberVariable)) {
          MemberVariable<? extends MemberVariable> var1 = (MemberVariable)first;
          MemberVariable<? extends MemberVariable> var2 = (MemberVariable)second;
          
          result = var1.getParent().subTypeOf(var2.getParent());  
        } else {
          result = false;
        }
        return result; 
      }

      @Override
      public boolean equal(Member first, Member second)
          throws MetamodelException {
        return first == second;
      }
       
     };
    }

		@Override
		public Type booleanType() throws MetamodelException {
			return defaultNamespace().findType("boolean");
		}

		@Override
		public Type classCastException() throws MetamodelException {
			return defaultNamespace().findType("java.lang.ClassCastException");
		}

		@Override
		public StrictPartialOrder<Member> hidesRelation() {
			return new StrictPartialOrder<Member>() {

				@Override
				public boolean contains(Member first, Member second) throws MetamodelException {
					boolean result = false;
					if((first instanceof NormalMethod) && (second instanceof NormalMethod)) {
						result = first.getNearestType().subTypeOf(second.getNearestType()) &&
						         (first.is(CLASS) == Ternary.TRUE) && 
						          first.signature().sameAs(second.signature());
					} else if(((first instanceof RegularMemberVariable) && (second instanceof RegularMemberVariable)) ||
							((first instanceof RegularMemberVariable) && (second instanceof RegularMemberVariable))) {
						 result = first.getNearestType().subTypeOf(second.getNearestType()) &&
						          first.signature().sameAs(second.signature());
					}
					return result;
				}

				@Override
				public boolean equal(Member first, Member second) throws MetamodelException {
					return first.equals(second);
				}
				
			};
		}

		@Override
		public Type voidType() throws MetamodelException {
			return defaultNamespace().findType("void");
		}
  
}
