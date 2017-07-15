package org.lambda3.indra.indexer;

import java.util.Map;

public class ModelMetadata {

    public static final String MODEL_NAME = "modelName";
    public static final String SPARSE = "sparse";
    public static final String DIMENSIONS = "numOfDimensions";
    public static final String PARAMS = "params";
    public static final String CORPUS_METADATA = "corpusMetadata";

    public final String modelName;
    public final boolean sparse;
    public final int numOfDimensions;
    public final Map<String, Object> params;
    public final CorpusMetadata corpusMetadata;

    public ModelMetadata(String modelName, boolean sparse, int numOfDimensions, CorpusMetadata corpusMetadata, Map<String, Object> params) {
        this.modelName = modelName;
        this.sparse = sparse;
        this.numOfDimensions = numOfDimensions;
        this.corpusMetadata = corpusMetadata;
        this.params = params;
    }
}
