package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.iterators.AbstractSequenceIterator;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.indexer.ModelWriter;

import java.util.Map;

public class PredictiveModelBuilder extends ModelBuilder {

    public static final String MIN_WORD_FREQUENCY = "minWordFrequency";
    public static final String WINDOW_SIZE = "windoSize";
    public static final String VECTOR_SIZE = "vectorSize";

    private SequenceVectors.Builder<VocabWord> builder;

    private PredictiveModelBuilder(ModelMetadata metadata, String outDir, SequenceVectors.Builder<VocabWord> builder) {
        super(metadata, outDir);
        this.builder = builder;
    }

    public static ModelBuilder createGloveModelBuilder(ModelMetadata metadata, String outDir) {
        return new PredictiveModelBuilder(metadata, outDir, new Glove.Builder());
    }

    public static ModelBuilder createWord2VecModelBuilder(ModelMetadata metadata, String outDir) {
        return new PredictiveModelBuilder(metadata, outDir, new Word2Vec.Builder());
    }

    @Override
    public void build(Corpus corpus) {
        SequenceIterator<VocabWord> iter = getSentenceIterator(corpus);

        Map<String, Object> params = metadata.params;
        int minWordFrequency = (Integer) params.get(MIN_WORD_FREQUENCY);
        int windowSize = (Integer) params.get(WINDOW_SIZE);
        int vectorSize = (Integer) params.get(VECTOR_SIZE);

        VocabCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();

        SequenceVectors<VocabWord> vectors = this.builder.minWordFrequency(minWordFrequency).vocabCache(cache).
                windowSize(windowSize).layerSize(metadata.numOfDimensions).layerSize(vectorSize).iterate(iter).build();
        vectors.fit();

        ModelWriter.save(this.outDir, this.metadata, cache, vectors);
    }

    private SequenceIterator<VocabWord> getSentenceIterator(Corpus corpus) {

        SentenceIterator sentenceIterator = new SentenceIterator() {
            @Override
            public String nextSentence() {
                return corpus.getDocumentsIterator().next().content;
            }

            @Override
            public boolean hasNext() {
                return corpus.getDocumentsIterator().hasNext();
            }

            @Override
            public void reset() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void finish() {
                //Do noting.
            }

            @Override
            public SentencePreProcessor getPreProcessor() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setPreProcessor(SentencePreProcessor preProcessor) {
                throw new UnsupportedOperationException();
            }
        };

        DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(sentenceIterator)
                .tokenizerFactory(tokenizerFactory).allowMultithreading(false).build();
        return new AbstractSequenceIterator.Builder<>(transformer).build();
    }
}
