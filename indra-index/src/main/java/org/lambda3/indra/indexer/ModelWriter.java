package org.lambda3.indra.indexer;

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

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.vector.Vector;
import org.apache.commons.math3.linear.RealVectorUtil;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.loader.mongo.MongoVector;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.RawSpaceModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ModelWriter {

    private static File prepereTargetDirAndSaveMetadata(String outDir, ModelMetadata metadata) {
        File modelDir = Paths.get(outDir, metadata.modelName, metadata.corpusMetadata.language,
                metadata.corpusMetadata.corpusName).toFile();
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        MetadataIO.write(modelDir.getAbsolutePath(), metadata);

        return Paths.get(modelDir.getAbsolutePath(), RawSpaceModel.MODEL_CONTENT_FILE_NAME).toFile();
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

    public static void save(String outDir, ModelMetadata metadata, Iterable<MongoVector> vectors) throws IOException {

        File modelFile = prepereTargetDirAndSaveMetadata(outDir, metadata);

        FileWriter fw;
        try {
            fw = new FileWriter(modelFile);

            try {

                if (metadata.sparse) {
                    for (MongoVector vector : vectors) {
                        Map<Integer, Double> newVector = RealVectorUtil.vectorToMap(vector.vector);
                        String repr = sparseVectorRepresentation(vector.term, newVector);
                        fw.write(repr);
                        fw.write("\n");
                    }
                } else {
                    for (MongoVector vector : vectors) {
                        double[] newVector = vector.vector.toArray();
                        String repr = denseVectorRepresentation(vector.term, newVector);
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
