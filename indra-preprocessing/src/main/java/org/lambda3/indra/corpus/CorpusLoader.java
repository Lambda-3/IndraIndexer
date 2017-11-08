package org.lambda3.indra.corpus;

import org.lambda3.indra.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class CorpusLoader {

    public static Corpus load(File corpusBaseDir) throws IOException {

        File metadataFile = Paths.get(corpusBaseDir.getAbsolutePath(), CorpusWriter.CORPUS_METADATA_FILE_NAME).toFile();
        Map<String, Object> map = JSONUtil.loadJSONAsMap(metadataFile);
        CorpusMetadata metadata = CorpusMetadataBuilder.fromMap(map);

        File contentFile = Paths.get(corpusBaseDir.getAbsolutePath(), CorpusWriter.CORPUS_CONTENT_FILE_NAME).toFile();
        return new Corpus(metadata, DocumentGenerator.ContentType.LINE, contentFile);
    }
}


