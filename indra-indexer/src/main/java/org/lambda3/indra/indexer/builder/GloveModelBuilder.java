package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.glove.Glove;

public class GloveModelBuilder extends PredictiveModelBuilder{

    public GloveModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        super(outDir, dimensions, windowSize, minWordFrequency, new Glove.Builder());
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public String getModelName() {
        return "GLOVE";
    }
}
