package org.lambda3.indra.corpus;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.Document;

import java.util.Iterator;

public abstract class DocumentGenerator {

    public abstract Iterator<Document> iterator();
}

