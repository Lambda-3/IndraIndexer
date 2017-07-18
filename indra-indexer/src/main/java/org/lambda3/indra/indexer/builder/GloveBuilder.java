package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.models.glove.Glove.Builder;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.lambda3.indra.indexer.Corpus;
import org.lambda3.indra.indexer.ModelMetadata;

import java.util.HashMap;
import java.util.Map;

public class GloveBuilder extends ModelBuilder {

    public static final String MIN_WORD_FREQUENCY = "MIN_WORD_FREQUENCY";
    public static final String WINDOW_SIZE = "WINDOW_SIZE";

    private SentenceIterator iter;
    private TokenizerFactory tokenizer;
    private Glove vec;
    private VocabCache cache;

    public GloveBuilder(ModelMetadata mmdata, String outdir) {
        super(mmdata, outdir);
    }

    @Override
    public void build(Corpus corpus) {
        tokenizer = new DefaultTokenizerFactory();


        Map<String, Object> params = mmdata.params;
        Builder builder = new Glove.Builder();

        int min_word_frequency = (Integer) params.get(MIN_WORD_FREQUENCY);
        int window_size = (Integer) params.get(WINDOW_SIZE);

        builder.minWordFrequency(min_word_frequency).vocabCache(cache).windowSize(window_size).layerSize(mmdata.numOfDimensions);
        vec = builder.iterate(iter).tokenizerFactory(tokenizer).build();

        vec.fit();

        Map<Object, Map<Integer, Double>> model = new HashMap<>();

        for (Object word : cache.words()) {
            double[] vectord = vec.getWordVector((String) word);
            Map<Integer, Double> vector = new HashMap<>();

            for (int i = 0; i < vectord.length; i++)
                vector.put(i, vectord[i]);

            model.put(word, vector);
        }


        savemodel(model, outdir, mmdata);


    }
}
