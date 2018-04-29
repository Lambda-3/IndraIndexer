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

import edu.ucla.sspace.vector.SparseVector;
import edu.ucla.sspace.vector.Vector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealVectorUtil;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.CorpusLoader;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.RawSpaceModel;
import org.lambda3.indra.util.TermVector;
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

    private static final int DIM = 5;
    private Collection<File> tmpDir = new ConcurrentLinkedQueue<>();

    private String buildModel(ModelBuilder builder, String baseDir, String corpusName) throws IOException {
        String corpusDir = ModelBuilderTest.class.getClassLoader().getResource("corpora/" + corpusName).getPath();
        Corpus corpus = CorpusLoader.load(new File(corpusDir));

        ModelMetadata metadata = builder.build(corpus, true);

        return Paths.get(baseDir, metadata.modelName.toLowerCase(), metadata.corpusMetadata.language,
                metadata.corpusMetadata.corpusName).toString();
    }

    private RawSpaceModel testDenseBuilder(String baseDir, ModelBuilder builder, String corpusName) throws IOException {
        String modelDir = buildModel(builder, baseDir, corpusName);

        ModelMetadata mm = MetadataIO.load(modelDir, ModelMetadata.class);
        RawSpaceModel denseModel = new RawSpaceModel(modelDir);

        Assert.assertFalse(denseModel.isSparse());
        Assert.assertEquals(mm, denseModel.modelMetadata);

        VectorIterator vectors = denseModel.getVectorIterator();

        if (!vectors.hasNext()) {
            Assert.fail("empty vector space.");
        }

        while (vectors.hasNext()) {
            TermVector vector = vectors.next();
            Assert.assertEquals(DIM, vector.content.getDimension());
            Assert.assertTrue(DoubleStream.of(vector.content.toArray()).sum() != 0);

            if (builder instanceof LatentSemanticAnalysisBuilder) {
                Vector sourceVector = ((LatentSemanticAnalysisBuilder) builder).sspace.getVector(vector.term);

                if (sourceVector == null) {
                    Assert.fail("term does not exist in the original model.");
                }

                Assert.assertEquals(vector.content.toArray().length, sourceVector.length());

                for (int i = 0; i < sourceVector.length(); i++) {
                    Assert.assertEquals((float) vector.content.toArray()[i], sourceVector.getValue(i).floatValue());
                }
            } else if (builder instanceof PredictiveModelBuilder) {
                double[] sourceVector = ((PredictiveModelBuilder) builder).vectors.getWordVector(vector.term);

                if (sourceVector == null) {
                    Assert.fail("term does not exist in the original model.");
                }

                Assert.assertEquals(vector.content.toArray().length, sourceVector.length);

                for (int i = 0; i < sourceVector.length; i++) {
                    Assert.assertEquals((float) vector.content.toArray()[i], (float) sourceVector[i]);
                }
            } else {
                Assert.fail("Builder not supported for this test.");
            }
        }

        return denseModel;
    }

    public RawSpaceModel createLatentSemanticAnalysisBuilder(String corpusName) {
        try {
            String baseDir = Files.createTempDirectory("indra-lsa-test").toString();
            tmpDir.add(new File(baseDir));
            LatentSemanticAnalysisBuilder builder = new LatentSemanticAnalysisBuilder(baseDir, DIM);
            return testDenseBuilder(baseDir, builder, corpusName);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        Assert.fail("model is null");
        return null;
    }

    public RawSpaceModel createExplicitSemanticAnalysisBuilder(String corpusName) {
        try {
            String baseDir = Files.createTempDirectory("indra-esa-test").toString();
            tmpDir.add(new File(baseDir));

            ExplicitSemanticAnalysisBuilder builder = new ExplicitSemanticAnalysisBuilder(baseDir);
            String modelDir = buildModel(builder, baseDir, corpusName);

            ModelMetadata mm = MetadataIO.load(modelDir, ModelMetadata.class);
            RawSpaceModel esa = new RawSpaceModel(modelDir);

            Assert.assertTrue(esa.isSparse());
            Assert.assertEquals(mm, esa.modelMetadata);

            VectorIterator vectors = esa.getVectorIterator();

            if (!vectors.hasNext()) {
                Assert.fail("empty vector space.");
            }

            while (vectors.hasNext()) {
                TermVector vector = vectors.next();
                Assert.assertTrue(vector.content.getDimension() > 0);
                Assert.assertEquals(vector.content.getDimension(), mm.dimensions);
                Map<Integer, Double> vecMap = RealVectorUtil.vectorToMap(vector.content);
                Assert.assertTrue(vecMap.values().stream().mapToDouble(a -> a).sum() != 0);

                SparseVector sourceVector = (SparseVector) builder.sspace.getVector(vector.term);
                if (sourceVector == null) {
                    Assert.fail("term does not exist in the original model.");
                }

                Assert.assertEquals(vecMap.size(), sourceVector.getNonZeroIndices().length);

                for (int index : sourceVector.getNonZeroIndices()) {
                    Assert.assertEquals(vecMap.get(index).floatValue(), sourceVector.getValue(index).floatValue());
                }
            }

            return esa;

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        Assert.fail("model is null");
        return null;
    }

    public RawSpaceModel createWord2VecModelBuilder(String corpusName) {
        try {
            String baseDir = Files.createTempDirectory("indra-w2v-test").toString();
            tmpDir.add(new File(baseDir));
            Word2VecModelBuilder builder = new Word2VecModelBuilder(baseDir, DIM, 5, 5);
            return testDenseBuilder(baseDir, builder, corpusName);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        Assert.fail("model is null");
        return null;
    }

    public RawSpaceModel createGloveModelBuilder(String corpusName) {
        try {
            String baseDir = Files.createTempDirectory("indra-glove-test").toString();
            tmpDir.add(new File(baseDir));
            GloveModelBuilder builder = new GloveModelBuilder(baseDir, DIM, 5, 5);
            return testDenseBuilder(baseDir, builder, corpusName);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        Assert.fail("model is null");
        return null;
    }

    @Test
    public void testPortugueseExplicitSemanticAnalysisBuilder() {
        createExplicitSemanticAnalysisBuilder("frei");
    }

    @Test
    public void testPortugueseGloveModelBuilder() {
        createGloveModelBuilder("frei");
    }

    @Test
    public void testPortugueseWord2VecModelBuilder() {
        createWord2VecModelBuilder("frei");
    }

    @Test
    public void testPortugueseLatentSemanticAnalysisBuilder() {
        createLatentSemanticAnalysisBuilder("frei");
    }

    @Test
    public void testChineseExplicitSemanticAnalysisBuilder() {
        createExplicitSemanticAnalysisBuilder("chinese");
    }

    @Test
    public void testChineseGloveModelBuilder() {
        createGloveModelBuilder("chinese");
    }

    @Test
    public void testChineseWord2VecModelBuilder() {
        createWord2VecModelBuilder("chinese");
    }

    @Test
    public void testChineseLatentSemanticAnalysisBuilder() {
        createLatentSemanticAnalysisBuilder("chinese");
    }


    @AfterTest
    public void deleteTmpFiles() throws IOException {
        for (File f : tmpDir) {
            FileUtils.deleteDirectory(f);
        }
    }
}
