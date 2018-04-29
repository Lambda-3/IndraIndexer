package org.lambda3.indra.loader.annoy;

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
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorUtil;
import org.lambda3.indra.AnalyzedTerm;
import org.lambda3.indra.composition.SumVectorComposer;
import org.lambda3.indra.core.annoy.AnnoyVectorSpace;
import org.lambda3.indra.indexer.builder.ModelBuilderTest;
import org.lambda3.indra.util.RawSpaceModel;
import org.lambda3.indra.util.TermVector;
import org.lambda3.indra.util.VectorIterator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class AnnoyIndraLoaderTest {

    private boolean deleteFiles = true;

    public void insertAndReadDenseTest(RawSpaceModel rsm) {
        String baseDir = null;
        try {
            baseDir = Files.createTempDirectory("indra-" + rsm.modelMetadata.modelName + "-test").toString();
            AnnoyIndraLoader loader = new AnnoyIndraLoader(baseDir, rsm.modelMetadata);
            loader.load(rsm);
            loader.close();

            String modelDir = Paths.get(baseDir, rsm.modelMetadata.modelName.toLowerCase(),
                    rsm.modelMetadata.corpusMetadata.language, rsm.modelMetadata.corpusMetadata.corpusName).toString();
            AnnoyVectorSpace vs = new AnnoyVectorSpace(modelDir);
            Assert.assertEquals(rsm.modelMetadata, vs.getMetadata());

            VectorIterator iter = rsm.getVectorIterator();

            while (iter.hasNext()) {
                TermVector sv = iter.next();
                RealVector approx = RealVectorUtil.loosePrecision(sv.content);

                AnalyzedTerm at = new AnalyzedTerm(sv.term, Collections.singletonList(sv.term));
                Map<String, RealVector> vectors = vs.getVectors(Collections.singletonList(at), new SumVectorComposer());

                RealVector vector = vectors.get(at.getTerm());
                Assert.assertEquals(approx, vector);
            }

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } finally {
            try {
                if (deleteFiles) {
                    FileUtils.deleteDirectory(new File(baseDir));
                }
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void insertAndReadPortugueseW2VTest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel w2v = modelTest.createWord2VecModelBuilder("frei");
        insertAndReadDenseTest(w2v);
        try {
            if (deleteFiles) {
                modelTest.deleteTmpFiles();
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void insertAndReadPortugueseLSATest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel lsa = modelTest.createLatentSemanticAnalysisBuilder("frei");
        insertAndReadDenseTest(lsa);
        try {
            if (deleteFiles) {
                modelTest.deleteTmpFiles();
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void insertAndReadPortugueseGloveTest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel glove = modelTest.createGloveModelBuilder("frei");
        insertAndReadDenseTest(glove);
        try {
            if (deleteFiles) {
                modelTest.deleteTmpFiles();
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void insertAndReadChineseW2VTest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel w2v = modelTest.createWord2VecModelBuilder("chinese");
        insertAndReadDenseTest(w2v);
        try {
            if (deleteFiles) {
                modelTest.deleteTmpFiles();
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void insertAndReadChineseLSATest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel lsa = modelTest.createLatentSemanticAnalysisBuilder("chinese");
        insertAndReadDenseTest(lsa);
        try {
            if (deleteFiles) {
                modelTest.deleteTmpFiles();
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void insertAndReadChineseGloveTest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel glove = modelTest.createGloveModelBuilder("chinese");
        insertAndReadDenseTest(glove);
        try {
            if (deleteFiles) {
                modelTest.deleteTmpFiles();
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
