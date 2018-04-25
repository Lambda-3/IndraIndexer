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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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

        DataOutputStream out;
        try {
            out = new DataOutputStream(new FileOutputStream(modelFile));

            try {

                if (metadata.sparse) {
                    for (String word : sspace.getWords()) {
                        Vector<Double> vector = sspace.getVector(word);
                        Map<Integer, Double> newVector = convertToSparseVector(vector);
                        if (!newVector.isEmpty()) {
                            byte[] repr = sparseVectorRepresentation(word, newVector);
                            out.write(repr);
                        }
                    }
                } else {

                    for (String word : sspace.getWords()) {
                        Vector<Double> vector = sspace.getVector(word);
                        double[] newVector = convertToDenseVector(vector);
                        byte[] repr = denseVectorRepresentation(word, newVector);
                        out.write(repr);
                    }
                }

                out.flush();
                out.close();
            } finally {
                out.close();
            }

        } catch (IOException e) {
            //TODO review here - log
            throw new RuntimeException(e);
        }
    }

    public static void save(String outDir, ModelMetadata metadata, Iterable<MongoVector> vectors) throws IOException {

        File modelFile = prepereTargetDirAndSaveMetadata(outDir, metadata);

        DataOutputStream out;
        try {
            out = new DataOutputStream(new FileOutputStream(modelFile));

            try {

                if (metadata.sparse) {
                    out.writeChars(String.format("%d\n", metadata.vocabSize));

                    for (MongoVector vector : vectors) {
                        Map<Integer, Double> newVector = RealVectorUtil.vectorToMap(vector.vector);
                        byte[] repr = sparseVectorRepresentation(vector.term, newVector);
                        out.write(repr);
                    }
                } else {
                    out.writeChars(String.format("%d %d\n", metadata.vocabSize, metadata.dimensions));
                    for (MongoVector vector : vectors) {
                        double[] newVector = vector.vector.toArray();
                        byte[] repr = denseVectorRepresentation(vector.term, newVector);
                        out.write(repr);
                    }
                }

                out.flush();
                out.close();
            } finally {
                out.close();
            }

        } catch (IOException e) {
            //TODO review here - log
            throw new RuntimeException(e);
        }
    }

    public static void save(String outDir, ModelMetadata metadata, VocabCache<VocabWord> cache, SequenceVectors<VocabWord> vectors) {
        File modelFile = prepereTargetDirAndSaveMetadata(outDir, metadata);

        DataOutputStream out;
        try {
            out = new DataOutputStream(new FileOutputStream(modelFile));

            try {
                for (String word : cache.words()) {
                    double[] vector = vectors.getWordVector(word);
                    byte[] repr = denseVectorRepresentation(word, vector);
                    out.write(repr);
                }

                out.flush();
                out.close();
            } finally {
                out.close();
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

    //binary representation
    private static byte[] sparseVectorRepresentation(String term, Map<Integer, Double> vector) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(((term.length() + 2) * Character.BYTES) + Integer.BYTES  + (vector.size() * (Integer.BYTES + Float.BYTES)));

        byte[] termBytes = term.getBytes();
        byteBuffer.put(termBytes, 0, termBytes.length);
        byteBuffer.put((byte) ' ');
        byteBuffer.putInt(vector.size());

        for (Integer i : vector.keySet()) {
            byteBuffer.putInt(i);
            byteBuffer.putFloat(vector.get(i).floatValue());
        }

        byteBuffer.put((byte) '\n');
        return byteBuffer.array();
    }

    //binary representation
    private static byte[] denseVectorRepresentation(String term, double[] vector) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(((term.length() + 2) * Character.BYTES) + (vector.length * Float.BYTES));

        byte[] termBytes = term.getBytes();
        byteBuffer.put(termBytes, 0, termBytes.length);
        byteBuffer.put((byte) ' ');

        for (Double d : vector) {
            byteBuffer.putFloat(d.floatValue());
        }

        byteBuffer.put((byte) '\n');
        return byteBuffer.array();
    }
}
