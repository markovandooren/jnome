package be.kuleuven.cs.distrinet.jnome.workspace;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7LanguageFactory;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfigurator;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.workspace.*;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;

public class BaseLibraryJavaProjectConfigurator extends JavaProjectConfigurator {

    protected boolean baseLibraryInParent = false;

    /**
     * Set baseLibraryInParent to true when running from class files outside eclipse.
     * Set baseLibraryInParent to false when running from class files inside eclipse.
     * When run from a jar, the base library is located correctly.
     */
    public BaseLibraryJavaProjectConfigurator() {
        super(Java7LanguageFactory.javaBaseJar());
    }

    public void searchInParent() {
        baseLibraryInParent = true;
    }

    public static class BaseDirectoryLoader extends DirectoryScanner {

        public BaseDirectoryLoader(String root, FileDocumentLoaderFactory factory, SafePredicate<? super String> filter) {
            super(root, filter, true, factory);
        }

    }

    public class LanguageBaseLibraryConfigurator extends BaseLibraryConfigurator {

        private String langHome;

        public LanguageBaseLibraryConfigurator(Language language, String langHome) {
            super(language);
            this.langHome = langHome;
        }


        @Override
        protected void addBaseScanner(View view) {
            // The base loader for Java already creates the primitive types
            // and load the Java base library.
            // Therefore, we only have to add a loader for the base Neio library file.

            // 1) For a normal jar release, the base library is always stored inside neio.jar
            // 2) For the Eclipse plugin, the base library is included inside the plugin jar
            //    and the neio-base.jar is included as well.
            // 3) When the Eclipse plugin is run as a nested Eclipse during development,
            //    we use a Directory loader to read the sources directly.
            URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
            try {
                String path = URLDecoder.decode(url.getFile(), "UTF-8");
                SafePredicate<? super String> sourceFileFilter = sourceFileFilter();
                if (path.endsWith(".jar")) {
                    JarFile jarFile = new JarFile(path);
                    view.addBinary(new ZipScanner(jarFile, sourceFileFilter));
                } else {
                    File root = new File(url.toURI());
                    if (baseLibraryInParent) {
                        root = root.getParentFile();
                    }
                    File file = new File(root, langHome);
                    view.addBinary(new BaseDirectoryLoader(file.getAbsolutePath(), new LazyJavaFileInputSourceFactory(), sourceFileFilter));
                }
            } catch (Exception e) {
                throw new ConfigException(e);
            }
        }
    }
}
