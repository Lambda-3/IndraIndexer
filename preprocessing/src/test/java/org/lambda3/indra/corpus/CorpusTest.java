package org.lambda3.indra.corpus;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CorpusTest {

    private static final String CORPORA_PATH = CorpusTest.class.getClassLoader().getResource("corpora").getPath();

    public Corpus loadCorpus(String corpusName, String path) throws IOException {
        CorpusLoader loader = new CorpusLoader(new File(path));
        return loader.load(corpusName);
    }

    public List<Document> getDocuments(Corpus corpus) {
        List<Document> docs = new LinkedList<>();
        Iterator<Document> iter = corpus.getDocumentsIterator();
        while (iter.hasNext()) {
            docs.add(iter.next());
        }

        return docs;
    }

    @Test
    public void loadEnglishTextCorpusTest() throws IOException {
        final String FIRST_DOC = "But if thou raisest the strong cudgel of justice,";

        Corpus corpus = loadCorpus("simpleEnglishCorpus", CORPORA_PATH);
        List<Document> docs = getDocuments(corpus);

        Assert.assertEquals(docs.size(), 3);
        Assert.assertEquals(docs.get(0).content, FIRST_DOC);
    }


    @Test
    public void loadPortugueseTextCorpusTest() throws IOException {
        final String LAST_DOC = "Torcida mais fiel não pode haver";

        Corpus corpus = loadCorpus("simplePortugueseCorpus", CORPORA_PATH);
        List<Document> docs = getDocuments(corpus);

        Assert.assertEquals(docs.size(), 13);
        Assert.assertEquals(docs.get(docs.size() - 1).content, LAST_DOC);
        Assert.assertTrue(docs.get(10).content.contains("bravo Leão"));
        Assert.assertTrue(docs.get(2).content.endsWith("."));
    }

    public void writeTextCorpusTest(String corpusName) throws IOException {
        Corpus originalCorpus = loadCorpus(corpusName, CORPORA_PATH);
        List<Document> inDocs = getDocuments(originalCorpus);

        File tmpDir = Files.createTempDirectory("indra-").toFile();
        tmpDir.deleteOnExit();
        CorpusWriter writer = new CorpusWriter(originalCorpus.metadata, tmpDir);

        for (Document doc : inDocs) {
            writer.addDocument(doc);
        }

        writer.close();

        Corpus writtenCorpus = loadCorpus(corpusName, tmpDir.getAbsolutePath());
        List<Document> writtenDocs = getDocuments(writtenCorpus);

        Assert.assertEquals(inDocs.size(), writtenDocs.size());
        for (int i = 0; i < inDocs.size(); i++) {
            Assert.assertEquals(inDocs.get(i).content, writtenDocs.get(i).content);
        }
    }

    @Test
    public void voidWriteEnglishTxtCorpusTest() throws IOException {
        writeTextCorpusTest("simpleEnglishCorpus");
    }

    @Test
    public void voidWritePortugueseTxtCorpusTest() throws IOException {
        writeTextCorpusTest("simplePortugueseCorpus");
    }
}
