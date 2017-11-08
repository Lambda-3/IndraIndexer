package org.lambda3.indra.corpus;

import org.lambda3.indra.JSONUtil;

import java.io.*;
import java.nio.file.Paths;

public class CorpusWriter implements Closeable {

    public static final String CORPUS_METADATA_FILE_NAME = "corpus.metadata";
    static final String CORPUS_CONTENT_FILE_NAME = "corpus.txt";

    private Writer documentWriter;
    private boolean closed = false;

    public CorpusWriter(CorpusMetadata metadata, File baseDir) throws IOException {
        File corpusDir = Paths.get(baseDir.getAbsolutePath(), metadata.corpusName).toFile();
        if (!corpusDir.exists()) {
            corpusDir.mkdirs();
        }


        File metadataFile = Paths.get(corpusDir.getAbsolutePath(), CORPUS_METADATA_FILE_NAME).toFile();
        JSONUtil.writeMapAsJson(metadata.asMap(), metadataFile);

        File contentFile = Paths.get(corpusDir.getAbsolutePath(), CORPUS_CONTENT_FILE_NAME).toFile();
        this.documentWriter = new FileWriter(contentFile);
    }

    public void addDocument(Document document) throws IOException {
        if (!closed) {
            documentWriter.write(document.content);
            documentWriter.write("\n");
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed && documentWriter != null) {
            documentWriter.flush();
            documentWriter.close();
        }
    }
}
