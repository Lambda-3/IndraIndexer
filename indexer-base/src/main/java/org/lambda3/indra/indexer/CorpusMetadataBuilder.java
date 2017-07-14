package org.lambda3.indra.indexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CorpusMetadataBuilder {

    public static final String CORPUS_NAME = "corpusName";
    public static final String LANGUAGE = "language";
    public static final String DESCRIPTION = "description";
    public static final String NUM_OF_DOCUMENTS = "numOfDocuments";
    public static final String ENCODING = "encoding";
    public static final String APPLY_STEMMER = "applyStemmer";
    //TODO lemma???? //TODO MORE???? transformers???
    public static final String REMOVE_ACCENTS = "removeAccents";
    public static final String APPLY_LOWERCASE = "applyLowercase";
    public static final String APPLY_STOP_WORDS = "applyStopWords";
    public static final String MIN_TOKEN_LENGTH = "minTokenLength";
    public static final String MAX_TOKEN_LENGTH = "maxTokenLength";

    private Map<String, Object> data = new HashMap<>();

    private CorpusMetadataBuilder(String corpusName, String language) {
        data.put(CORPUS_NAME, corpusName);
        data.put(LANGUAGE, language);
    }

    public static CorpusMetadataBuilder newCorpusMetadata(String corpusName, String language) {
        return newCorpusMetadata(corpusName, language);
    }

    public CorpusMetadataBuilder desc(String description) {
        data.put(DESCRIPTION, description);
        return this;
    }

    public CorpusMetadataBuilder numOfDocs(int numOfDocuments) {
        data.put(NUM_OF_DOCUMENTS, numOfDocuments);
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

    public CorpusMetadataBuilder applyStopWords(boolean applyStopWords) {
        data.put(APPLY_STOP_WORDS, applyStopWords);
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

    public CorpusMetadata build() {
        this.data = Collections.unmodifiableMap(data);
        return new CorpusMetadata(data);
    }
}
