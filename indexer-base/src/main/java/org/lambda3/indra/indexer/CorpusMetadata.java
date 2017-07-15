package org.lambda3.indra.indexer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CorpusMetadata {

    public final String corpusName;
    public final String language;
    public final String description;
    public final String encoding;
    public final int applyStemmer;
    public final boolean removeAccents;
    public final boolean applyLowercase;
    public final boolean replaceNumbers;
    public final int minTokenLength;
    public final int maxTokenLength;
    public final Set<String> stopWords;
    public final Map<String, Collection<String>> transformers;

    private Map<String, Object> data;

    CorpusMetadata(Map<String, Object> data) {
        this.data = data;
        corpusName = (String) data.get(CorpusMetadataBuilder.CORPUS_NAME);
        language = (String) data.get(CorpusMetadataBuilder.LANGUAGE);
        description = (String) data.get(CorpusMetadataBuilder.DESCRIPTION);
        encoding = (String) data.get(CorpusMetadataBuilder.ENCODING);
        applyStemmer = (int) data.get(CorpusMetadataBuilder.APPLY_STEMMER);
        removeAccents = (boolean) data.get(CorpusMetadataBuilder.REMOVE_ACCENTS);
        applyLowercase = (boolean) data.get(CorpusMetadataBuilder.APPLY_LOWERCASE);
        replaceNumbers = (boolean) data.get(CorpusMetadataBuilder.REPLACE_NUMBERS);
        minTokenLength = (int) data.get(CorpusMetadataBuilder.MIN_TOKEN_LENGTH);
        maxTokenLength = (int) data.get(CorpusMetadataBuilder.MAX_TOKEN_LENGTH);
        stopWords = (Set<String>) data.get(CorpusMetadataBuilder.STOP_WORDS);
        transformers = (Map<String, Collection<String>>) data.get(CorpusMetadataBuilder.TRANSFORMERS);
    }

    public Map<String, Object> asMap() {
        return data;
    }
}
