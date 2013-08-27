package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import be.kuleuven.cs.distrinet.chameleon.analysis.AnalysisOptions;
import be.kuleuven.cs.distrinet.chameleon.analysis.dependency.DependencyOptionsFactory;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePluginImpl;

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
