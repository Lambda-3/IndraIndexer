package org.lambda3.indra.corpus;

import java.util.Iterator;
import java.io.File;
public class Corpus {

    public final CorpusMetadata metadata;
    private Iterator<Document> iter;
    private DocumentGenerator.ContentType type;
    private File file;
    Corpus(CorpusMetadata metadata, DocumentGenerator.ContentType type, File file) {
        this.metadata = metadata;
        this.type = type;
        this.file = file;
        reset();
    }

    public Iterator<Document> getDocumentsIterator() {
        return iter;
    }


    public void reset(){
        this.iter = new DocumentIterator(type, file);
    }
}