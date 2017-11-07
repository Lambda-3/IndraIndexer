package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;

public abstract class ModelBuilder {
    String outDir;
    int dimensions;
    int windowSize;
    int minWordFrequency;

    ModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        this.outDir = outDir;
        this.dimensions = dimensions;
        this.windowSize = windowSize;
        this.minWordFrequency = minWordFrequency;
    }

    public abstract void build(Corpus corpus);

    ModelMetadata getModelMetadata(Corpus corpus) {
        return new ModelMetadata(getModelName(), isSparse(), this.dimensions,
                this.windowSize, this.minWordFrequency, corpus.metadata);
    }

    public abstract boolean isSparse();

    public abstract String getModelName();
}

