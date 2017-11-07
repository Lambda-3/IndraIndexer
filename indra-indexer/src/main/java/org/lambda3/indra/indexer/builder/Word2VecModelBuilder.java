package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.word2vec.Word2Vec;

public class Word2VecModelBuilder extends PredictiveModelBuilder {

    public Word2VecModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        super(outDir, dimensions, windowSize, minWordFrequency, new Word2Vec.Builder());
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public String getModelName() {
        return "WORD2VEC";
    }
}
