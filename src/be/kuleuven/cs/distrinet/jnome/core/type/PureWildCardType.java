//package be.kuleuven.cs.distrinet.jnome.core.type;
//
//import org.aikodi.chameleon.core.element.Element;
//import org.aikodi.chameleon.core.lookup.LookupException;
//import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
//import org.aikodi.chameleon.oo.type.Type;
//import org.aikodi.chameleon.oo.type.TypeFixer;
//import org.aikodi.chameleon.oo.type.generics.IntervalType;
//
//public class PureWildCardType extends IntervalType {
//
//	public PureWildCardType(Type type) throws LookupException {
//		super("?",type.language(ObjectOrientedLanguage.class).getNullType(type.view().namespace()),type);
//	}
//
//	protected PureWildCardType(String name, Type lowerBound, Type upperBound) {
//    super(name, lowerBound,upperBound);
//  }
//
//
//
//  @Override
//	public String getFullyQualifiedName() {
//		return "?";
//	}
//
//	@Override
//	protected Element cloneSelf() {
//	  return new PureWildCardType(name(),lowerBound(),upperBound());
//	}
//	
//	/**
//	* @{inheritDoc}
//	*/
//	@Override
//	public boolean uniSupertypeOf(Type other, TypeFixer trace) throws LookupException {
//	  return true;
//	}
//}
