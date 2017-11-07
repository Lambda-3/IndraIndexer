package org.lambda3.indra.loader;

import org.lambda3.indra.JSONUtil;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.indexer.ModelWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Map;

public class RawSpaceModel {

    public final ModelMetadata modelMetadata;
    private File vectorFileAbsolutePath;

    public RawSpaceModel(String baseDir) {
        this.vectorFileAbsolutePath = Paths.get(baseDir, ModelWriter.MODEL_CONTENT_FILE_NAME).toFile();
        File metadataFile = Paths.get(baseDir, ModelWriter.MODEL_METADATA_FILE_NAME).toFile();
        Map<String, Object> map = JSONUtil.loadJSONAsMap(metadataFile);
        this.modelMetadata = new ModelMetadata(map);
    }

    public boolean isSparse() {
        return modelMetadata.sparse;
    }

    public VectorIterator getVectorIterator() throws FileNotFoundException {
        return new VectorIterator(isSparse(), this.vectorFileAbsolutePath);
    }
}
