package org.lambda3.indra.indexer;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.vector.Vector;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.indexer.builder.PredictiveModelBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class ModelWriter {

    public static void save(String outDir, ModelMetadata metadata, SemanticSpace sspace) throws IOException {

        MetadataWriter.write(outDir, metadata);
        File modelFile = Paths.get(outDir, metadata.modelName).toFile();

        FileWriter fw = null;
        try {
            fw = new FileWriter(modelFile);

            try {

                for (String word : sspace.getWords()) {
                    Vector<Double> vector = sspace.getVector(word);
                    String repr = vectorRepresentation(word, convertVector(vector));
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

    public static void save(String outDir, ModelMetadata metadata, VocabCache<VocabWord> cache, SequenceVectors<VocabWord> vectors) {
        MetadataWriter.write(outDir, metadata);
        File modelFile = Paths.get(outDir, metadata.modelName).toFile();

        FileWriter fw = null;
        try {
            fw = new FileWriter(modelFile);

            try {
                for (String word : cache.words()) {
                    double[] vector = vectors.getWordVector(word);
                    String repr = vectorRepresentation(word, vector);
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

    public static double[] convertVector(Vector<Double> vector) {

        double[] newVector = new double[vector.length()];
        for (int i = 0; i < vector.length(); i++) {
            newVector[i] = vector.getValue(i).doubleValue();
        }

        return newVector;
    }

    public static String vectorRepresentation(String term, double[] vector) {
        StringBuilder sb = new StringBuilder(term);
        sb.append("\t");

        for (double d : vector) {
            sb.append(d);
            sb.append(" ");//TODO review the format here
        }

        return sb.toString().trim();
    }
}
