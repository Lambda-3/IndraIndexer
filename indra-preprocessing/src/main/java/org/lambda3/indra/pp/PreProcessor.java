package org.lambda3.indra.pp;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.pp.transform.Transformer;

import java.util.Iterator;

public interface PreProcessor {

    abstract Document process(Document document);

    abstract Iterator<String> process(String text);

    CorpusMetadata getCorpusMetadata();
}
