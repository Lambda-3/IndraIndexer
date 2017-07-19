package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.models.glove.Glove.Builder;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.lambda3.indra.indexer.Corpus;
import org.lambda3.indra.indexer.ModelMetadata;

import java.util.HashMap;
import java.util.Map;

public class GloveBuilder extends PredictiveModelBuilder {

    public GloveBuilder(ModelMetadata mmdata, String outdir) {
        super(mmdata, outdir, null);
    }
}
