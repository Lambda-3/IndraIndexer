package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;

public abstract class Preprocessor {

    public final CorpusMetadata metadata;

    public Preprocessor(CorpusMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Document process(Document document);
}
