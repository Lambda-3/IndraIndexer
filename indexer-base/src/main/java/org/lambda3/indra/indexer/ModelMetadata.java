package org.lambda3.indra.indexer;

public class ModelMetadata {

    public static final String MODEL_NAME = "modelName";
    public final String modelName;

    public static final String SPARSE = "sparse";
    public final boolean sparse;

    public static final String DIMENSIONS = "numOfDimensions";
    public final int numOfDimensions;

    //TODO ADD MORE HERE, BECAUSE THERE ARE LOTS OF PARAMS SPECIFIC FOR ANY TYPE OF MODELs.
    public final CorpusMetadata corpusMetadata;

    public ModelMetadata(String modelName, boolean sparse, int numOfDimensions, CorpusMetadata corpusMetadata) {
        this.modelName = modelName;
        this.sparse = sparse;
        this.numOfDimensions = numOfDimensions;
        this.corpusMetadata = corpusMetadata;
    }
}
