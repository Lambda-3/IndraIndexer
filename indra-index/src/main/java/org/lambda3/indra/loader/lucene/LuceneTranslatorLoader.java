package org.lambda3.indra.loader.lucene;

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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.core.lucene.LuceneTranslator;
import org.lambda3.indra.corpus.CorpusMetadata;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LuceneTranslatorLoader {

    public static final String TRANSLATION_FILE_NAME = "translations.txt";

    private static Document createDocument(String content) throws IOException {
        String[] parts = content.split("\t");
        if (parts.length > 1) {
            Map<String, Double> translations = getTranslations(parts[1]);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(translations);

            Document doc = new Document();
            doc.add(new StringField(LuceneTranslator.TERM_FIELD, parts[0], Field.Store.YES));
            doc.add(new StoredField(LuceneTranslator.TRANSLATION_FIELD, bos.toByteArray()));

            return doc;
        }
        return null;
    }

    private static Map<String, Double> getTranslations(String content) {
        Map<String, Double> map = new HashMap<>();

        String[] terms = content.split(" ");
        for (String term : terms) {
            int sep = term.lastIndexOf(":");
            map.put(term.substring(0, sep), Double.parseDouble(term.substring(sep + 1)));
        }

        return map;
    }

    public static void load(String inputDir, String outputDir) {
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }

        CorpusMetadata metadata = MetadataIO.load(inputDir, CorpusMetadata.class);
        MetadataIO.write(outputDir, metadata);

        try {
            Directory dir = FSDirectory.open(Paths.get(outputDir, LuceneTranslator.LEX_INDEX));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            iwc.setRAMBufferSizeMB(256.0);

            IndexWriter writer = new IndexWriter(dir, iwc);

            BufferedReader reader = new BufferedReader(new FileReader(Paths.get(inputDir, TRANSLATION_FILE_NAME).toFile()));
            String line;

            while ((line = reader.readLine()) != null) {
                Document doc = createDocument(line);
                if (doc != null) {
                    writer.addDocument(doc);
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Lucene Translator Loader");
        String input = args[0];
        String output = args[1];

        System.out.println(String.format("Loading from %s to %s", input, output));

        load(input, output);
    }
}
