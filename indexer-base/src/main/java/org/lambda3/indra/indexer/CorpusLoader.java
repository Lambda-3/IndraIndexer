package org.lambda3.indra.indexer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class CorpusLoader {

    private static final String METADATA_FILE_NAME = "corpus.metadata";
    private static final String CONTENT_FILE_NAME = "corpus.txt";

    private File baseDir;

    public CorpusLoader(File baseDir) {
        this.baseDir = baseDir;
    }

    public Corpus load(String corpusName) throws IOException {
        File metadataFile = Paths.get(baseDir.getAbsolutePath(), corpusName, METADATA_FILE_NAME).toFile();
        File contentFile = Paths.get(baseDir.getAbsolutePath(), corpusName, CONTENT_FILE_NAME).toFile();

        CorpusMetadata metadata = loadMetadata(metadataFile);
        return new StreamCorpus(metadata, new DocumentIterator(new BufferedReader(new FileReader(contentFile))), null);
    }

    public Corpus getWriterCorpus(CorpusMetadata metadata) throws IOException {
        File corpusDir = Paths.get(baseDir.getAbsolutePath(), metadata.corpusName).toFile();
        if (!corpusDir.exists()) {
            corpusDir.mkdirs();
        }

        File metadataFile = Paths.get(corpusDir.getAbsolutePath(), METADATA_FILE_NAME).toFile();
        File contentFile = Paths.get(corpusDir.getAbsolutePath(), CONTENT_FILE_NAME).toFile();

        writeMetadata(metadata, metadataFile);
        return new StreamCorpus(metadata, null, new FileWriter(contentFile));
    }

    private static CorpusMetadata loadMetadata(File file) {
        if (file.exists()) {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));
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

    private static void writeMetadata(CorpusMetadata metadata, File metadataFile) throws IOException {
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

        Writer writer = new FileWriter(metadataFile);
        metadataJson.writeJSONString(writer);
        writer.close();
    }
}

class DocumentIterator implements Iterator<Document> {

    private BufferedReader reader;
    private String line;

    DocumentIterator(BufferedReader reader) throws IOException {
        this.reader = reader;
        nextNonEmptyLine();
    }

    public void nextNonEmptyLine() throws IOException {
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = (line != null);
        if (!hasNext && reader != null) {
            try {
                this.reader.close();
                this.reader = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return hasNext;
    }

    @Override
    public Document next() {
        if (!hasNext()) {
            return null;
        }

        Document doc = Document.simpleDocument(line);
        try {
            nextNonEmptyLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }
}
