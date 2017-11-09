package org.lambda3.indra.loader.annoy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealVector;
import org.lambda3.indra.AnalyzedTerm;
import org.lambda3.indra.composition.SumVectorComposer;
import org.lambda3.indra.core.annoy.AnnoyVectorSpace;
import org.lambda3.indra.core.lucene.LuceneVectorSpace;
import org.lambda3.indra.indexer.builder.ModelBuilderTest;
import org.lambda3.indra.loader.DenseVector;
import org.lambda3.indra.loader.RawSpaceModel;
import org.lambda3.indra.loader.SparseVector;
import org.lambda3.indra.loader.VectorIterator;
import org.lambda3.indra.loader.annoy.AnnoyIndraLoader;
import org.lambda3.indra.loader.lucene.LuceneIndraLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class AnnoyIndraLoaderTest {


    @Test
    public void insertAndRead() {
        String baseDir = null;
        try {
            baseDir = Files.createTempDirectory("test").toString();
            ModelBuilderTest modelTest = new ModelBuilderTest();
            RawSpaceModel<DenseVector>  w2v= modelTest.testWord2VecModelBuilder(baseDir);


            AnnoyIndraLoader loader = new AnnoyIndraLoader(baseDir);
            loader.load(w2v);
            loader.close();

            String modelDir = Paths.get(baseDir, w2v.modelMetadata.getConciseName()).toString();

            AnnoyVectorSpace vs = new AnnoyVectorSpace(modelDir);
            Assert.assertEquals(w2v.modelMetadata, vs.getMetadata());

            VectorIterator<DenseVector> iter = w2v.getVectorIterator();

            while (iter.hasNext()) {
                DenseVector sv = iter.next();
                AnalyzedTerm at = new AnalyzedTerm(sv.term, Collections.singletonList(sv.term));
                Map<String, RealVector> vectors = vs.getVectors(Collections.singletonList(at), new SumVectorComposer());

                RealVector vector = vectors.get(at.getTerm());
                Assert.assertEquals(sv.content, vector);
            }

            modelTest.deleteTmpFiles();
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
}
