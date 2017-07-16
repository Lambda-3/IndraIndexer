package org.lambda3.indra.preprocessing;


import org.lambda3.indra.indexer.Corpus;
import org.lambda3.indra.indexer.CorpusLoader;
import org.lambda3.indra.indexer.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class IndraPreProcessorTest {

    @Test
    public void portugueseSmallCorpusTest() throws IOException {

        String outputTmpDir = null;
        try {
            outputTmpDir = Files.createTempDirectory("indra-pp-test").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String corpusName = "pessoa";
        String fileTemplate = Paths.get(PlainTextDocumentGeneratorTest.INPUT_DIR, "%s").toString();
        String inputFiles = "\"" + String.format(fileTemplate, "mar_salgado.txt");
        inputFiles += " " + String.format(fileTemplate, "more/heteronimos.a") + "\"";

        String[] args = {"pp", "-f", inputFiles, "-n", corpusName, "-l", "pt", "-ct", "line",
                "-ft", "text", "-o", outputTmpDir};

        IndraPreProcessor.main(args);

        File outputDir = Paths.get(PlainTextDocumentGeneratorTest.BASE_DIR, "output").toFile();
        Corpus manual = new CorpusLoader(outputDir).load(corpusName);
        Corpus generated = new CorpusLoader(new File(outputTmpDir)).load(corpusName);

        List<Document> manualDocuments = new LinkedList<>();
        manual.getDocumentsIterator().forEachRemaining(manualDocuments::add);

        List<Document> generatedDocuments = new LinkedList<>();
        generated.getDocumentsIterator().forEachRemaining(generatedDocuments::add);

        Assert.assertEquals(manualDocuments.size(), generatedDocuments.size());
        for (int i = 0; i < manualDocuments.size(); i++) {
            Assert.assertEquals(manualDocuments.get(i), generatedDocuments.get(i));
        }
    }
}
