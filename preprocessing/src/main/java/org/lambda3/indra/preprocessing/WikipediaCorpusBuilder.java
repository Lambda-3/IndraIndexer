package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.Corpus;

public class WikipediaCorpusBuilder extends CorpusBuilder {

    public WikipediaCorpusBuilder(String name, String lang) {
        super(name, lang);
    }

    @Override
    public Corpus getCorpus() {
        return null;
    }
}
