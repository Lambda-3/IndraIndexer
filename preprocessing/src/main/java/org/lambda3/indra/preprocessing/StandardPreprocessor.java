package org.lambda3.indra.preprocessing;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class StandardPreprocessor extends Preprocessor {
    private static Logger logger = LoggerFactory.getLogger(StandardPreprocessor.class);

    private Tokenizer tokenizer;
    private TokenStream tokenStream;

    public StandardPreprocessor(CorpusMetadata metadata) {
        super(metadata);
        tokenizer = new StandardTokenizer();
        tokenStream = createStream(metadata, tokenizer);
    }

    @Override
    public Document process(Document doc) {
        if (doc.content == null || doc.content.isEmpty()) {
            return doc;
        }

        String content = metadata.applyLowercase ? doc.content.toLowerCase() : doc.content;

        if (!transformers.isEmpty()) {
            StringBuilder sbContent = new StringBuilder(content);
            transformers.stream().forEach(t -> t.transform(sbContent));
            content = sbContent.toString();
        }

        List<String> tokens = new ArrayList<>();
        try (StringReader reader = new StringReader(content)) {
            tokenizer.setReader(reader);
            CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(cattr.toString());
            }
        } catch (IOException e) {
            logger.error("Error analyzing {}", content, e);
        } finally {
            try {
                tokenStream.end();
                tokenStream.close();
            } catch (IOException e) {
                logger.error("Error closing stream {}", e);
            }
        }

        return new Document(doc.id, String.join(" ", tokens));
    }

    private TokenStream getStopFilter(String lang, Set<String> metadataStopWords, TokenStream stream) {

        if (metadataStopWords != null && !metadataStopWords.isEmpty()) {
            return new StopFilter(stream, new CharArraySet(metadataStopWords, false));

        } else {
            try {
                InputStream in = ClassLoader.getSystemResourceAsStream(lang.toLowerCase() + ".stopwords");
                if (in != null) {
                    logger.debug("Loading Stop words for lang={}", lang);
                    CharArraySet stopWords = new CharArraySet(30, true);
                    try (BufferedReader bin = new BufferedReader(new InputStreamReader(in))) {
                        String line;
                        String[] parts;
                        while ((line = bin.readLine()) != null) {
                            parts = line.split(Pattern.quote("|"));
                            line = parts[0].trim();

                            if (line.length() > 0) {
                                stopWords.add(line);
                            }
                        }
                        return new StopFilter(stream, stopWords);
                    }
                } else {
                    logger.warn("No stop words found for lang={}", lang);
                }
            } catch (Exception e) {
                logger.error("Error creating stop filter for lang={}", lang, e);
            }
        }

        return stream;
    }

    private TokenStream createStream(CorpusMetadata metadata, Tokenizer tokenizer) {
        TokenStream stream = new StandardFilter(tokenizer);
        stream = new LengthFilter(stream, metadata.minTokenLength, metadata.maxTokenLength);

        if (metadata.applyStopWords) {
            stream = getStopFilter(metadata.language, /*TODO metadata.getStopWords()*/null, stream);
        }

        if (metadata.applyStemmer > 0) {
            stream = getStemmerFilter(metadata.language, metadata.applyStemmer, stream);
        }

        if (metadata.removeAccents) {
            stream = new ASCIIFoldingFilter(stream);
        }

        return stream;
    }

    private TokenStream getStemmerFilter(String lang, int times, TokenStream stream) {
        SnowballProgram stemmer = getStemmer(lang);

        if (stemmer != null && times > 0) {
            for (int i = 0; i < times; i++) {
                stream = new SnowballFilter(stream, stemmer);
            }
        }

        return stream;
    }

    private static SnowballProgram getStemmer(String lang) {
        switch (lang.toUpperCase()) {
            case "EN":
                return new EnglishStemmer();
            case "PT":
                return new PortugueseStemmer();
            case "ES":
                return new SpanishStemmer();
            case "DE":
                return new GermanStemmer();
            case "FR":
                return new FrenchStemmer();
            case "SV":
                return new SwedishStemmer();
            case "IT":
                return new ItalianStemmer();
            case "NL":
                return new DutchStemmer();
            case "RU":
                return new RussianStemmer();

            case "AR":
            case "FA":
            case "ZH":
            case "KO":
                logger.warn("No stemmer is being used for '{}'", lang);
                return null;
        }
        return null;
    }
}
