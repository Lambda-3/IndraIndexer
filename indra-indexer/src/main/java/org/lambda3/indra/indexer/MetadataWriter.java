package org.lambda3.indra.indexer;

import org.lambda3.indra.Metadata;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.CorpusMetadata;

import java.io.File;
import java.nio.file.Paths;

public class MetadataWriter {

    public static void write(String outDir, Metadata metadata) {
        File file = Paths.get(outDir, getMetadataFileName(metadata)).toFile();
        //TODO see how to write json file.
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
