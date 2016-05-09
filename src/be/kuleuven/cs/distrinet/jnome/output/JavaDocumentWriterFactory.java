package be.kuleuven.cs.distrinet.jnome.output;

import be.ugent.chameleonsupport.build.DocumentWriterFactory;
import org.aikodi.chameleon.plugin.build.DocumentWriter;

/**
 * @author Titouan Vervack
 */
public class JavaDocumentWriterFactory implements DocumentWriterFactory {

    @Override
    public DocumentWriter writer() {
        return new JavaDocumentWriter(".java");
    }
}
