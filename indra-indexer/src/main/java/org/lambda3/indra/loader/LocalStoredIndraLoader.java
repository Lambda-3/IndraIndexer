package org.lambda3.indra.loader;

import org.lambda3.indra.indexer.MetadataWriter;

import java.io.File;
import java.nio.file.Paths;

public abstract class LocalStoredIndraLoader<V extends Vector> implements IndraLoader {

    private String baseDir;
    protected File modelDir;

    public LocalStoredIndraLoader(String baseDir) {
        this.baseDir = baseDir;
    }

    protected abstract void doLoad(VectorIterator<V> iter);

    @Override
    public void load(RawSpaceModel rsm) {
        String dirName = String.format("%s-%s-%s", rsm.modelMetadata.modelName,
                rsm.modelMetadata.corpusMetadata.language, rsm.modelMetadata.corpusMetadata.corpusName);

        this.modelDir = Paths.get(baseDir, dirName).toFile();
        if (!this.modelDir.exists()) {
            this.modelDir.mkdirs();
        }

        MetadataWriter.write(this.modelDir.getAbsolutePath(), rsm.modelMetadata);


    }

}
