package org.lambda3.indra;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;

import java.util.Map;

public class ModelMetadataBuilder {

    public static ModelMetadata fromMap(Map<String, Object> map) {
        String modelName = (String) map.get(ModelMetadata.MODEL);
        boolean sparse = (boolean) map.get(ModelMetadata.SPARSE);
        int dimensions = (int) map.get(ModelMetadata.DIMENSIONS);
        int windowSize = (int) map.get(ModelMetadata.WINDOW_SIZE);
        int minWordFrequency = (int) map.get(ModelMetadata.MIN_WORD_FREQUENCY);
        Map<String, Object> corpusMetadataMap = (Map<String, Object>) map.get(ModelMetadata.CORPUS_METADATA);
        CorpusMetadata corpusMetadata = CorpusMetadataBuilder.fromMap(corpusMetadataMap);

        return new ModelMetadata(modelName, sparse, dimensions, windowSize, minWordFrequency, corpusMetadata);
    }
}
