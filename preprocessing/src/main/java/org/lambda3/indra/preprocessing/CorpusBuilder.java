package org.lambda3.indra.preprocessing;

import org.lambda3.indra.client.ModelMetadata;
import org.lambda3.indra.indexer.Corpus;

public abstract class CorpusBuilder {
    public final ModelMetadata metadata;

    public CorpusBuilder(ModelMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Corpus getCorpus();
}

