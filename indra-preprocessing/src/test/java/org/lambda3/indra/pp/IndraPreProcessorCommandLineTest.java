package org.lambda3.indra.pp;

/*-
 * ==========================License-Start=============================
 * indra-preprocessing
 * --------------------------------------------------------------------
 * Copyright (C) 2017 Lambda^3
 * --------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ==========================License-End===============================
 */

import org.apache.commons.io.FileUtils;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.CorpusLoader;
import org.lambda3.indra.corpus.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IndraPreProcessorCommandLineTest {

    public void corpusTest(String inputFileExpression, String corpusName, String language, String outputDir,
                           String multiWordTokens, String regex) throws IOException {
        File outputTmpDir = null;
        try {
            outputTmpDir = Files.createTempDirectory("indra-pp-test").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> lArgs = Arrays.asList("pp", "-f", inputFileExpression, "-n", corpusName, "-l", language, "-ct", "line",
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

        IndraPreProcessorCommandLine.main(lArgs.toArray(new String[0]));

        Corpus manual = CorpusLoader.load(Paths.get(outputDir, corpusName).toFile());
        Corpus generated = CorpusLoader.load(Paths.get(outputTmpDir.getAbsolutePath(), corpusName).toFile());

        List<Document> manualDocuments = new LinkedList<>();
        manual.getDocumentsIterator().forEachRemaining(manualDocuments::add);

        List<Document> generatedDocuments = new LinkedList<>();
        generated.getDocumentsIterator().forEachRemaining(generatedDocuments::add);

        Assert.assertEquals(manualDocuments.size(), generatedDocuments.size());
        for (int i = 0; i < manualDocuments.size(); i++) {
            Assert.assertEquals(generatedDocuments.get(i), manualDocuments.get(i));
        }

        FileUtils.deleteDirectory(outputTmpDir);
    }

    @Test
    public void portugueseSmallCorpusTest() throws IOException {
        String fileTemplate = Paths.get(PlainTextDocumentGeneratorTest.INPUT_DIR, "%s").toString();
        String inputFiles = "\"" + String.format(fileTemplate, "mar_salgado.txt");
        inputFiles += " " + String.format(fileTemplate, "more/heteronimos.a") + "\"";
        String outputDir = Paths.get(PlainTextDocumentGeneratorTest.BASE_DIR, "output").toString();

        corpusTest(inputFiles, "pessoa", "pt", outputDir, null, null);
    }

    @Test
    public void englishSmallCorpusTest() throws IOException {
        String inputDir = getClass().getClassLoader().getResource("it/input").getPath();
        String outputDir = getClass().getClassLoader().getResource("it/output").getPath();
        String mwt = Paths.get(inputDir, "multiWordTokens").toString();
        corpusTest(inputDir, "sia", "en", outputDir, mwt, ".*file$");
    }
}
