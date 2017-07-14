package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.Corpus;

public abstract class CorpusBuilder {
    public final String name;
    public final String lang;

    public CorpusBuilder(String name, String lang) {
        this.name = name;
        this.lang = lang;
    }

    public abstract Corpus getCorpus();
}

