package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.Word2Vec.Builder;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.lambda3.indra.indexer.Corpus;

import java.util.HashMap;
import java.util.Map;

public class Word2VectorBuilder implements ModelBuilder {

    public static int VECTOR_LENGTH = 300;
    public static int MIN_WORD_FREQUENCY = 5;
    public static int WINDOW_SIZE = 5;

    private SentenceIterator iter;
    private TokenizerFactory tokenizer;
    private Word2Vec vec;
    private VocabCache cache;

    @Override
    public void build(Corpus corpus) {
        //this.iter = new LinesSentenceIterator(corpus);
        tokenizer = new DefaultTokenizerFactory();

        //cache = new InMemoryLookupCache.Builder().lr(2e-5).vectorLength(VECTOR_LENGTH).build();

        Builder builder = new Word2Vec.Builder();
        builder.minWordFrequency(MIN_WORD_FREQUENCY).vocabCache(cache).windowSize(WINDOW_SIZE).layerSize(VECTOR_LENGTH);

        vec = builder.iterate(iter).tokenizerFactory(tokenizer).build();

        //vec.setCache(cache);
        vec.fit();

        for (Object word : cache.words()) {
            double[] vectord = vec.getWordVector((String) word);
            Map<Integer, Double> vector = new HashMap<>();

            for (int i = 0; i < vectord.length; i++)
                vector.put(i, vectord[i]);

            //dao.insertVector(word, vector);
        }
    }
}
