package org.lambda3.indra.loader.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealVector;
import org.lambda3.indra.AnalyzedTerm;
import org.lambda3.indra.composition.SumVectorComposer;
import org.lambda3.indra.core.lucene.LuceneVectorSpace;
import org.lambda3.indra.indexer.builder.ModelBuilderTest;
import org.lambda3.indra.loader.RawSpaceModel;
import org.lambda3.indra.loader.SparseVector;
import org.lambda3.indra.loader.VectorIterator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class LuceneIndraLoaderTest {

    @Test
    public void insertAndReadTest() {
        String baseDir = null;
        try {
            baseDir = Files.createTempDirectory("indra-esa-test").toString();
            ModelBuilderTest modelTest = new ModelBuilderTest();
            RawSpaceModel<SparseVector> esa = modelTest.createExplicitSemanticAnalysisBuilder();

            LuceneIndraLoader loader = new LuceneIndraLoader(baseDir, esa.modelMetadata);
            loader.load(esa);
            loader.close();

            String modelDir = Paths.get(baseDir, esa.modelMetadata.getConciseName()).toString();
            LuceneVectorSpace vs = new LuceneVectorSpace(modelDir);
            Assert.assertEquals(esa.modelMetadata, vs.getMetadata());

            VectorIterator<SparseVector> iter = esa.getVectorIterator();

            while (iter.hasNext()) {
                SparseVector sv = iter.next();
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
