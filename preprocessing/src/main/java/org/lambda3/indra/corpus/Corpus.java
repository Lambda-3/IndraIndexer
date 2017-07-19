package org.lambda3.indra.corpus;

import java.util.Iterator;

public class Corpus {

    public final CorpusMetadata metadata;
    private final Iterator<Document> iter;

    Corpus(CorpusMetadata metadata, Iterator<Document> iter) {
        this.metadata = metadata;
        this.iter = iter;
    }

    public Iterator<Document> getDocumentsIterator() {
        return iter;
    }
}