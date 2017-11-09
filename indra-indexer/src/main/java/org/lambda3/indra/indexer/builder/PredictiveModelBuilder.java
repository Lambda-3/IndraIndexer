package org.lambda3.indra.indexer.builder;

import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.iterators.AbstractSequenceIterator;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.indexer.ModelWriter;
import org.lambda3.indra.model.ModelMetadata;

public abstract class PredictiveModelBuilder extends ModelBuilder {

    private SequenceVectors.Builder<VocabWord> builder;

    PredictiveModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        super(outDir, dimensions, windowSize, minWordFrequency);
        this.builder = getBuilder();
    }

    @Override
    public ModelMetadata build(Corpus corpus) {

        VocabCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        SequenceVectors<VocabWord> vectors = builder.iterations(1).minWordFrequency(minWordFrequency).
                windowSize(windowSize).layerSize(dimensions).vocabCache(cache).iterate(getSentenceIterator(corpus)).build();
        vectors.fit();

        ModelMetadata metadata = getModelMetadata(corpus);
        ModelWriter.save(this.outDir, metadata, cache, vectors);

        return metadata;
    }

    VectorsConfiguration getConfiguration() {
        VectorsConfiguration config = new VectorsConfiguration();
        config.setSeed(31);
        return config;
    }

    private SentenceIterator getIterator(Corpus corpus) {

        return new SentenceIterator() {

            @Override
            public synchronized String nextSentence() {
                return corpus.getDocumentsIterator().next().content;
            }

            @Override
            public boolean hasNext() {
                return corpus.getDocumentsIterator().hasNext();
            }

            @Override
            public void reset() {
                corpus.reset();
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

    }

    private SequenceIterator<VocabWord> getSentenceIterator(Corpus corpus) {
        SentenceIterator sentenceIterator = getIterator(corpus);

        DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(sentenceIterator)
                .tokenizerFactory(tokenizerFactory).allowMultithreading(false).build();

        return new AbstractSequenceIterator.Builder<>(transformer).build();
    }

    public abstract SequenceVectors.Builder<VocabWord> getBuilder();
}
