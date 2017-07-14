package org.lambda3.indra.indexer;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class CorpusMetadata {

    public final String corpusName;
    public final String language;
    public final String description;
    public final int numOfDocuments;
    public final String encoding;
    public final boolean sparse = false;
    public final boolean binary = true;
    public final int applyStemmer = 3;
    public final boolean removeAccents = true;
    public final boolean applyLowercase = true;
    public final boolean applyStopWords = true;
    public final int minTokenLength = 3;
    public final int maxTokenLength = 100;
    public final int dimensions = 300;
    public final Set<String> stopWords = Collections.emptySet();
    public final Map<String, String> more = Collections.emptyMap();

    private Map<String, Object> data;

    CorpusMetadata(Map<String, Object> data) {
        corpusName = (String) data.get(CorpusMetadataBuilder.CORPUS_NAME);
        language = (String) data.get(CorpusMetadataBuilder.LANGUAGE);
        description = (String) data.get(CorpusMetadataBuilder.DESCRIPTION);
        numOfDocuments = (int) data.get(CorpusMetadataBuilder.NUM_OF_DOCUMENTS);
        encoding = (String) data.get(CorpusMetadataBuilder.ENCODING);
        //TODO continue...
    }

    public Map<String, Object> asMap() {
        return data;
    }
}
