package org.lambda3.indra.indexer.builder;

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

    PredictiveModelBuilder(String outDir, int dimensions, int windowSize,
                           int minWordFrequency, SequenceVectors.Builder<VocabWord> builder) {
        super(outDir, dimensions, windowSize, minWordFrequency);
        this.builder = builder;
    }

    @Override
    public ModelMetadata build(Corpus corpus) {

        SequenceIterator<VocabWord> iter = getSentenceIterator(corpus);
        VocabCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();

        SequenceVectors<VocabWord> vectors = this.builder.minWordFrequency(minWordFrequency).vocabCache(cache).
                windowSize(windowSize).layerSize(dimensions).iterate(iter).build();
        vectors.fit();

        ModelMetadata metadata = getModelMetadata(corpus);
        ModelWriter.save(this.outDir, metadata, cache, vectors);

        return metadata;
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

        DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(sentenceIterator)
                .tokenizerFactory(tokenizerFactory).allowMultithreading(false).build();
        return new AbstractSequenceIterator.Builder<>(transformer).build();
    }
}
