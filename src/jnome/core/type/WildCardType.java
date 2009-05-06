//package jnome.core.type;
//
//import java.util.List;
//import java.util.Set;
//
//import org.rejuse.association.Reference;
//import org.rejuse.math.matrix.Matrix;
//
//import chameleon.core.element.ChameleonProgrammerException;
//import chameleon.core.type.Type;
//import chameleon.core.type.TypeElement;
//import chameleon.core.type.generics.TypeConstraint;
//import chameleon.core.type.inheritance.InheritanceRelation;
//
//public class WildCardType extends Type {
//
//	@Override
//	public void add(TypeElement element) throws ChameleonProgrammerException {
//		throw new ChameleonProgrammerException("Cannot add an element to a wildcard type.");
//	}
//
//	@Override
//	public void addInheritanceRelation(InheritanceRelation relation) throws ChameleonProgrammerException {
//		throw new ChameleonProgrammerException("Cannot add an inheritance relation to a wildcard type.");
//	}
//
//	@Override
//	public WildCardType clone() {
//		WildCardType result = new WildCardType();
//		result.setBound(bound().clone());
//		return result;
//	}
//
//	@Override
//	public Set<? extends TypeElement> directlyDeclaredElements() {
//		compile error
//	}
//
//	@Override
//	public List<InheritanceRelation> inheritanceRelations() {
//		compile error
//	}
//
//	@Override
//	public void removeInheritanceRelation(InheritanceRelation relation) throws ChameleonProgrammerException {
//		throw new ChameleonProgrammerException("Cannot remove an inheritance relation from a wildcard type.");
//	}
//	
//	/**
//	 * Return the most specific type that can be denoted by this wildcard type.
//	 * @return
//	 */
//	public Type lowerBound() {
//		return bound().lowerBound();
//	}
//
//	/**
//	 * Return the bound of this wildcard type.
//	 */
//	public TypeConstraint bound() {
//		return _constraint.getOtherEnd();
//	}
//	
//	/**
//	 * Set the bound of this wildcard type.
//	 */
// /*@
//   @ public behavior
//   @
//   @ post \result == null;
//   @*/
//	public void setBound(TypeConstraint constraint) {
//		if(constraint != null) {
//			_constraint.connectTo(constraint.parentLink());
//		} else {
//			_constraint.connectTo(null);
//		}
//	}
//	
//	private Reference<WildCardType, TypeConstraint> _constraint;
//	
//	static {
//		List<? super Matrix> t = null;
//		List<? extends Matrix> y = null;
//		t.add(y.get(0));
//	}
//}
