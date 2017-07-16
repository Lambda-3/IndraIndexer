package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;

import java.util.Iterator;

public class WikipediaDocumentGenerator extends DocumentGenerator {

    public WikipediaDocumentGenerator(CorpusMetadata metadata) {
        super(metadata);
    }

    @Override
    public Iterator<Document> iterator() {
        return null;
    }


}
