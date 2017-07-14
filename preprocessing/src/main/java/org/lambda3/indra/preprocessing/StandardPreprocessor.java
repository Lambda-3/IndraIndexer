package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;

public class StandardPreprocessor extends Preprocessor {

    public StandardPreprocessor(CorpusMetadata metadata) {
        super(metadata);
    }

    @Override
    public Document process(Document document) {
        return null;
    }
}
