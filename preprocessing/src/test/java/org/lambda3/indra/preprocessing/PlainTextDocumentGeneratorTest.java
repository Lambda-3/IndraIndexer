package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.CorpusMetadataBuilder;
import org.lambda3.indra.indexer.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class PlainTextDocumentGeneratorTest {

    public static final String BASE_DIR = PlainTextDocumentGeneratorTest.class.getClassLoader()
            .getResource("plainTextCorpus").getPath();
    public static final String INPUT_DIR = Paths.get(BASE_DIR, "input").toString();

    public List<Document> countDocuments(String dir, PlainTextDocumentGenerator.ContentType type, String regex) {
        CorpusMetadata md = CorpusMetadataBuilder.newCorpusMetadata("pessoa", "pt").build();

        Pattern pattern = (regex != null ? Pattern.compile(regex) : null);
        PlainTextDocumentGenerator builder = new PlainTextDocumentGenerator(md, type, pattern, dir);
        Iterator<Document> generator = builder.iterator();

        List<Document> docs = new LinkedList<>();
        while (generator.hasNext()) {
            docs.add(generator.next());

        }
        return docs;
    }

    @Test
    public void countDocumentAllTypeFileTest() {
        List<Document> docs = countDocuments(INPUT_DIR, PlainTextDocumentGenerator.ContentType.FILE_DOCUMENT, null);
        Assert.assertEquals(docs.size(), 6);
    }

    @Test(timeOut = 20000)
    public void countDocumentMoreTypeFileTest() {
        String moreDir = Paths.get(INPUT_DIR, "more").toString();
        List<Document> docs = countDocuments(moreDir, PlainTextDocumentGenerator.ContentType.FILE_DOCUMENT, null);
        Assert.assertEquals(docs.size(), 3);
    }

    @Test
    public void marSalgadoDocumentTest() {
        String file = Paths.get(INPUT_DIR, "mar_salgado.txt").toString();
        List<Document> docs = countDocuments(file, PlainTextDocumentGenerator.ContentType.LINE_DOCUMENT, null);

        Assert.assertEquals(docs.size(), 12);
        Assert.assertTrue(docs.get(0).content.contains("salgado"));
        Assert.assertTrue(docs.get(11).content.contains("espelhou"));
    }

    @Test(timeOut = 20000)
    public void tabacariaDocumentTest() {
        String file = Paths.get(INPUT_DIR, "tabacaria.txt").toString();
        List<Document> docs = countDocuments(file, PlainTextDocumentGenerator.ContentType.LINE_DOCUMENT, null);

        Assert.assertEquals(docs.size(), 167);
        Assert.assertTrue(docs.get(26).content.contains("aprendizagem"));
    }

    @Test
    public void regex1FilterDocumentTest() {
        List<Document> docs = countDocuments(INPUT_DIR, PlainTextDocumentGenerator.ContentType.FILE_DOCUMENT, "bio(.*)");

        Assert.assertEquals(docs.size(), 2);
        for (Document doc : docs) {
            Assert.assertTrue(doc.content.startsWith("Fernando"));
        }
    }

    @Test
    public void regex2FilterDocumentTest() {
        List<Document> docs = countDocuments(INPUT_DIR, PlainTextDocumentGenerator.ContentType.FILE_DOCUMENT, "(.*).txt$");

        Assert.assertEquals(docs.size(), 3);
        for (Document doc : docs) {
            Assert.assertTrue(doc.content.contains("!"));
        }
    }
}
