package org.lambda3.indra.indexer;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class CorpusMetadataBuilder {

    public static final String CORPUS_NAME = "corpusName";
    public static final String LANGUAGE = "language";
    public static final String DESCRIPTION = "description";
    public static final String ENCODING = "encoding";
    public static final String APPLY_STEMMER = "applyStemmer";
    public static final String REMOVE_ACCENTS = "removeAccents";
    public static final String APPLY_LOWERCASE = "applyLowercase";
    public static final String REPLACE_NUMBERS = "replaceNumbers";
    public static final String STOP_WORDS = "stopWords";
    public static final String MIN_TOKEN_LENGTH = "minTokenLength";
    public static final String MAX_TOKEN_LENGTH = "maxTokenLength";
    public static final String TRANSFORMERS = "transformers";

    private Map<String, Object> data = new HashMap<>();

    private CorpusMetadataBuilder(String corpusName, String language) {
        data.put(CORPUS_NAME, corpusName);
        data.put(LANGUAGE, language);
        data.put(DESCRIPTION, null);
        data.put(ENCODING, StandardCharsets.UTF_8.name());
        data.put(APPLY_STEMMER, 0);
        data.put(REMOVE_ACCENTS, true);
        data.put(APPLY_LOWERCASE, true);
        data.put(REPLACE_NUMBERS, true);
        data.put(STOP_WORDS, Collections.EMPTY_SET);
        data.put(MIN_TOKEN_LENGTH, 1);
        data.put(MAX_TOKEN_LENGTH, 100);
        data.put(TRANSFORMERS, Collections.EMPTY_MAP);
    }

    public static CorpusMetadataBuilder newCorpusMetadata(String corpusName, String language) {
        return new CorpusMetadataBuilder(corpusName, language);
    }

    public CorpusMetadataBuilder desc(String description) {
        data.put(DESCRIPTION, description);
        return this;
    }

    public CorpusMetadataBuilder encoding(String encoding) {
        data.put(ENCODING, encoding);
        return this;
    }

    public CorpusMetadataBuilder applyStemmer(int times) {
        data.put(APPLY_STEMMER, times);
        return this;
    }

    public CorpusMetadataBuilder removeAccents(boolean removeAccents) {
        data.put(REMOVE_ACCENTS, removeAccents);
        return this;
    }

    public CorpusMetadataBuilder applyLowercase(boolean applyLowercase) {
        data.put(APPLY_LOWERCASE, applyLowercase);
        return this;
    }

    public CorpusMetadataBuilder replaceNumbers(boolean replaceNumbers) {
        data.put(REPLACE_NUMBERS, replaceNumbers);
        return this;
    }

    public CorpusMetadataBuilder stopWords(Set<String> stopWords) {
        data.put(STOP_WORDS, stopWords);
        return this;
    }

    public CorpusMetadataBuilder minTokenLength(int minTokenLength) {
        data.put(MIN_TOKEN_LENGTH, minTokenLength);
        return this;
    }

    public CorpusMetadataBuilder maxTokenLength(int maxTokenLength) {
        data.put(MAX_TOKEN_LENGTH, maxTokenLength);
        return this;
    }

    public CorpusMetadataBuilder transformers(Map<String, Collection<String>> transformers) {
        data.put(TRANSFORMERS, transformers);
        return this;
    }

    public CorpusMetadata build() {
        this.data = Collections.unmodifiableMap(data);
        return new CorpusMetadata(data);
    }
}
