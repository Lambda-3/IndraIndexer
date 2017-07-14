package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.Corpus;

public class PlainTextCorpusBuilder extends CorpusBuilder {

    public PlainTextCorpusBuilder(String name, String lang) {
        super(name, lang);
    }

    @Override
    public Corpus getCorpus() {
        return null;
    }
}
