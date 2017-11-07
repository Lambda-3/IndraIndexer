package org.lambda3.indra.pp;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.Document;

import java.util.Iterator;
import java.util.Objects;

public class StandardPreProcessor implements PreProcessor {
    private final CorpusMetadata metadata;

    public StandardPreProcessor(CorpusMetadata metadata) {
        this.metadata = Objects.requireNonNull(metadata);
    }

    @Override
    public Document process(Document doc) {
        Iterable<String> iterable = () -> process(doc.content);
        return new Document(doc.id, String.join(" ", iterable));
    }

    @Override
    public Iterator<String> process(String text) {
        return new StandardPreProcessorIterator(metadata, text);
    }

    @Override
    public CorpusMetadata getCorpusMetadata() {
        return metadata;
    }
}
