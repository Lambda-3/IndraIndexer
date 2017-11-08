package org.lambda3.indra.indexer;

import org.lambda3.indra.JSONUtil;
import org.lambda3.indra.Metadata;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.corpus.CorpusWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class MetadataWriter {

    private static String getFileName(Class<? extends Metadata> clazz) {
        return clazz == CorpusMetadata.class ? CorpusWriter.CORPUS_METADATA_FILE_NAME :
                ModelWriter.MODEL_METADATA_FILE_NAME;
    }

    public static void write(String modelDir, Metadata metadata) {
        try {
            JSONUtil.writeMapAsJson(metadata.asMap(), Paths.get(modelDir, getFileName(metadata.getClass())).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <M extends Metadata> M load(String dir, Class<M> clazz) {


        File metadataFile = Paths.get(dir, getFileName(clazz)).toFile();
        Map<String, Object> map = JSONUtil.loadJSONAsMap(metadataFile);

        if (clazz == CorpusMetadata.class) {
            return (M) CorpusMetadataBuilder.fromMap(map);
        } else {
            return (M) new ModelMetadata(map);
        }
    }
}
