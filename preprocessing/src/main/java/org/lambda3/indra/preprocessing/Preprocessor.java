package org.lambda3.indra.preprocessing;

import org.lambda3.indra.client.ModelMetadata;
import org.lambda3.indra.indexer.Document;

public abstract class Preprocessor {

    public final ModelMetadata metadata;

    public Preprocessor(ModelMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Document process(Document document);
}
