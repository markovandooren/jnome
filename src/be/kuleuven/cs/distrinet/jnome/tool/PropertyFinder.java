package be.kuleuven.cs.distrinet.jnome.tool;

import java.util.List;

import org.aikodi.chameleon.oo.expression.NameExpression;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.expression.AssignmentExpression;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class PropertyFinder extends CommandLineTool {

	  /**
	   * args[0] = path for the directory to write output
	   * args[1] = path to read input files
	   * ...1 or more input paths possible...
	   * args[i] = fqn of package to read, let this start with "@" to read the package recursively
	   *...1 or more packageFqns possible...
	   * args[n] = fqn of package to read, let this start with "#" to NOT read the package recursively.
	   *...1 or more packageFqns possible...
	   *
	   * Example 
	   * java Copy c:\output\ c:\input1\ c:\input2\ @javax.swing @java.lang #java #java.security 
	   */
	  public PropertyFinder(String[] args) throws Exception {
	  	super(args);
		  List<Type> types = view().namespace().descendants(Type.class);
		  findProperties(types,"set","get");
		  findPairs(types,"set","get");
		  findPairs(types,"add","remove");
		  findTriples(types,"add","remove","get");
		}

		protected void findProperties(List<Type> types, String first, String second) {
		  int size = types.size();
			int count = 1;
		  int pairsInClasses = 0;
		  int pairsInInterfaces = 0;
		  int multiAssign=0;
		  int noAssign=0;
		  for(Type type:types) {
		  	//		  	System.out.println("Searching type "+count+" of "+size);
		  	Java7 lang = type.language(Java7.class);
		  	count++;
		  	for(Method method: type.directlyDeclaredElements(Method.class)) {
		  		String name = method.name();
		  		if(name.startsWith(first)) {
		  			String X = name.substring(3);
		  			for(Method getter: type.directlyDeclaredElements(Method.class)) {
		  				String otherName = getter.name();
		  				if(otherName.equals(second+X)) {
		  					if(! type.isTrue(lang.INTERFACE)) {
			  					List<AssignmentExpression> exprs = method.descendants(AssignmentExpression.class);
			  					if(exprs.size() == 1) {
			  						AssignmentExpression e = exprs.get(0);
			  						String varName = null;
			  						if(e.variableExpression() instanceof NameExpression) {
			  							varName = ((NameExpression)e.variableExpression()).name();
			  						} 
			  						boolean found = false;
			  						List<NameExpression> reads = method.descendants(NameExpression.class);
			  						for(NameExpression read:reads) {
			  							if(read.name().equals(varName)) {
					  						pairsInClasses++;
					  						found = true;
			  								break;
			  							}
			  						}
			  						if(! found) {
			  							  System.out.println("No shared field access for "+ type.getFullyQualifiedName()+" : "+name);
			  						}
			  					} else if (exprs.size() > 1) {
	  							  multiAssign++;
	  							  System.out.println("Multiple assignments in "+ type.getFullyQualifiedName()+" : "+name);
			  					} else {
			  						noAssign++;
	  							  System.out.println("No assignment in "+ type.getFullyQualifiedName()+" : "+name);
			  					}
		  					} else {
		  						pairsInInterfaces++;
		  					}
		  				}
		  			}
		  		}
		  	}
		  }
			System.out.println(first+" and "+second+" properties found in classes: "+pairsInClasses);
			System.out.println(first+" and "+second+" properties found in interfaces: "+pairsInInterfaces);
			System.out.println("multiassign = "+multiAssign);
			System.out.println("noassign = "+noAssign);
		}
	  
		protected void findPairs(List<Type> types, String first, String second) {
		  int size = types.size();
			int count = 1;
		  int pairsInClasses = 0;
		  int pairsInInterfaces = 0;
		  for(Type type:types) {
//		  	System.out.println("Searching type "+count+" of "+size);
		  	Java7 lang = type.language(Java7.class);
		  		count++;
		  		for(Method method: type.directlyDeclaredElements(Method.class)) {
		  			String name = method.name();
		  			if(name.startsWith(first)) {
		  				String X = name.substring(3);
		  				for(Method getter: type.directlyDeclaredElements(Method.class)) {
		  					String otherName = getter.name();
		  					if(otherName.equals(second+X)) {
//				  		  System.out.println(method.nearestAncestor(Type.class).getFullyQualifiedName()+" : "+X);
		  				  	if(! type.isTrue(lang.INTERFACE)) {
		  						pairsInClasses++;
		  				  	} else {
			  						pairsInInterfaces++;
		  				  	}
		  					}
		  				}
		  			}
		  		}
		  }
			System.out.println(first+" and "+second+" pairs found in classes: "+pairsInClasses);
			System.out.println(first+" and "+second+" pairs found in interfaces: "+pairsInInterfaces);
		}
	  
		protected void findTriples(List<Type> types, String first, String second, String third) {
			int size = types.size();
			int count = 1;
			int pairsInClasses = 0;
			int pairsInInterfaces = 0;
			for(Type type:types) {
				//				System.out.println("Searching type "+count+" of "+size);
				Java7 lang = type.language(Java7.class);
				count++;
				for(Method method: type.directlyDeclaredElements(Method.class)) {
					String name = method.name();
					if(name.startsWith(first)) {
						String X = name.substring(3);
						for(Method getter: type.directlyDeclaredElements(Method.class)) {
							String otherName = getter.name();
							if(otherName.equals(second+X)) {
			  				boolean foundThird = false;

								for(Method three: type.directlyDeclaredElements(Method.class)) {
									String thirdName = three.name();
									if(thirdName.equals(third+X+"s")) {
			  						foundThird = true;
	//				  		  System.out.println(method.nearestAncestor(Type.class).getFullyQualifiedName()+" : "+X);
										if(! type.isTrue(lang.INTERFACE)) {
											pairsInClasses++;
										} else {
											pairsInInterfaces++;
										}
									}
								}
			  				if(! foundThird) {
//			  					System.out.println("No getter for "+type.getFullyQualifiedName()+"."+name);
			  				}

							}
						}
					}
				}
			}
			System.out.println(first+" and "+second+" and "+ third + " triples found in classes: "+pairsInClasses);
			System.out.println(first+" and "+second+" and "+ third +" triples found in interfaces: "+pairsInInterfaces);
		}

		public static void main(String[] args) throws Exception {
			new PropertyFinder(args);
	  }

}
