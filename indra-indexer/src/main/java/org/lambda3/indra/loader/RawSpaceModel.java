package org.lambda3.indra.loader;

import org.lambda3.indra.indexer.ModelWriter;
import org.lambda3.indra.model.ModelMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class RawSpaceModel<V extends Vector> {

    public final ModelMetadata modelMetadata;
    private File vectorFileAbsolutePath;
    private Class<V> clazz;

    public RawSpaceModel(String baseDir, ModelMetadata modelMetadata, Class<V> clazz) {
        this.vectorFileAbsolutePath = Paths.get(baseDir, ModelWriter.MODEL_CONTENT_FILE_NAME).toFile();
        this.modelMetadata = modelMetadata;
        this.clazz = clazz;
    }

    public boolean isSparse() {
        return modelMetadata.sparse;
    }

    public VectorIterator<V> getVectorIterator() throws FileNotFoundException {
        return new VectorIterator<V>(this.vectorFileAbsolutePath, modelMetadata.dimensions, clazz);
    }
}
