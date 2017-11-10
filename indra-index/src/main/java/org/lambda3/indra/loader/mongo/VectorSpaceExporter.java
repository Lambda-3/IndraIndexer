package org.lambda3.indra.loader.mongo;

/*-
 * ==========================License-Start=============================
 * indra-index
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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.math3.linear.RealVector;
import org.bson.Document;
import org.bson.types.Binary;
import org.lambda3.indra.core.codecs.BinaryCodecs;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.exception.IndraException;
import org.lambda3.indra.indexer.ModelWriter;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.pp.StandardPreProcessorIterator;

import java.io.IOException;
import java.util.Set;

public class VectorSpaceExporter {
    private static final String TERM_FIELD_NAME = "term";
    private static final String VECTOR_FIELD_NAME = "vector";
    private static final String TERMS_COLL_NAME = "terms";
    private static final String METADATA_COLL_NAME = "metadata";

    private static final int SPARSE_DIM = 5000000;

    private MongoCollection<Document> termsColl;
    private MongoClient mongoClient;

    private String lang;
    private String model;
    private String corpus;
    private int dimensions;
    private boolean legacy;
    private boolean sparse;
    private long vocabSize;
    private int minWordFreq;
    private int windowSize;

    public VectorSpaceExporter(String mongoURI, String lang, String model, String corpus) {
        this.lang = lang;
        this.model = model;
        this.corpus = corpus;

        this.legacy = corpus.equalsIgnoreCase("wiki-2014");
        this.sparse = model.equalsIgnoreCase("esa");
        this.dimensions = model.equalsIgnoreCase("esa") ? SPARSE_DIM : 300;
        this.minWordFreq = model.toLowerCase().endsWith("sa") ? -1 : 5;
        this.windowSize = minWordFreq;

        String dbName = String.format("%s-%s-%s", model, lang, corpus).toLowerCase();
        this.mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        this.termsColl = mongoClient.getDatabase(dbName).getCollection(TERMS_COLL_NAME);
        this.vocabSize = termsColl.count();
    }

    public Iterable<MongoVector> getVectors() {
        FindIterable<Document> docs = termsColl.find();
        return docs.map(doc -> new MongoVector(doc.getString(TERM_FIELD_NAME), unmarshall(doc, dimensions, legacy, sparse)));
    }

    public ModelMetadata getModelMetadata() {
        CorpusMetadataBuilder builder = CorpusMetadataBuilder.newCorpusMetadata(corpus, lang);
        if (legacy) {
            try {
                Set<String> stopWords = StandardPreProcessorIterator.getDefaultStopWordSet(lang);
                builder.replaceNumbers(false).removeAccents(true).applyLowercase(true).applyStemmer(3).
                        maxTokenLength(100).minTokenLength(3).stopWords(stopWords);
            } catch (IndraException e) {
                e.printStackTrace();
            }
        } else {
            builder.replaceNumbers(false).removeAccents(false).applyLowercase(false).applyStemmer(0).
                    maxTokenLength(100).minTokenLength(0);
        }

        return new ModelMetadata(model, sparse, dimensions, vocabSize, windowSize, minWordFreq, builder.build());
    }


    private static RealVector unmarshall(Document doc, int dimensions, boolean legacy, boolean sparse) {
        try {
            final Binary binary = doc.get(VECTOR_FIELD_NAME, Binary.class);
            final byte[] b = binary.getData();

            if (legacy) {
                return BinaryCodecs.legacyUnmarshall(b, dimensions, sparse, dimensions);
            } else {
                return BinaryCodecs.unmarshall(b, sparse, dimensions);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("MongoVectorSpaceExporter v.0.1");
        final String ALPHARD = "mongodb://alphard.fim.uni-passau.de:27017";
        String baseDir = args[0];
        boolean google = args.length > 1 && Boolean.parseBoolean(args[1]);

        if (google) {
            VectorSpaceExporter googleVse = new VectorSpaceExporter(ALPHARD, "en", "w2v", "googlenews300neg");
            ModelMetadata metadata = googleVse.getModelMetadata();
            Iterable<MongoVector> vectors = googleVse.getVectors();
            ModelWriter.save(baseDir, metadata, vectors);
        } else {

            String[] models = {"esa", "w2v", "lsa", "glove"};
            String[] langs = {"en", "pt", "de", "it", "nl", "fr", "es", "ar", "fa", "ru", "zh", "sv"};

            for (String model : models) {
                for (String lang : langs) {
                    System.out.println(String.format("exporting model '%s' and language '%s'", model, lang));
                    VectorSpaceExporter vse = new VectorSpaceExporter(ALPHARD, lang, model, "wiki-2014");
                    ModelMetadata metadata = vse.getModelMetadata();
                    Iterable<MongoVector> vectors = vse.getVectors();
                    ModelWriter.save(baseDir, metadata, vectors);
                    System.out.println("--- done");
                }
            }
        }

        System.out.println("THE END!");
    }
}
