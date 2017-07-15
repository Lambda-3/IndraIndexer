package org.lambda3.indra.indexer;

import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;

public class CorpusLoader {

    static final String METADATA_FILE_NAME = "corpus.metadata";
    static final String CONTENT_FILE_NAME = "corpus.txt";

    public Corpus load(File corpusDir) throws IOException {
        File metadataFile = Paths.get(corpusDir.getAbsolutePath(), METADATA_FILE_NAME).toFile();
        File contentFile = Paths.get(corpusDir.getAbsolutePath(), CONTENT_FILE_NAME).toFile();

        CorpusMetadata metadata = loadMetadata(metadataFile);
        return new StreamCorpus(metadata, new DocumentIterator(new BufferedReader(new FileReader(contentFile))), null);
    }

    private static CorpusMetadata loadMetadata(File file) {
        //TODO implement me. Load the json file...
        return null;
    }

    static void writeMetadata(CorpusMetadata metadata, OutputStream stream) {
        //TODO implement me. write the metadata in the outputstream...
    }

    public void save(Corpus corpus, File corpusDir) {
        if (!corpusDir.exists()) {
            corpusDir.mkdirs();
        } else if (corpusDir.isFile()) {
            throw new RuntimeException(String.format("corpusDir can't point to a file. (%s)",
                    corpusDir.getAbsoluteFile()));
        }

        File metadataFile = Paths.get(corpusDir.getAbsolutePath(), METADATA_FILE_NAME).toFile();
        File contentFile = Paths.get(corpusDir.getAbsolutePath(), CONTENT_FILE_NAME).toFile();
    }
}

class DocumentIterator implements Iterator<Document> {

    private BufferedReader reader;
    private String line;

    DocumentIterator(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.line = reader.readLine();
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
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }
}
