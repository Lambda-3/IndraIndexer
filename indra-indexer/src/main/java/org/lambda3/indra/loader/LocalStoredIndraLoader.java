package org.lambda3.indra.loader;

import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.model.ModelMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public abstract class LocalStoredIndraLoader<V extends Vector> implements IndraLoader<V> {

    protected String modelDir;

    public LocalStoredIndraLoader(String baseDir, ModelMetadata metadata) {
        File modelDirFile = Paths.get(baseDir, metadata.getConciseName()).toFile();
        if (!modelDirFile.exists()) {
            modelDirFile.mkdirs();
        }

        this.modelDir = modelDirFile.getAbsolutePath();
    }

    protected abstract void doLoad(VectorIterator<V> iter);

    @Override
    public void load(RawSpaceModel<V> rsm) throws FileNotFoundException {
        MetadataIO.write(this.modelDir, rsm.modelMetadata);
        doLoad(rsm.getVectorIterator());
    }

}
