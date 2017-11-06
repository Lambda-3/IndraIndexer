package org.lambda3.indra.corpus;

import org.lambda3.indra.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class CorpusLoader {
    static final String CORPUS_METADATA_FILE_NAME = "corpus.metadata";
    static final String CORPUS_CONTENT_FILE_NAME = "corpus.txt";

    private File baseDir;

    public CorpusLoader(File baseDir) {
        this.baseDir = baseDir;
    }

    public Corpus load(String corpusName) throws IOException {
        File corpusBaseDir = Paths.get(baseDir.getAbsolutePath(), corpusName).toFile();


        File metadataFile = Paths.get(corpusBaseDir.getAbsolutePath(), CORPUS_METADATA_FILE_NAME).toFile();
        Map<String, Object> map = JSONUtil.loadJSONAsMap(metadataFile);
        CorpusMetadata metadata = CorpusMetadataBuilder.fromMap(map);

        File contentFile = Paths.get(baseDir.getAbsolutePath(), corpusName, CORPUS_CONTENT_FILE_NAME).toFile();
        return new Corpus(metadata, DocumentGenerator.ContentType.LINE, contentFile);
    }

    public Corpus read(CorpusMetadata metadata) throws IOException {

        return new Corpus(metadata, DocumentGenerator.ContentType.LINE, baseDir);
    }
}


