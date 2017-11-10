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
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.core.lucene.LuceneTranslator;
import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.loader.lucene.LuceneTranslatorLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class TranslationExporter {

    private static final String NULL_VALUE = "NULL";

    private static String doctToString(Document document) {
        String term = (String) document.get(LuceneTranslator.TERM_FIELD);
        StringBuilder sb = new StringBuilder(term);
        sb.append("\t");

        Document tf = (Document) document.get(LuceneTranslator.TRANSLATION_FIELD);
        if (tf != null) {
            tf.remove(NULL_VALUE);
            Map<String, Double> translations = (Map) tf;
            for (String t : translations.keySet()) {
                sb.append(t);
                sb.append(":");
                sb.append(translations.get(t));
                sb.append(" ");
            }

            sb.delete(sb.length() - 1, sb.length());
            sb.append("\n");
        }

        return sb.toString();
    }

    private static void export(String baseOutputDir, String mongoURI, String dbName, String lang) {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        MongoDatabase db = mongoClient.getDatabase(dbName);
        try {
            File baseDir = new File(baseOutputDir);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            CorpusMetadata cm = CorpusMetadataBuilder.newCorpusMetadata("en-Europarl_DGT_OpenSubtitile", lang).
                    applyLowercase(true).removeAccents(false).replaceNumbers(false).build();
            MetadataIO.write(baseOutputDir, cm);

            File outputFile = Paths.get(baseOutputDir, LuceneTranslatorLoader.TRANSLATION_FILE_NAME).toFile();
            FileWriter writer = new FileWriter(outputFile);

            MongoCollection<Document> lexs = db.getCollection(LuceneTranslator.LEX_INDEX);
            System.out.println(String.format("--- this database has %d terms ---", lexs.count()));

            FindIterable<Document> docs = lexs.find();
            int counter = 0;
            for (Document doc : docs) {
                writer.write(doctToString(doc));

                if (++counter % 100_000 == 0) {
                    System.out.println(String.format("     - %d terms exported", counter));
                }
            }

            writer.close();
            mongoClient.close();
            System.out.println("Finished!\n\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Translator Exporter v.0.1");
        String baseDir = args[0];
        String baseDbName = "en_%s-Europarl_DGT_OpenSubtitile";
        String[] langs = {"de", "es", "fr", "it", "nl", "pt", "sv"};

        for (String lang : langs) {
            System.out.println("--- exporting data for lang " + lang);
            String baseOutputDir = Paths.get(baseDir, lang).toString();
            String dbName = String.format(baseDbName, lang);

            TranslationExporter.export(baseOutputDir, "mongodb://alphard.fim.uni-passau.de:27017", dbName, lang);
        }
    }
}
