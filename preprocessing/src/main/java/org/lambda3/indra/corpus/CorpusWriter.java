package org.lambda3.indra.corpus;

import java.io.*;
import java.nio.file.Paths;

public class CorpusWriter implements Closeable {

    private Writer documentWriter;
    private boolean closed = false;

    public CorpusWriter(CorpusMetadata metadata, File baseDir) throws IOException {
        File corpusDir = Paths.get(baseDir.getAbsolutePath(), metadata.corpusName).toFile();
        if (!corpusDir.exists()) {
            corpusDir.mkdirs();
        }

        MetadataIO.writeCorpusMetadata(metadata, corpusDir);
        File contentFile = Paths.get(corpusDir.getAbsolutePath(), CorpusLoader.CORPUS_CONTENT_FILE_NAME).toFile();
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
