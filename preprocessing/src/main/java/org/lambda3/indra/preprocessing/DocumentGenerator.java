package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.Corpus;
import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;

import java.util.Iterator;

public abstract class DocumentGenerator {

    public final CorpusMetadata metadata;

    public DocumentGenerator(CorpusMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Iterator<Document> iterator();
}

