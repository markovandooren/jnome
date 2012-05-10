package jnome.eclipse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jnome.core.modifier.Default;
import chameleon.core.element.Element;
import chameleon.core.modifier.Modifier;
import chameleon.eclipse.connector.EclipseEditorExtension;
import chameleon.eclipse.presentation.treeview.CompositeIconProvider;
import chameleon.eclipse.presentation.treeview.DefaultIconProvider;
import chameleon.eclipse.presentation.treeview.IconProvider;
import chameleon.exception.ModelException;
import chameleon.oo.member.Member;
import chameleon.oo.method.Method;
import chameleon.oo.variable.FormalParameter;
import chameleon.oo.variable.MemberVariable;
import chameleon.plugin.Plugin;
import chameleon.plugin.output.Syntax;
import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Constructor;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Interface;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Protected;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;

/**
 * @author Marko van Dooren
 * @author Koen Vanderkimpen
 */
public class JavaEditorExtension extends EclipseEditorExtension {

	public JavaEditorExtension(String name) {
		super(name);
		ACCESS_ICON_DECORATOR = new AccessIconDecorator();
		CLASS_ICON_PROVIDER = new ClassIconProvider(ACCESS_ICON_DECORATOR);
		FIELD_ICON_PROVIDER = new DefaultIconProvider("field", MemberVariable.class,ACCESS_ICON_DECORATOR);
		MEMBER_ICON_PROVIDER = new DefaultIconProvider("member", Member.class,ACCESS_ICON_DECORATOR);
	}

	public String pluginID() {
		return Bootstrapper.PLUGIN_ID;
	}

	public Plugin clone() {
		return new JavaEditorExtension(languageName());
	}

	public String getLabel(Element element) {
		try {
			String result;
			if (element instanceof Method) {
				Method method = (Method)element;
				result = method.name();
				List<FormalParameter> params = method.formalParameters();
				result += "(";
				if (params.size()>0) {
					for (int i = 0;i<params.size();i++) {
						FormalParameter p = params.get(i);
						try {
							result += element.language().plugin(Syntax.class).toCode(p.getTypeReference());
						} catch (ModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (i<params.size()-1) {
							result += ",";
						}
					}
				}
				result += ")";
			} else result = super.getLabel(element);
			return result;
		} catch(Exception exc) {
			return "";
		}
	}

	@Override
	public List<Modifier> getFilterModifiers() {
		List<Modifier> result = new ArrayList<Modifier>();
		result.add(new Private());
		result.add(new Protected());
		result.add(new Public());
		result.add(new Default());
		result.add(new Static());
		result.add(new Final());
		result.add(new Abstract());
		result.add(new Constructor());
		result.add(new Interface());
		result.add(new Native());
		return result;
	}

	@Override
	public JavaDeclarationCategorizer declarationCategorizer()  {
		return new JavaDeclarationCategorizer();
	}

	@Override
	public JavaOutlineSelector createOutlineSelector() {
		return new JavaOutlineSelector();
	}

	public CompositeIconProvider createIconProvider() {
		return new CompositeIconProvider(
				CLASS_ICON_PROVIDER,
				FIELD_ICON_PROVIDER,
				MEMBER_ICON_PROVIDER
				);
	}

	private void register(String fileName, String iconName) throws MalformedURLException {
		register(fileName, iconName, Bootstrapper.PLUGIN_ID);
	}

	@Override
	protected void initializeRegistry() {
		super.initializeRegistry();
		try {
			register("class_obj.gif","publicclass");
			register("class_default_obj.gif","defaultclass");
			register("innerclass_private_obj.gif","privateclass");
			register("innerclass_protected_obj.gif","protectedclass");
			register("int_obj.gif","publicinterface");
			register("int_default_obj.gif","defaultinterface");
			register("innerinterface_private_obj.gif","privateinterface");
			register("innerinterface_protected_obj.gif","protectedinterface");
			register("field_private_obj.gif","privatefield");
			register("field_protected_obj.gif","protectedfield");
			register("field_default_obj.gif","defaultfield");
			register("field_public_obj.gif","publicfield");
			register("methpri_obj.gif","privatemember");
			register("methpro_obj.gif","protectedmember");
			register("methdef_obj.gif","defaultmember");
			register("methpub_obj.gif","publicmember");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final AccessIconDecorator ACCESS_ICON_DECORATOR;

	public final IconProvider CLASS_ICON_PROVIDER;
	public final IconProvider FIELD_ICON_PROVIDER;
	public final IconProvider MEMBER_ICON_PROVIDER;
}
