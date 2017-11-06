package org.lambda3.indra.indexer;

import org.lambda3.indra.JSONUtil;
import org.lambda3.indra.Metadata;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.CorpusLoader;
import org.lambda3.indra.corpus.CorpusMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MetadataWriter {

    public static void write(String outDir, Metadata metadata) {
        try {
            File metadataFile = Paths.get(outDir, getMetadataFileName(metadata)).toFile();
            JSONUtil.writeMapAsJson(metadata.asMap(), metadataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMetadataFileName(Metadata metadata) {
        if (metadata instanceof CorpusMetadata) {
            return "corpus.metadata";
        } else if (metadata instanceof ModelMetadata) {
            return "model.metadata";
        } else {
            throw new IllegalStateException("metadata type not supported."); //TODO more here.
        }
    }
}
