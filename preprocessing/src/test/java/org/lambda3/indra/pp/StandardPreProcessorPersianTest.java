package org.lambda3.indra.pp;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.corpus.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class StandardPreProcessorPersianTest {
    private String content = "فارسی یا پارسی ی ي  یکی از زبان\u200Cهای هندواروپایی در شاخهٔ زبان\u200Cهای ایرانی جنوب غربی است" +
            " که در کشورهای ایران٬ افغانستان،[۳] تاجیکستان[4] و ازبکستان[۵] به آن سخن می\u200Cگویند." +
            " فارسی زبان رسمی کشورهای ایران و تاجیکستان و یکی از دو زبان٬ًٍ رسمی افغانستان (در کنار پشتو) است." +
            " زبان رسمی کشور هندوستان نیز تا پیش از ورود استعمار انگلیس، فارسی بود.[۶]";


    @Test(enabled = false)
    public void stemmerTest() {

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").
                applyLowercase(true).removeAccents(true).applyStemmer(1).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);

        String word = "کتاب\u200Cها";
        Document doc = pp.process(Document.simpleDocument(word));
        // https://github.com/htaghizadeh/PersianStemmer-Java

        Assert.assertFalse(doc.content.contains("کتاب\u200Cها"));
        Assert.assertTrue(doc.content.contains("کتاب"));

    }

    @Test
    public void keepAlphaNumTokensTest() {

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").
                applyLowercase(true).removeAccents(true).applyStemmer(0).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        String content = " 2سیامک34";
        Document doc = pp.process(Document.simpleDocument(content));

        Assert.assertFalse(doc.content.contains("2 سیامک 34"));
        Assert.assertTrue(doc.content.contains("2سیامک34"));

    }

    @Test(enabled = false)
    public void accentTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").removeAccents(true).applyStemmer(0).build();
        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));
        //TODO please implement me.


    }

    @Test(enabled = false)
    public void transformerTest() {
        List<String> mwt = Arrays.asList("فارسی زبان رسمی کشورهای ایران", "زبان رسمی کشور هندوستان",
                "که در کشورهای ایران٬ افغانستان،[۳]");

        Map<String, Collection<String>> transformers = Collections.singletonMap("MultiWordsTransformer", mwt);

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").
                applyLowercase(true).removeAccents(true).applyStemmer(0).transformers(transformers).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);

        Document doc = pp.process(Document.simpleDocument(content));

        Assert.assertFalse(doc.content.contains("فارسی زبان رسمی کشورهای ایران"));
        Assert.assertTrue(doc.content.contains("فارسی_زبان_رسمی_کشورهای_ایران"));

        Assert.assertFalse(doc.content.contains("زبان رسمی کشور هندوستان"));
        Assert.assertTrue(doc.content.contains("زبان_رسمی_کشور_هندوستان"));
        Assert.assertFalse(doc.content.contains("که در کشورهای ایران افغانستان ۳"));
        Assert.assertTrue(doc.content.contains("که_در_کشورهای_ایران__افغانستان__۳"));
    }

    @Test
    public void stopWordsTest() {
        Set<String> stopWords = new HashSet<>(Arrays.asList("یکی", "در", "یا"));

        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").
                applyLowercase(true).removeAccents(true).applyStemmer(0).stopWords(stopWords).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));


        Assert.assertTrue(doc.content.startsWith("فارسی"));
        Assert.assertFalse(doc.content.startsWith("فارسی یا پارسی"));
        Assert.assertTrue(doc.content.startsWith("فارسی پارسی"));

        Assert.assertFalse(doc.content.contains("در"));
        Assert.assertFalse(doc.content.contains("یکی"));
    }

    @Test(enabled = false)
    public void keepNumbersTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").
                replaceNumbers(false).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));

        // Convert Persian & Arabic number to English

        Assert.assertTrue(doc.content.contains("4"));
        Assert.assertTrue(doc.content.contains("۳"));
        Assert.assertTrue(doc.content.contains("۵"));
        Assert.assertTrue(doc.content.contains("۶"));
        Assert.assertFalse(doc.content.contains("<NUMBER>"));

    }

    @Test(enabled = false)
    public void removeNumbersTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").
                replaceNumbers(true).build();


        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument(content));

        // Convert Persian & Arabic number to English


        Assert.assertFalse(doc.content.contains("4"));
        Assert.assertFalse(doc.content.contains("۳"));
        Assert.assertFalse(doc.content.contains("۵"));
        Assert.assertFalse(doc.content.contains("۶"));
        Assert.assertTrue(doc.content.contains("<NUMBER>"));
    }

    @Test
    public void removeFloatNumbersTest() {
        CorpusMetadata metadata = CorpusMetadataBuilder.newCorpusMetadata("corpus-name", "fa").
                replaceNumbers(true).build();

        StandardPreProcessor pp = new StandardPreProcessor(metadata);
        Document doc = pp.process(Document.simpleDocument("How money is written in English, $30.50 or $30,50?"));

        //TODO write this one here.
    }

    public static void main(String[] args) {
        StandardPreProcessorPersianTest test = new StandardPreProcessorPersianTest();


    }

}


