package org.lambda3.indra.indexer;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class StreamCorpus extends Corpus {

    private Iterator<Document> iter;
    private Writer out;

    StreamCorpus(CorpusMetadata metadata, Iterator<Document> iter, Writer out) {
        super(metadata);
        this.iter = iter;
        this.out = out;
    }

    @Override
    public Iterator<Document> getDocumentsIterator() {
        if (iter == null) {
            throw new RuntimeException("No document to read! Tthis instance is write-only.");
        }
        return iter;
    }

    @Override
    public void addDocument(Document document) throws IOException {
        if (out == null) {
            throw new RuntimeException("can't add new documents! This instance is read-only.");
        }
        out.write(document.content);
        out.write("\n");
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        }
    }
}
