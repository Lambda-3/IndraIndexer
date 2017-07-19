package org.lambda3.indra.corpus;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetadataIO {

    private static final String CORPUS_METADATA_FILE_NAME = "corpus.metadata";

    static CorpusMetadata loadCorpusMetadata(File corpusBaseDir) {
        File metadataFile = Paths.get(corpusBaseDir.getAbsolutePath(), CORPUS_METADATA_FILE_NAME).toFile();
        if (metadataFile.exists()) {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(metadataFile));
                Map<String, Object> data = CorpusMetadataBuilder.getDefaultData();
                data.putAll(jsonObject);

                Object objStopWords = data.get(CorpusMetadataBuilder.STOP_WORDS);
                if (objStopWords instanceof JSONArray) {
                    JSONArray array = ((JSONArray) objStopWords);
                    Set<String> stopWords = new HashSet<>();

                    for (Object sw : array) {
                        stopWords.add((String) sw);
                    }
                    data.put(CorpusMetadataBuilder.STOP_WORDS, stopWords);
                }

                return new CorpusMetadata(data);

            } catch (ParseException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    static void writeCorpusMetadata(CorpusMetadata metadata, File corpusBaseDir) throws IOException {
        Map<String, Object> map = metadata.asMap();
        Map<String, Collection<String>> trans = (Map<String, Collection<String>>) map.get(CorpusMetadataBuilder.TRANSFORMERS);

        JSONObject transObject = new JSONObject();
        for (String key : trans.keySet()) {
            JSONArray array = new JSONArray();
            trans.get(key).stream().forEach(array::add);
            transObject.put(key, array);
        }

        JSONObject metadataJson = new JSONObject(map);
        metadataJson.put(CorpusMetadataBuilder.TRANSFORMERS, transObject);

        File metadataFile = Paths.get(corpusBaseDir.getAbsolutePath(), CORPUS_METADATA_FILE_NAME).toFile();
        Writer writer = new FileWriter(metadataFile);
        metadataJson.writeJSONString(writer);
        writer.close();
    }
}


