package org.jnome.tools;

import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.method.Method;
import chameleon.core.method.MethodSignature;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.type.Type;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.MemberVariable;
import chameleon.editor.ChameleonEditorExtension;
import chameleon.support.member.simplename.SimpleNameSignature;
import chameleon.tool.ToolExtension;

/**
 * @author Marko van Dooren
 * @author Koen Vanderkimpen
 */
public class JavaEditorExtension extends ChameleonEditorExtension {
	
    public String getLabel(Element element) {
  		String result;
            if (element instanceof Method) {
                Method<Method,MethodSignature> method = (Method<Method,MethodSignature>)element;
                result = ((SimpleNameSignature)method.signature()).getName();
                List<FormalParameter> params = method.getParameters();
                if (params.size()>0) {
                    result += "(";
                    for (int i = 0;i<params.size();i++) {
                        FormalParameter p = params.get(i);
                        result += p.getTypeReference().getName();
                        if (i<params.size()-1) {
                        	result += ",";
                        }
                    }
                    result += ")";
                }
            } else if (element instanceof MemberVariable) {
                result = ((MemberVariable)element).getName();
            } else if (element instanceof Type) {
                result = ((Type)element).getName();
            } else if (element instanceof NamespacePart) {
            	Namespace namespace = ((NamespacePart)element).getDeclaredNamespace();
            	if(namespace != null) {
            		result = namespace.getFullyQualifiedName();
            	} else {
            		result = "Error in namespace declaration.";
            	}
            } else {
            	result = "";
            }
        return result;
    }

    public ToolExtension clone() {
        return new JavaEditorExtension();
    }

}
