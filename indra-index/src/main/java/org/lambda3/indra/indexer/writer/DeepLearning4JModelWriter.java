package org.lambda3.indra.indexer.writer;

import com.google.common.io.LittleEndianDataOutputStream;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.lambda3.indra.model.ModelMetadata;

import java.io.IOException;

public class DeepLearning4JModelWriter extends ModelWriter {

    private VocabCache<VocabWord> cache;
    private SequenceVectors<VocabWord> vectors;

    public DeepLearning4JModelWriter(ModelMetadata metadata, VocabCache<VocabWord> cache, SequenceVectors<VocabWord> vectors) {
        super(metadata);
        this.cache = cache;
        this.vectors = vectors;
    }

    @Override
    void writeSparse(LittleEndianDataOutputStream stream) throws IOException {
        throw new UnsupportedOperationException("DeepLearning4J does not support sparse models.");
    }

    @Override
    void writeDense(LittleEndianDataOutputStream stream) throws IOException {
        for (String word : cache.words()) {
            writeTerm(word, stream);

            double[] vector = vectors.getWordVector(word);
            for (double i : vector) {
                stream.writeFloat((float) i);
            }
        }
    }
}
