package org.lambda3.indra.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

public class StreamCorpus extends Corpus {

    private Iterator<? extends Document> iter;
    private Writer out;

    StreamCorpus(CorpusMetadata metadata, Iterator<Document> iter, Writer out) {
        super(metadata);
        this.iter = iter;
        this.out = out;
    }

    @Override
    public Iterator<? extends Document> getDocumentsIterator() {
        if(iter == null) {
            throw new RuntimeException("No document to read! Tthis instance is write-only.");
        }
        return iter;
    }

    @Override
    public void addDocument(Document document) throws IOException {
        if(out == null) {
            throw new RuntimeException("can't add new documents! This instance is read-only.");
        }
        out.write(document.content);
        out.write("\n");
    }

    @Override
    public void close() throws IOException {
        if(out != null) {
            out.flush();
            out.close();
        }
    }
}
