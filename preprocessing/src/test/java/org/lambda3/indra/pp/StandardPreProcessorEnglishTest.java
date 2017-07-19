package org.lambda3.indra.pp;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.pp.transform.MultiWordsTransformer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class StandardPreProcessorEnglishTest {

    private String content = "Eduardo Jorge Martins Alves Sobrinho (born October 26, 1949),[1] most known simply" +
            " as Eduardo Jorge, is a Brazilian public health physician and politician. He is most known for creating " +
            "(or co-creating) the federal laws on family planning, voluntary sterilization, production of generic " +
            "drugs, regulation of asbestos use, and linking budgetary resources for Sistema Único de Saúde.[2] " +
            "300,00 and 32.55";

    @Test
    public void lowercaseAndAccentsTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                applyLowercase(true).removeAccents(true).applyStemmer(0).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));
        Assert.assertFalse(doc.content.contains("Eduardo Jorge Martins Alves Sobrinho"));
        Assert.assertTrue(doc.content.contains("eduardo jorge martins alves sobrinho"));

        Assert.assertFalse(doc.content.contains("Único"));
        Assert.assertTrue(doc.content.contains("unico"));
    }


    public Document transformerTest(String text, List<String> mwt) {
        Map<String, Collection<String>> transformers = Collections.singletonMap("MultiWordsTransformer", mwt);

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                applyLowercase(true).removeAccents(true).applyStemmer(0).transformers(transformers).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        pp.addTransformer(new MultiWordsTransformer(mwt));

        return pp.process(Document.simpleDocument(text));
    }


    @Test
    public void firstTransformerTest() {
        List<String> mwt = Arrays.asList("eduardo jorge martins alves sobrinho", "family planning",
                "sistema único de saúde");

        Document doc = transformerTest(content, mwt);

        Assert.assertFalse(doc.content.contains("eduardo jorge martins alves sobrinho"));
        Assert.assertTrue(doc.content.contains("eduardo_jorge_martins_alves_sobrinho"));

        Assert.assertFalse(doc.content.contains("family planning"));
        Assert.assertTrue(doc.content.contains("family_planning"));

        Assert.assertFalse(doc.content.contains("sistema único de saúde"));
        Assert.assertFalse(doc.content.contains("sistema_único_de_saúde"));
        Assert.assertTrue(doc.content.contains("sistema_unico_de_saude"));

    }

    @Test
    public void secondTransformerTest() {
        String text = "A simplified version of the 1040A form for individual income tax.";
        List<String> mwt = Arrays.asList("1040a form", "income tax");

        Document doc = transformerTest(text, mwt);
        Assert.assertFalse(doc.content.contains("1040a form"));
        Assert.assertTrue(doc.content.contains("1040a_form"));

        Assert.assertFalse(doc.content.contains("income tax"));
        Assert.assertTrue(doc.content.contains("income_tax"));

        Assert.assertEquals(text.toLowerCase(), doc.content.replace("_", " ") + ".");
    }

    @Test(timeOut = 10000, enabled = false)
    public void thirdTransformerTest() {
        String text = "What is 10-k? maybe it is a 10-year treasury note. Or it is 125% loan or 130/30 mutual fund" +
                " or even a 1%/10 net 30.";
        List<String> mwt = Arrays.asList("10-k", "10-year treasury note", "125% loan",
                "130/30 mutual fund", "1%/10 net 30");

        Document doc = transformerTest(text, mwt);
        Assert.assertFalse(doc.content.contains("1040 form"));
        Assert.assertTrue(doc.content.contains("1040_form"));

        Assert.assertFalse(doc.content.contains("income tax"));
        Assert.assertTrue(doc.content.contains("income_tax"));

        Assert.assertEquals(text.toLowerCase(), doc.content.replace("_", " ") + ".");
    }


    @Test
    public void stopWordsTest() {
        Set<String> stopWords = new HashSet<>(Arrays.asList("known", "voluntary", "jorge"));

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                applyLowercase(true).removeAccents(true).applyStemmer(0).stopWords(stopWords).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
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

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
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

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
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

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument("How money is written in English, $30.50 or $30,50?"));
        Assert.assertEquals(doc.content, "how money is written in english <NUMBER> or <NUMBER>");
    }

    @Test
    public void keepAlphaNumTokensTest() {
        String content = "Bla bla bla3. New bla[2], and bla bla[4].";
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "en").
                replaceNumbers(true).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));

        Assert.assertTrue(doc.content.contains("bla3"));
        Assert.assertFalse(doc.content.contains("bla 2"));
        Assert.assertFalse(doc.content.contains("bla2"));
        Assert.assertFalse(doc.content.contains("bla 4"));
        Assert.assertFalse(doc.content.contains("bla4"));

    }
}
