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
package org.jnome.input.acquire;

import java.util.ArrayList;
import java.util.List;

import org.jnome.decorator.Decorator;
import org.jnome.input.acquire.TypeAcquirer.TypeConnector;
import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.MetamodelException;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.ElementImpl;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceOrTypeReference;
import chameleon.core.namespace.NamespaceReference;
import chameleon.core.namespacepart.DemandImport;
import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.namespacepart.NamespacePartLocalContext;
import chameleon.core.namespacepart.TypeImport;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class CompilationUnitAcquirer extends Acquirer {

	/**
	 * @param factory
	 */
	public CompilationUnitAcquirer(Factory factory, ILinkage linkage) {
		super(factory, linkage);
	}

	private ExtendedAST packageAST;

	/**
	 * @param rootAST
	 * @return
	 */

	/*
	 * @ @ public behavior @
	 */
	//public void acquire(Namespace defaultPackage, ExtendedAST rootAST) throws MetamodelException {
		//System.out.println("Acquiring " + rootAST.getText());
		//Namespace pack = acquireNamespaceDeclaration(defaultPackage, rootAST);
		// the most inner-package is returned:
		// for example: package org.jnome.test => pack = test
		//NamespacePart pp = new NamespacePart(pack,	new JavaNamespacePartLocalContext());
		// NamespacePart defaultpp = new NamespacePart(defaultPackage, new
		// JavaNamespacePartLocalContext());
		//pack.addNamespacePart(pp);
		// defaultpp.addSubNamespacePart(pp);
		//CompilationUnit cu = new CompilationUnit(pp, pack);
//		if (packageAST != null) setFQNDecorator(packageAST, cu);
//		_linkage.decoratePosition(0, 0, Decorator.ALL_DECORATOR, cu);
//		_linkage.decoratePosition(0, 0, Decorator.ALL_DECORATOR, pp);
//
//		acquireImportStatements(pp, rootAST);
//		acquireObjectTypes(pp, rootAST);
//
//		// //XXX dit is een tijdelijke fix om de outline te blijven laten
//		// verschijnen
//		// //ideaal zou zijn: als alle nullpointers correct worden opgevangen
//		cu.addNamespacePart(pp);
//		_linkage.addCompilationUnit(cu);
//	}

	public void acquire(Namespace defaultPackage, ExtendedAST rootAST) throws MetamodelException {
		//System.out.println("Acquiring " + rootAST.getText());
	  	Namespace pack = acquireNamespaceDeclaration(defaultPackage, rootAST);
	    //the most inner-package is returned: 
	    //for example: package org.jnome.test => pack = test
	  	NamespacePart pp = new NamespacePart(pack, defaultPackage.language().contextFactory().getNamespacePartLocalContext());
	  	CompilationUnit cu;
	  	if(pack != defaultPackage){
	  		//System.out.println("pack == defaultPackage");
	  		NamespacePart defaultpp = new NamespacePart(defaultPackage, defaultPackage.language().contextFactory().getNamespacePartLocalContext());
	  		defaultpp.addNamespacePart(pp);
	  		cu = new CompilationUnit(defaultpp);
	  		//TODO manuel probeersel
	  		_linkage.decoratePosition(0, 0, Decorator.ALL_DECORATOR, defaultpp);
	  }else{
		  cu = new CompilationUnit(pp);
	  }
	  	if (packageAST != null) setFQNDecorator(packageAST, cu);
		_linkage.decoratePosition(0, 0, Decorator.ALL_DECORATOR, cu);
		_linkage.decoratePosition(0, 0, Decorator.ALL_DECORATOR, pp);

		acquireImportStatements(pp, rootAST);
		acquireObjectTypes(pp, rootAST);

		// //XXX dit is een tijdelijke fix om de outline te blijven laten
		// verschijnen
		// //ideaal zou zijn: als alle nullpointers correct worden opgevangen
		//cu.addNamespacePart(pp);
		_linkage.addCompilationUnit(cu);

	  }
	
	
	// accesibility changed from protected to public
	public void acquireObjectTypes(NamespacePart pp, ExtendedAST rootAST) {

		try {
			// acquire classes
			acquireClasses(pp, rootAST);

			// acquire interfaces
			acquireInterfaces(pp, rootAST);
		}
		catch (NullPointerException e) {

			e.printStackTrace();
		}
	}

	protected void acquireClasses(final NamespacePart pp, ExtendedAST rootAST) {
		try {
			// TESING ONLY
			// rootAST.showChildren(0);

			// Construct an array of AST's with root CLASS_DEF
			ExtendedAST[] classDefArray = rootAST
					.childrenOfType(JavaTokenTypes.CLASS_DEF);

			// Add classes one by one
			for (int i = 0; i < classDefArray.length; i++) {
				ExtendedAST classDefAST = classDefArray[i];

				// TESING ONLY
				// classDefAST.getLengthChildren();

				// create a new ResolvedObjectTypeAcquirer
				JavaClassAcquirer acquirer = getFactory().getJavaClassAcquirer(_linkage);
				acquirer.acquire(classDefAST, new TypeConnector() {
					public void connect(Type t) {
						pp.addType(t);
					}
				});

			}
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	protected void acquireInterfaces(final NamespacePart pp, ExtendedAST rootAST) {
		try {
			// Construct an array of AST's with root INTERFACE_DEF
			ExtendedAST[] interfaceDefArray = rootAST
					.childrenOfType(JavaTokenTypes.INTERFACE_DEF);

			// Add interfaces one by one
			for (int i = 0; i < interfaceDefArray.length; i++) {
				ExtendedAST interfaceDefAST = interfaceDefArray[i];

				// create a new ResolvedObjectTypeAcquirer
				InterfaceAcquirer acquirer = getFactory().getInterfaceAcquirer(_linkage);
				acquirer.acquire(interfaceDefAST, new TypeConnector() {
					public void connect(Type t) {
						pp.addType(t);
					}
				});
			}
		}
		catch (NullPointerException e) {

			e.printStackTrace();
		}
	}

	protected void acquireImportStatements(NamespacePart pp, ExtendedAST rootAST) {
		try {
			// Construct an array of AST's with root IMPORT
			ExtendedAST[] importArray = rootAST
					.childrenOfType(JavaTokenTypes.IMPORT);

			// Add imports one by one
			for (int i = 0; i < importArray.length; i++) {

				// Look at the AST without the root of type
				// JnomeJavaTokenTypes.IMPORT
				ExtendedAST importAST = importArray[i].firstChild();
				setFQNDecorator(importArray[i], pp);
				// Create a new UnresolvedObjectType/UnresolvedPackage.
				// The compilation unit will have a double binding with this
				// unresolved object.
				createNewUnresolvedImportable(importAST, pp);
			}

			DemandImport javaLang = new DemandImport(new NamespaceReference(new NamespaceReference("java"), "lang"));
			pp.addDemandImport(javaLang);
		}
		catch (NullPointerException e) {

			e.printStackTrace();
		}
	}

	private void setFQNDecorator(ExtendedAST importAST, ElementImpl cu) {

		if (!(importAST == null)) { // maw: er is geen packagename of import
			setSmallDecorator(cu, importAST, Decorator.CODEWORD_DECORATOR);
			setSmallDecorator(cu, importAST.firstChild(),
					Decorator.FULLYQUALIFIEDNAME_DECORATOR);
		}
	}

	private void createNewUnresolvedImportable(ExtendedAST importAST, NamespacePart pp) {
		try {
			// make a vector containing the parts of the (fully qualified) name
			// contained in the ast
			List names = importAST.getListOfStrings();

			// check the last string in the name
			// if it is a '*', a package is imported, else an objecttype is
			// imported
			String lastName = (String) names.get(names.size() - 1);
			if (lastName.equals("*")) {
				names.remove(names.size() - 1);
				if (!(Util.concat(names).equals("java.lang"))) {
					String last = (String) names.get(names.size() - 1);
					names.remove(names.size() - 1);
					String firstPart = Util.concat(names);
					NamespaceReference packRef = null;
					if (firstPart.length() > 0) {
						packRef = new NamespaceReference(firstPart);
					}
					NamespaceOrTypeReference ref = new NamespaceOrTypeReference(
							packRef, last);
					pp.addDemandImport(new DemandImport(ref));
				}
			}
			else {
				JavaTypeReference tr = new JavaTypeReference(Util.concat(names));
				TypeImport ti = new TypeImport(tr);
				pp.addImportedType(ti);
				
				//pp.addImportedType(new TypeImport(new JavaTypeReference(Util.concat(names))));
			}
		}
		catch (NullPointerException e) {

			e.printStackTrace();
		}
	}

	public Namespace acquireNamespaceDeclaration(Namespace defaultPackage, ExtendedAST rootAST) throws MetamodelException {
		return (Namespace) defaultPackage
				.getOrCreateNamespace(getPackageName(rootAST));
	}

	public String getPackageName(ExtendedAST ast) {
		ExtendedAST packageDef = ast
				.firstChildOfType(JavaTokenTypes.PACKAGE_DEF);
		this.packageAST = packageDef;
		List packageNames = new ArrayList();
		if (packageDef != null) {
			// Examine the fully qualified name of the package in the AST
			// Make a vector of strings containing the subsequent packages
			// in this name
			ExtendedAST rootOfPackageNameTree = packageDef.firstChild();

			packageNames = rootOfPackageNameTree.getListOfStrings();
		}
		return Util.concat(packageNames);
	}

}
