package org.lambda3.indra.indexer.writer;

import com.google.common.io.LittleEndianDataOutputStream;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.RawSpaceModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class ModelWriter {

    protected ModelMetadata metadata;

    ModelWriter(ModelMetadata metadata) {
        this.metadata = metadata;
    }

    public void save(String outDir) {
        File modelFile = prepereTargetDirAndSaveMetadata(outDir, metadata);
        LittleEndianDataOutputStream out;

        try {
            out = new LittleEndianDataOutputStream(new FileOutputStream(modelFile));

            try {
                for (char c : String.format("%d %d\n", metadata.vocabSize, metadata.dimensions).toCharArray()) {
                    out.write((byte) c);
                }

                if (metadata.sparse) {
                    writeSparse(out);
                } else {
                    writeDense(out);
                }
                out.flush();
            } finally {
                out.close();
            }

        } catch (IOException e) {
            //TODO review here - log
            throw new RuntimeException(e);
        }
    }

    private File prepereTargetDirAndSaveMetadata(String outDir, ModelMetadata metadata) {
        File modelDir = Paths.get(outDir, metadata.modelName, metadata.corpusMetadata.language,
                metadata.corpusMetadata.corpusName).toFile();
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        MetadataIO.write(modelDir.getAbsolutePath(), metadata);
        return Paths.get(modelDir.getAbsolutePath(), RawSpaceModel.MODEL_CONTENT_FILE_NAME).toFile();
    }

    void writeTerm(String term, LittleEndianDataOutputStream stream) throws IOException {
        stream.write(term.getBytes());
        stream.write((byte) ' ');
    }


    abstract void writeSparse(LittleEndianDataOutputStream stream) throws IOException;

    abstract void writeDense(LittleEndianDataOutputStream stream) throws IOException;
}
