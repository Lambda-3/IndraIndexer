package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.exception.IndraRuntimeException;
import org.lambda3.indra.model.ModelMetadata;

public abstract class ModelBuilder {
    //TODO write warning when a paramter is NOT_APPLIED but is was provided by the command line.
    protected static final int NOT_APPLIED = -1;

    String outDir;
    int dimensions;
    int windowSize;
    int minWordFrequency;
    long vocabSize;

    ModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        this.outDir = outDir;
        this.dimensions = dimensions;
        this.windowSize = windowSize;
        this.minWordFrequency = minWordFrequency;
    }

    public abstract ModelMetadata build(Corpus corpus);

    ModelMetadata getModelMetadata(Corpus corpus) {
        if (dimensions <= 0) {
            throw new IndraRuntimeException("dimensions should be greater than 0 (dim=" + dimensions + ")");
        }

        if (vocabSize <= 0) {
            throw new IndraRuntimeException("vocabSize should be greater than 0 (vocabSize=" + vocabSize + ")");
        }


        return new ModelMetadata(getModelName(), isSparse(), this.dimensions, this.vocabSize, this.windowSize,
                this.minWordFrequency, corpus.metadata);
    }

    public abstract boolean isSparse();

    public abstract String getModelName();
}

