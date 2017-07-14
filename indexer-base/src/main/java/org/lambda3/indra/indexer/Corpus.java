package org.lambda3.indra.indexer;

import java.util.Iterator;

public abstract class Corpus {

    public final CorpusMetadata metadata;

    public Corpus(CorpusMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Iterator<? extends Document> getDocumentsIterator();


}