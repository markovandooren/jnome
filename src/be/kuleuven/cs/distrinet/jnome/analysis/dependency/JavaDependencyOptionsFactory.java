package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import org.aikodi.chameleon.analysis.AnalysisOptions;
import org.aikodi.chameleon.analysis.dependency.DependencyOptionsFactory;
import org.aikodi.chameleon.plugin.LanguagePluginImpl;

public class JavaDependencyOptionsFactory extends LanguagePluginImpl implements DependencyOptionsFactory{

	@Override
	public AnalysisOptions createConfiguration() {
		return new JavaDependencyOptions();
	}

	@Override
	public LanguagePluginImpl clone() {
		return new JavaDependencyOptionsFactory();
	}

}
