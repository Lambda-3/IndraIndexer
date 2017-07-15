package org.lambda3.indra.indexer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public abstract class Corpus implements Closeable {

    public final CorpusMetadata metadata;

    public Corpus(CorpusMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Iterator<? extends Document> getDocumentsIterator();

    public abstract void addDocument(Document document) throws IOException;


}