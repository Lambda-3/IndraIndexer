package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;

public abstract class ModelBuilder {
    //TODO write warning when a paramter is NOT_APPLIED but is was provided by the command line.
    protected static final int NOT_APPLIED = -1;

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

    public abstract ModelMetadata build(Corpus corpus);

    ModelMetadata getModelMetadata(Corpus corpus) {
        return new ModelMetadata(getModelName(), isSparse(), this.dimensions,
                this.windowSize, this.minWordFrequency, corpus.metadata);
    }

    public abstract boolean isSparse();

    public abstract String getModelName();
}

