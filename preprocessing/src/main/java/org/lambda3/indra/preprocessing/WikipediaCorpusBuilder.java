package org.lambda3.indra.preprocessing;

import org.lambda3.indra.client.ModelMetadata;
import org.lambda3.indra.indexer.Corpus;

public class WikipediaCorpusBuilder extends CorpusBuilder {

    public WikipediaCorpusBuilder(ModelMetadata metadata) {
        super(metadata);
    }

    @Override
    public Corpus getCorpus() {
        return null;
    }
}
