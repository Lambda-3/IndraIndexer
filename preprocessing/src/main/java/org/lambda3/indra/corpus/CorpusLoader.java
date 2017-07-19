package org.lambda3.indra.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;

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
        return new Corpus(metadata, new DocumentIterator(new BufferedReader(new FileReader(contentFile))));
    }
}

class DocumentIterator implements Iterator<Document> {
    private BufferedReader reader;
    private String line;

    DocumentIterator(BufferedReader reader) throws IOException {
        this.reader = reader;
        nextNonEmptyLine();
    }

    private void nextNonEmptyLine() throws IOException {
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = (line != null);
        if (!hasNext && reader != null) {
            try {
                this.reader.close();
                this.reader = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return hasNext;
    }

    @Override
    public Document next() {
        if (!hasNext()) {
            return null;
        }

        Document doc = Document.simpleDocument(line);
        try {
            nextNonEmptyLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }
}
