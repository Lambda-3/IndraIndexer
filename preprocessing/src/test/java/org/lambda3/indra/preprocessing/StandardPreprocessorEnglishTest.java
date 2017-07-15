package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.CorpusMetadataBuilder;
import org.lambda3.indra.indexer.Document;
import org.lambda3.indra.preprocessing.transform.MultiWordsTransformer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class StandardPreprocessorEnglishTest {

    private String content = "Eduardo Jorge Martins Alves Sobrinho (born October 26, 1949),[1] most known simply" +
            " as Eduardo Jorge, is a Brazilian public health physician and politician. He is most known for creating " +
            "(or co-creating) the federal laws on family planning, voluntary sterilization, production of generic " +
            "drugs, regulation of asbestos use, and linking budgetary resources for Sistema Único de Saúde.[2] " +
            "300,00 and 32.55";

    @Test
    public void lowercaseAndAccentsTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                applyLowercase(true).removeAccents(true).applyStemmer(0).build();

        StandardPreprocessor pp = new StandardPreprocessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));
        Assert.assertFalse(doc.content.contains("Eduardo Jorge Martins Alves Sobrinho"));
        Assert.assertTrue(doc.content.contains("eduardo jorge martins alves sobrinho"));

        Assert.assertFalse(doc.content.contains("Único"));
        Assert.assertTrue(doc.content.contains("unico"));
    }

    @Test
    public void transformerTest() {
        List<String> mwt = Arrays.asList("eduardo jorge martins alves sobrinho", "family planning",
                "sistema único de saúde");
        Map<String, Collection<String>> transformers = Collections.singletonMap("MultiWordsTransformer", mwt);

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                applyLowercase(true).removeAccents(true).applyStemmer(0).transformers(transformers).build();

        StandardPreprocessor pp = new StandardPreprocessor(metadata);
        pp.addTransformer(new MultiWordsTransformer(mwt));

        Document doc = pp.process(Document.simpleDocument(content));

        Assert.assertFalse(doc.content.contains("eduardo jorge martins alves sobrinho"));
        Assert.assertTrue(doc.content.contains("eduardo_jorge_martins_alves_sobrinho"));

        Assert.assertFalse(doc.content.contains("family planning"));
        Assert.assertTrue(doc.content.contains("family_planning"));

        Assert.assertFalse(doc.content.contains("sistema único de saúde"));
        Assert.assertFalse(doc.content.contains("sistema_único_de_saúde"));
        Assert.assertTrue(doc.content.contains("sistema_unico_de_saude"));
    }

    @Test
    public void stopWordsTest() {
        Set<String> stopWords = new HashSet<>(Arrays.asList("known", "voluntary", "jorge"));

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                applyLowercase(true).removeAccents(true).applyStemmer(0).stopWords(stopWords).build();

        StandardPreprocessor pp = new StandardPreprocessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));

        Assert.assertTrue(doc.content.startsWith("eduardo"));
        Assert.assertFalse(doc.content.startsWith("eduardo jorge"));
        Assert.assertTrue(doc.content.startsWith("eduardo martins alves"));

        Assert.assertFalse(doc.content.contains("voluntary"));
        Assert.assertFalse(doc.content.contains("known"));
    }

    @Test
    public void keepNumbersTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                replaceNumbers(false).build();

        StandardPreprocessor pp = new StandardPreprocessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));

        Assert.assertTrue(doc.content.contains("26"));
        Assert.assertTrue(doc.content.contains("1949"));
        Assert.assertTrue(doc.content.contains("1"));
        Assert.assertTrue(doc.content.contains("2"));
        Assert.assertFalse(doc.content.contains("<NUMBER>"));

    }

    @Test
    public void removeNumbersTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                replaceNumbers(true).build();

        StandardPreprocessor pp = new StandardPreprocessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));

        Assert.assertFalse(doc.content.contains("26"));
        Assert.assertFalse(doc.content.contains("1949"));
        Assert.assertFalse(doc.content.contains("1"));
        Assert.assertFalse(doc.content.contains("2"));
        Assert.assertTrue(doc.content.contains("<NUMBER>"));
    }

    @Test
    public void removeFloatNumbersTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                replaceNumbers(true).build();

        StandardPreprocessor pp = new StandardPreprocessor(metadata);
        Document doc = pp.process(Document.simpleDocument("How money is written in English, $30.50 or $30,50?"));

        //TODO write this one here.
    }
}
