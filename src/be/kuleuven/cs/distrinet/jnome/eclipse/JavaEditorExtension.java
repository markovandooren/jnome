package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.Modifier;
import org.aikodi.chameleon.eclipse.connector.EclipseEditorExtension;
import org.aikodi.chameleon.eclipse.presentation.treeview.CompositeIconProvider;
import org.aikodi.chameleon.eclipse.presentation.treeview.DefaultIconProvider;
import org.aikodi.chameleon.eclipse.presentation.treeview.IconProvider;
import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.oo.variable.MemberVariable;
import org.aikodi.chameleon.plugin.output.Syntax;
import org.aikodi.chameleon.support.modifier.Abstract;
import org.aikodi.chameleon.support.modifier.Constructor;
import org.aikodi.chameleon.support.modifier.Final;
import org.aikodi.chameleon.support.modifier.Interface;
import org.aikodi.chameleon.support.modifier.Native;
import org.aikodi.chameleon.support.modifier.Private;
import org.aikodi.chameleon.support.modifier.Protected;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.chameleon.support.modifier.Static;

import be.kuleuven.cs.distrinet.jnome.core.modifier.Default;

/**
 * @author Marko van Dooren
 * @author Koen Vanderkimpen
 */
public class JavaEditorExtension extends EclipseEditorExtension {

	public JavaEditorExtension() {
		ACCESS_ICON_DECORATOR = new AccessIconDecorator();
		CLASS_ICON_PROVIDER = new ClassIconProvider(ACCESS_ICON_DECORATOR);
		FIELD_ICON_PROVIDER = new DefaultIconProvider("field", MemberVariable.class,ACCESS_ICON_DECORATOR);
		MEMBER_ICON_PROVIDER = new DefaultIconProvider("member", Member.class,ACCESS_ICON_DECORATOR);
	}

	public JavaEditorExtension clone() {
		return new JavaEditorExtension();
	}

	@Override
	public String label(Element element) {
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
			} else result = super.label(element);
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
