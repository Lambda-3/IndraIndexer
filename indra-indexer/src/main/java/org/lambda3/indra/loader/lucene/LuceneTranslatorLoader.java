package org.lambda3.indra.loader.lucene;

import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.corpus.CorpusMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LuceneTranslatorLoader {

    private static final String TRANSLATION_FILE_NAME = "translations.txt";

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
        CorpusMetadata metadata = MetadataIO.load(inputDir, CorpusMetadata.class);
        MetadataIO.write(outputDir, metadata);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Paths.get(inputDir, TRANSLATION_FILE_NAME).toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
