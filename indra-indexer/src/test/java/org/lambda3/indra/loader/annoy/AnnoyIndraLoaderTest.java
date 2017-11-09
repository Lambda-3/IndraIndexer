package org.lambda3.indra.loader.annoy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.lambda3.indra.AnalyzedTerm;
import org.lambda3.indra.composition.SumVectorComposer;
import org.lambda3.indra.core.annoy.AnnoyVectorSpace;
import org.lambda3.indra.indexer.builder.ModelBuilderTest;
import org.lambda3.indra.loader.DenseVector;
import org.lambda3.indra.loader.RawSpaceModel;
import org.lambda3.indra.loader.VectorIterator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class AnnoyIndraLoaderTest {


    private RealVector loosePrecision(RealVector rv) {
        double[] arv = rv.toArray();

        double[] nrv = new double[rv.getDimension()];
        for (int i = 0; i < arv.length; i++) {
            nrv[i] = (float) arv[i];
        }

        return new ArrayRealVector(nrv, false);
    }

    public void insertAndReadDenseTest(RawSpaceModel<DenseVector> rsm) {
        String baseDir = null;
        try {
            baseDir = Files.createTempDirectory("indra-" + rsm.modelMetadata.modelName + "-test").toString();
            AnnoyIndraLoader loader = new AnnoyIndraLoader(baseDir, rsm.modelMetadata);
            loader.load(rsm);
            loader.close();

            String modelDir = Paths.get(baseDir, rsm.modelMetadata.getConciseName()).toString();
            AnnoyVectorSpace vs = new AnnoyVectorSpace(modelDir);
            Assert.assertEquals(rsm.modelMetadata, vs.getMetadata());

            VectorIterator<DenseVector> iter = rsm.getVectorIterator();

            while (iter.hasNext()) {
                DenseVector sv = iter.next();
                RealVector approx = loosePrecision(sv.content);

                AnalyzedTerm at = new AnalyzedTerm(sv.term, Collections.singletonList(sv.term));
                Map<String, RealVector> vectors = vs.getVectors(Collections.singletonList(at), new SumVectorComposer());

                RealVector vector = vectors.get(at.getTerm());
                Assert.assertEquals(approx, vector);
            }

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } finally {
            try {
                FileUtils.deleteDirectory(new File(baseDir));
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void insertAndReadW2VTest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel<DenseVector> w2v = modelTest.createWord2VecModelBuilder();
        insertAndReadDenseTest(w2v);
        try {
            modelTest.deleteTmpFiles();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void insertAndReadLSATest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel<DenseVector> lsa = modelTest.createLatentSemanticAnalysisBuilder();
        insertAndReadDenseTest(lsa);
        try {
            modelTest.deleteTmpFiles();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void insertAndReadGloveTest() {
        ModelBuilderTest modelTest = new ModelBuilderTest();
        RawSpaceModel<DenseVector> glove = modelTest.createGloveModelBuilder();
        insertAndReadDenseTest(glove);
        try {
            modelTest.deleteTmpFiles();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
