package be.kuleuven.cs.distrinet.jnome.tool;

import java.io.File;

import be.kuleuven.cs.distrinet.chameleon.workspace.BootstrapProjectConfig;
import be.kuleuven.cs.distrinet.chameleon.workspace.LanguageRepository;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectInitialisationListener;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfig;

public class ProjectFileCreator {

	public static void main(String[] args) {
		File file;
		if(args.length > 0) {
			file = new File(args[0]);
		} else {
			file = new File(".");
		}
		Java lang = new JavaLanguageFactory().create();
		LanguageRepository repository = new LanguageRepository();
		repository.add(lang);
		Workspace workspace = new Workspace(repository);
		ProjectConfigurator plugin = lang.plugin(ProjectConfigurator.class);
		BootstrapProjectConfig.BaseLibraryConfiguration baseLibraryConfiguration = new BootstrapProjectConfig.BaseLibraryConfiguration();
		ProjectInitialisationListener listener = null;
		JavaProjectConfig config = (JavaProjectConfig) plugin.createConfigElement("projectName", file, workspace, listener, baseLibraryConfiguration);
		
	}
}
