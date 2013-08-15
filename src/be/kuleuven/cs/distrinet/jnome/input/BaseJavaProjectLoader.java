package be.kuleuven.cs.distrinet.jnome.input;

import java.io.IOException;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.Operator;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectInputSource;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoaderImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.NullType;
import be.kuleuven.cs.distrinet.jnome.workspace.JarLoader;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

public class BaseJavaProjectLoader extends JarLoader {

	public BaseJavaProjectLoader(JarFile path, Java java) {
		super(path, java.plugin(ProjectConfigurator.class).binaryFileFilter(),true);
	}
	
	@Override
	protected void createInputSources() throws IOException, LookupException, InputException {
		// First create input sources for the base classes in rt.jar
		super.createInputSources();
		// The add predefined elements.
		new PredefinedElementsFactory((JavaView) view(), this).initializePredefinedElements();
	}
	



}
