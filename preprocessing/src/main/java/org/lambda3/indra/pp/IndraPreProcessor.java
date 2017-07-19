package org.lambda3.indra.pp;

import org.lambda3.indra.corpus.*;

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
        CorpusWriter writer = null;
        try {
            StandardPreProcessor pp = new StandardPreProcessor(metadata);
            writer = new CorpusWriter(metadata, outputDir);

            Iterator<Document> generator = null;
            switch (fileType) {
                case "text":
                    Pattern pattern = (patternRegex != null ? Pattern.compile(patternRegex) : null);
                    generator = new PlainTextDocumentGenerator(contentType, pattern,
                            corpusFiles.split(" ")).iterator();
                    break;
                case "wiki":
                    generator = new WikipediaDocumentGenerator().iterator();
                default:
                    System.out.println(contentType + " is not a valid contentType. It should be 'text' or 'wiki'");
            }


            while (generator.hasNext()) {
                Document doc = generator.next();
                writer.addDocument(pp.process(doc));
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
