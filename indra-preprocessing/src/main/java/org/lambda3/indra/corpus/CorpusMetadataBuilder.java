package org.lambda3.indra.corpus;

/*-
 * ==========================License-Start=============================
 * indra-preprocessing
 * --------------------------------------------------------------------
 * Copyright (C) 2017 Lambda^3
 * --------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ==========================License-End===============================
 */

import java.nio.charset.StandardCharsets;
import java.util.*;

public class CorpusMetadataBuilder {

    static final String CORPUS_NAME = "corpusName";
    static final String LANGUAGE = "language";
    static final String DESCRIPTION = "description";
    static final String ENCODING = "encoding";
    static final String APPLY_STEMMER = "applyStemmer";
    static final String REMOVE_ACCENTS = "removeAccents";
    static final String APPLY_LOWERCASE = "applyLowercase";
    static final String REPLACE_NUMBERS = "replaceNumbers";
    static final String STOP_WORDS = "stopWords";
    static final String MIN_TOKEN_LENGTH = "minTokenLength";
    static final String MAX_TOKEN_LENGTH = "maxTokenLength";
    static final String TRANSFORMERS = "transformers";

    static final Map<String, Object> DEFAULT_DATA = getDefaultData();
    private Map<String, Object> data = new HashMap<>();

    private CorpusMetadataBuilder(String corpusName, String language) {
        data.put(CORPUS_NAME, corpusName);
        data.put(LANGUAGE, language);
        data.putAll(DEFAULT_DATA);
    }

    private static Map<String, Object> getDefaultData() {
        Map<String, Object> defaultData = new HashMap<>();

        defaultData.put(DESCRIPTION, null);
        defaultData.put(ENCODING, StandardCharsets.UTF_8.name());
        defaultData.put(APPLY_STEMMER, 0L);
        defaultData.put(REMOVE_ACCENTS, true);
        defaultData.put(APPLY_LOWERCASE, true);
        defaultData.put(REPLACE_NUMBERS, true);
        defaultData.put(STOP_WORDS, Collections.EMPTY_SET);
        defaultData.put(MIN_TOKEN_LENGTH, 1L);
        defaultData.put(MAX_TOKEN_LENGTH, 100L);
        defaultData.put(TRANSFORMERS, Collections.EMPTY_MAP);

        return Collections.unmodifiableMap(defaultData);
    }

    public static CorpusMetadata fromMap(Map<String, Object> map) {
        Map<String, Object> data = new HashMap<>();
        data.putAll(DEFAULT_DATA);
        data.putAll(map);
        return new CorpusMetadata(data);
    }

    public static CorpusMetadataBuilder newCorpusMetadata(String corpusName, String language) {
        return new CorpusMetadataBuilder(corpusName, language);
    }

    public CorpusMetadataBuilder desc(String desc) {
        data.put(DESCRIPTION, desc);
        return this;
    }

    public CorpusMetadataBuilder encoding(String encoding) {
        data.put(ENCODING, encoding);
        return this;
    }

    public CorpusMetadataBuilder applyStemmer(long times) {
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

    public CorpusMetadataBuilder minTokenLength(long minTokenLength) {
        data.put(MIN_TOKEN_LENGTH, minTokenLength);
        return this;
    }

    public CorpusMetadataBuilder maxTokenLength(long maxTokenLength) {
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
