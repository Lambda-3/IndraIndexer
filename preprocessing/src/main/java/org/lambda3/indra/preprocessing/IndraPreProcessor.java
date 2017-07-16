package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.Corpus;
import org.lambda3.indra.indexer.CorpusLoader;
import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class IndraPreProcessor {

    public List<File> doCheckFiles(String corpusFiles, String patternRegex) {
        Pattern pattern = (patternRegex != null ? Pattern.compile(patternRegex) : null);
        return PlainTextDocumentGenerator.defineFiles(corpusFiles.split(" "), pattern);
    }

    public void doPreProcess(CorpusMetadata metadata, String corpusFiles, String patternRegex, String fileType,
                             PlainTextDocumentGenerator.ContentType contentType, File outputDir) {
        Corpus newCorpus = null;
        try {
            StandardPreprocessor pp = new StandardPreprocessor(metadata);
            newCorpus = new CorpusLoader(outputDir).getWriterCorpus(metadata);

            Iterator<Document> generator = null;
            switch (fileType) {
                case "text":
                    Pattern pattern = (patternRegex != null ? Pattern.compile(patternRegex) : null);
                    generator = new PlainTextDocumentGenerator(metadata, contentType, pattern,
                            corpusFiles.split(" ")).iterator();
                    break;
                case "wiki":
                    generator = new WikipediaDocumentGenerator(metadata).iterator();
                default:
                    System.out.println(contentType + " is not a valid contentType. It should be 'text' or 'wiki'");
            }


            while (generator.hasNext()) {
                Document doc = generator.next();
                newCorpus.addDocument(pp.process(doc));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (newCorpus != null) {
                try {
                    newCorpus.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
