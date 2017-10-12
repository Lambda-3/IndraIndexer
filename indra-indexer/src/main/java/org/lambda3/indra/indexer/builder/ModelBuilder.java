package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;


abstract class ModelBuilder {
    protected ModelMetadata metadata;
    protected String outDir;


    public ModelBuilder(ModelMetadata metadata, String outDir) {
        this.metadata = metadata;
        this.outDir = outDir;
    }

    public abstract void build(Corpus corpus);
}

