package org.lambda3.indra.indexer;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.vector.Vector;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.lambda3.indra.ModelMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ModelWriter {

    public static final String MODEL_METADATA_FILE_NAME = "model.metadata";
    public static final String MODEL_CONTENT_FILE_NAME = "vectors.txt";

    private static File prepereTargetDirAndSaveMetadata(String outDir, ModelMetadata metadata) {
        File modelDir = Paths.get(outDir, metadata.getConciseName()).toFile();
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        MetadataWriter.write(modelDir.getAbsolutePath(), metadata);

        return Paths.get(modelDir.getAbsolutePath(), MODEL_CONTENT_FILE_NAME).toFile();
    }

    public static void save(String outDir, ModelMetadata metadata, SemanticSpace sspace) throws IOException {

        File modelFile = prepereTargetDirAndSaveMetadata(outDir, metadata);

        FileWriter fw;
        try {
            fw = new FileWriter(modelFile);

            try {

                if (metadata.sparse) {
                    for (String word : sspace.getWords()) {
                        Vector<Double> vector = sspace.getVector(word);
                        Map<Integer, Double> newVector = convertToSparseVector(vector);
                        String repr = sparseVectorRepresentation(word, newVector);
                        fw.write(repr);
                        fw.write("\n");
                    }
                } else {

                    for (String word : sspace.getWords()) {
                        Vector<Double> vector = sspace.getVector(word);
                        double[] newVector = convertToDenseVector(vector);
                        String repr = denseVectorRepresentation(word, newVector);
                        fw.write(repr);
                        fw.write("\n");
                    }
                }

                fw.flush();
                fw.close();
            } finally {
                fw.close();
            }

        } catch (IOException e) {
            //TODO review here - log
            throw new RuntimeException(e);
        }
    }

    public static void save(String outDir, ModelMetadata metadata, VocabCache<VocabWord> cache, SequenceVectors<VocabWord> vectors) {
        File modelFile = prepereTargetDirAndSaveMetadata(outDir, metadata);

        FileWriter fw;
        try {
            fw = new FileWriter(modelFile);

            try {
                for (String word : cache.words()) {
                    double[] vector = vectors.getWordVector(word);
                    String repr = denseVectorRepresentation(word, vector);
                    fw.write(repr);
                    fw.write("\n");
                }

                fw.flush();
                fw.close();
            } finally {
                fw.close();
            }

        } catch (IOException e) {
            //TODO review here - log
            throw new RuntimeException(e);
        }
    }

    private static double[] convertToDenseVector(Vector<Double> vector) {

        double[] newVector = new double[vector.length()];
        for (int i = 0; i < vector.length(); i++) {
            newVector[i] = vector.getValue(i).doubleValue();
        }

        return newVector;
    }

    private static Map<Integer, Double> convertToSparseVector(Vector<Double> vector) {
        Map<Integer, Double> newVector = new HashMap<>();
        for (int i = 0; i < vector.length(); i++) {
            double value = vector.getValue(i).doubleValue();
            if (value != 0) {
                newVector.put(i, value);
            }
        }

        return newVector;
    }

    private static String sparseVectorRepresentation(String term, Map<Integer, Double> vector) {
        StringBuilder sb = new StringBuilder(term);
        sb.append("\t");

        for (int key : vector.keySet()) {
            sb.append(key);
            sb.append(":");
            sb.append(vector.get(key));
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    private static String denseVectorRepresentation(String term, double[] vector) {
        StringBuilder sb = new StringBuilder(term);
        sb.append("\t");

        for (double d : vector) {
            sb.append(d);
            sb.append(" ");
        }

        return sb.toString().trim();
    }
}
