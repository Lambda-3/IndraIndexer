package org.lambda3.indra;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;

import java.util.HashMap;
import java.util.Map;

public class ModelMetadata implements Metadata {

    public static final String MODEL = "model";
    public static final String SPARSE = "sparse";
    public static final String DIMENSIONS = "dimensions";
    public static final String WINDOW_SIZE = "windowSize";
    public static final String MIN_WORD_FREQUENCY = "minWordFrequency";
    public static final String CORPUS_METADATA = "corpusMetadata";

    public final String modelName;
    public final boolean sparse;
    public final int dimensions;
    public final int windowSize;
    public final int minWordFrequency;
    public final CorpusMetadata corpusMetadata;

    public ModelMetadata(Map<String, Object> map) {
        this.modelName = (String) map.get(MODEL);
        this.sparse = (boolean) map.get(SPARSE);
        this.dimensions = (int) map.get(DIMENSIONS);
        this.windowSize = (int) map.get(WINDOW_SIZE);
        this.minWordFrequency = (int) map.get(MIN_WORD_FREQUENCY);
        Map<String, Object> corpusMetaDataMap = (Map<String, Object>) map.get(CORPUS_METADATA);
        this.corpusMetadata = CorpusMetadataBuilder.fromMap(corpusMetaDataMap);
    }

    public ModelMetadata(String modelName, boolean sparse, int dimensions, int windowSize, int minWordFrequency,
                         CorpusMetadata corpusMetadata) {
        this.modelName = modelName;
        this.sparse = sparse;
        this.dimensions = dimensions;
        this.windowSize = windowSize;
        this.minWordFrequency = minWordFrequency;
        this.corpusMetadata = corpusMetadata;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(MODEL, this.modelName);
        map.put(SPARSE, this.sparse);
        map.put(DIMENSIONS, this.dimensions);
        map.put(WINDOW_SIZE, this.windowSize);
        map.put(MIN_WORD_FREQUENCY, this.minWordFrequency);
        map.put(CORPUS_METADATA, corpusMetadata.asMap());

        return map;
    }
}
