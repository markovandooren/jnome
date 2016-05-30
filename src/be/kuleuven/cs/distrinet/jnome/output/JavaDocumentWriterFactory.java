package be.kuleuven.cs.distrinet.jnome.output;

import org.aikodi.chameleon.plugin.build.DocumentWriter;
import org.aikodi.chameleon.plugin.build.DocumentWriterFactory;

/**
 * @author Titouan Vervack
 */
public class JavaDocumentWriterFactory implements DocumentWriterFactory {

    @Override
    public DocumentWriter writer() {
        return new JavaDocumentWriter(".java");
    }
}
