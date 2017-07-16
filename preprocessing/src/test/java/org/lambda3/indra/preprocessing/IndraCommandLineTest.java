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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IndraCommandLineTest {

    public void corpusTest(String inputFileExpression, String corpusName, File outputDir,
                           String multiWordTokens, String regex) throws IOException {
        File outputTmpDir = null;
        try {
            outputTmpDir = Files.createTempDirectory("indra-pp-test").toFile();
            outputTmpDir.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> lArgs = Arrays.asList("pp", "-f", inputFileExpression, "-n", corpusName, "-l", "pt", "-ct", "line",
                "-ft", "text", "-o", outputTmpDir.toString());
        lArgs = new LinkedList<>(lArgs);
        if (multiWordTokens != null) {
            lArgs.add("--multi-word-tokens");
            lArgs.add(multiWordTokens);
        }

        if (regex != null) {
            lArgs.add("-r");
            lArgs.add(regex);
        }

        IndraCommandLine.main(lArgs.toArray(new String[0]));

        Corpus manual = new CorpusLoader(outputDir).load(corpusName);
        Corpus generated = new CorpusLoader(outputTmpDir).load(corpusName);

        List<Document> manualDocuments = new LinkedList<>();
        manual.getDocumentsIterator().forEachRemaining(manualDocuments::add);

        List<Document> generatedDocuments = new LinkedList<>();
        generated.getDocumentsIterator().forEachRemaining(generatedDocuments::add);

        Assert.assertEquals(manualDocuments.size(), generatedDocuments.size());
        for (int i = 0; i < manualDocuments.size(); i++) {
            Assert.assertEquals(generatedDocuments.get(i), manualDocuments.get(i));
        }
    }

    @Test
    public void portugueseSmallCorpusTest() throws IOException {
        String fileTemplate = Paths.get(PlainTextDocumentGeneratorTest.INPUT_DIR, "%s").toString();
        String inputFiles = "\"" + String.format(fileTemplate, "mar_salgado.txt");
        inputFiles += " " + String.format(fileTemplate, "more/heteronimos.a") + "\"";
        File outputDir = Paths.get(PlainTextDocumentGeneratorTest.BASE_DIR, "output").toFile();

        corpusTest(inputFiles, "pessoa", outputDir, null, null);
    }

    @Test
    public void englishSmallCorpusTest() throws IOException {
        String inputDir = getClass().getClassLoader().getResource("it/input").getPath();
        String outputDir = getClass().getClassLoader().getResource("it/output").getPath();
        String mwt = Paths.get(inputDir, "multiWordTokens").toString();
        corpusTest(inputDir, "sia", new File(outputDir), mwt, ".*file$");
    }
}
