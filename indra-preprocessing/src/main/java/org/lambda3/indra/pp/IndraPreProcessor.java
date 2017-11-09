package org.lambda3.indra.pp;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusWriter;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.corpus.DocumentGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class IndraPreProcessor {

    public List<File> doCheckFiles(String corpusFiles, String patternRegex) {
        Pattern pattern = (patternRegex != null ? Pattern.compile(patternRegex) : null);
        return DocumentGenerator.defineFiles(corpusFiles.split(" "), pattern);
    }

    public void doPreProcess(CorpusMetadata metadata, String corpusFiles, String patternRegex, DocumentGenerator.FileType fileType,
                             DocumentGenerator.ContentType contentType, File outputDir) {
        CorpusWriter writer = null;
        try {
            StandardPreProcessor pp = new StandardPreProcessor(metadata);
            writer = new CorpusWriter(metadata, outputDir);

            Pattern pattern = (patternRegex != null ? Pattern.compile(patternRegex) : null);
            Iterator<Document> generator = new DocumentGenerator(fileType, contentType, pattern,
                    corpusFiles.split(" ")).iterator();

            while (generator.hasNext()) {
                Document doc = generator.next();

                Iterable<String> iterable = () -> pp.process(doc.content);
                writer.addDocument(new Document(doc.id, String.join(" ", iterable)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
