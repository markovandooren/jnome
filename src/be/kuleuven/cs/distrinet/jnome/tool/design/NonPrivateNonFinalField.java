/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.VariableDeclaration;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;

/**
 * @author Marko van Dooren
 *
 */
public class NonPrivateNonFinalField extends Analysis<VariableDeclaration, Verification,Nothing> {

  public NonPrivateNonFinalField() {
    super(VariableDeclaration.class, Valid.create());
}

@Override
protected void analyze(VariableDeclaration declaration) {
    Verification result = Valid.create();
    Java7 language = declaration.language(Java7.class);
    Variable variable = declaration.variable();
    if(variable.isFalse(language.PRIVATE) && (variable.isFalse(language.FINAL))) {
        String message = "Encapsulation error. Non-final field "+variable.name() +
                " in class "+variable.nearestAncestor(Type.class).getFullyQualifiedName()+" is not private.";
        result = new BasicProblem(declaration, message);
    }
    setResult(result().and(result));
}

}
