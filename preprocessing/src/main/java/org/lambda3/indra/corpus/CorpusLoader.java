package org.lambda3.indra.corpus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CorpusLoader {

    static final String CORPUS_CONTENT_FILE_NAME = "corpus.txt";

    private File baseDir;

    public CorpusLoader(File baseDir) {
        this.baseDir = baseDir;
    }

    public Corpus load(String corpusName) throws IOException {
        File corpusBaseDir = Paths.get(baseDir.getAbsolutePath(), corpusName).toFile();

        File contentFile = Paths.get(baseDir.getAbsolutePath(), corpusName, CORPUS_CONTENT_FILE_NAME).toFile();

        CorpusMetadata metadata = MetadataIO.loadCorpusMetadata(corpusBaseDir);
        return new Corpus(metadata, new DocumentIterator(DocumentGenerator.ContentType.LINE, contentFile));
    }
}


