package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.indexer.ModelWriter;
import org.lambda3.indra.model.ModelMetadata;

public class GloveModelBuilder extends PredictiveModelBuilder {

    public GloveModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        super(outDir, dimensions, windowSize, minWordFrequency);
    }

    @Override
    public SequenceVectors.Builder<VocabWord> getBuilder() {
        return new Glove.Builder(getConfiguration());
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
