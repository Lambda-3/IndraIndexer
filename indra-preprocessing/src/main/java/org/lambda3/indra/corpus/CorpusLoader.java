package org.lambda3.indra.corpus;

import org.lambda3.indra.MetadataIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CorpusLoader {

    public static Corpus load(File corpusBaseDir) throws IOException {
        CorpusMetadata metadata = MetadataIO.load(corpusBaseDir.getAbsolutePath(), CorpusMetadata.class);

        File contentFile = Paths.get(corpusBaseDir.getAbsolutePath(), CorpusWriter.CORPUS_CONTENT_FILE_NAME).toFile();
        return new Corpus(metadata, DocumentGenerator.ContentType.LINE, contentFile);
    }
}


