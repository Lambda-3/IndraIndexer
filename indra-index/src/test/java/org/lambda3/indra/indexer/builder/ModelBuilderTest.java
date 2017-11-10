package org.lambda3.indra.indexer.builder;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealVectorUtil;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.CorpusLoader;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.DenseVector;
import org.lambda3.indra.util.RawSpaceModel;
import org.lambda3.indra.util.SparseVector;
import org.lambda3.indra.util.VectorIterator;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.DoubleStream;

public class ModelBuilderTest {

    private static final int DIM = 25;
    private Collection<File> tmpDir = new ConcurrentLinkedQueue<>();

    private String buildModel(ModelBuilder builder, String baseDir) throws IOException {
        String corpusDir = getClass().getClassLoader().getResource("corpora/frei").getPath();
        Corpus corpus = CorpusLoader.load(new File(corpusDir));

        ModelMetadata metadata = builder.build(corpus);

        return Paths.get(baseDir, metadata.modelName, metadata.corpusMetadata.language,
                metadata.corpusMetadata.corpusName).toString();
    }

    public RawSpaceModel<DenseVector> testDenseBuilder(String baseDir, ModelBuilder builder) throws IOException {
        String modelDir = buildModel(builder, baseDir);

        ModelMetadata mm = MetadataIO.load(modelDir, ModelMetadata.class);
        RawSpaceModel<DenseVector> denseModel = new RawSpaceModel<>(modelDir, mm, DenseVector.class);

        Assert.assertFalse(denseModel.isSparse());
        Assert.assertEquals(mm, denseModel.modelMetadata);

        VectorIterator<DenseVector> vectors = denseModel.getVectorIterator();
        if (vectors.hasNext()) {
            DenseVector vector = vectors.next();
            Assert.assertEquals(DIM, vector.content.getDimension());
            Assert.assertTrue(DoubleStream.of(vector.content.toArray()).sum() != 0);
        }

        return denseModel;
    }

    public RawSpaceModel<DenseVector> createLatentSemanticAnalysisBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-lsa-test").toString();
            tmpDir.add(new File(baseDir));
            LatentSemanticAnalysisBuilder builder = new LatentSemanticAnalysisBuilder(baseDir, DIM);
            return testDenseBuilder(baseDir, builder);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }

    public RawSpaceModel<SparseVector> createExplicitSemanticAnalysisBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-esa-test").toString();
            tmpDir.add(new File(baseDir));

            ExplicitSemanticAnalysisBuilder builder = new ExplicitSemanticAnalysisBuilder(baseDir);
            String modelDir = buildModel(builder, baseDir);

            ModelMetadata mm = MetadataIO.load(modelDir, ModelMetadata.class);
            RawSpaceModel<SparseVector> esa = new RawSpaceModel<>(modelDir, mm, SparseVector.class);

            Assert.assertTrue(esa.isSparse());
            Assert.assertEquals(mm, esa.modelMetadata);

            VectorIterator<SparseVector> vectors = esa.getVectorIterator();
            if (vectors.hasNext()) {
                SparseVector vector = vectors.next();
                Assert.assertTrue(vector.content.getDimension() > 0);
                Assert.assertEquals(vector.content.getDimension(), mm.dimensions);
                Map<Integer, Double> vecMap = RealVectorUtil.vectorToMap(vector.content);
                Assert.assertTrue(vecMap.values().stream().mapToDouble(a -> a).sum() != 0);
            }

            return esa;

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        return null;
    }

    public RawSpaceModel<DenseVector> createWord2VecModelBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-w2v-test").toString();
            tmpDir.add(new File(baseDir));
            Word2VecModelBuilder builder = new Word2VecModelBuilder(baseDir, DIM, 5, 5);
            return testDenseBuilder(baseDir, builder);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }

    public RawSpaceModel<DenseVector> createGloveModelBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-glove-test").toString();
            tmpDir.add(new File(baseDir));
            GloveModelBuilder builder = new GloveModelBuilder(baseDir, DIM, 5, 5);
            return testDenseBuilder(baseDir, builder);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }

    @Test
    public void testExplicitSemanticAnalysisBuilder() {
        createExplicitSemanticAnalysisBuilder();
    }

    @Test
    public void testGloveModelBuilder() {
        createGloveModelBuilder();
    }

    @Test
    public void testWord2VecModelBuilder() {
        createWord2VecModelBuilder();
    }

    @Test
    public void testLatentSemanticAnalysisBuilder() {
        createLatentSemanticAnalysisBuilder();
    }

    @AfterTest
    public void deleteTmpFiles() throws IOException {
        for (File f : tmpDir) {
            FileUtils.deleteDirectory(f);
        }
    }

}
