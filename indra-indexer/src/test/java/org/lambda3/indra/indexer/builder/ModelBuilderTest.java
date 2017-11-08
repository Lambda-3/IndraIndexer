package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.CorpusLoader;
import org.lambda3.indra.indexer.MetadataWriter;
import org.lambda3.indra.loader.DenseVector;
import org.lambda3.indra.loader.RawSpaceModel;
import org.lambda3.indra.loader.SparseVector;
import org.lambda3.indra.loader.VectorIterator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.DoubleStream;

public class ModelBuilderTest {

    private static final int DIM = 25;

    private String buildModel(ModelBuilder builder, String baseDir) throws IOException {
        String corpusDir = getClass().getClassLoader().getResource("corpora/frei").getPath();
        Corpus corpus = CorpusLoader.load(new File(corpusDir));

        ModelMetadata metadata = builder.build(corpus);

        return Paths.get(baseDir, metadata.getConciseName()).toString();
    }

    public void testDenseBuilder(String baseDir, ModelBuilder builder) throws IOException {
        String modelDir = buildModel(builder, baseDir);

        ModelMetadata mm = MetadataWriter.load(modelDir, ModelMetadata.class);
        RawSpaceModel<DenseVector> lsa = new RawSpaceModel<>(modelDir, mm, DenseVector.class);

        Assert.assertFalse(lsa.isSparse());
        Assert.assertEquals(mm, lsa.modelMetadata);

        VectorIterator<DenseVector> vectors = lsa.getVectorIterator();
        if (vectors.hasNext()) {
            DenseVector vector = vectors.next();
            Assert.assertEquals(DIM, vector.content.length);
            Assert.assertTrue(DoubleStream.of(vector.content).sum() != 0);
        }

        new File(baseDir).delete();
    }

    @Test
    public void testLatentSemanticAnalysisBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-lsa-test").toString();
            LatentSemanticAnalysisBuilder builder = new LatentSemanticAnalysisBuilder(baseDir, DIM);
            testDenseBuilder(baseDir, builder);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testExplicitSemanticAnalysisBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-esa-test").toString();
            ExplicitSemanticAnalysisBuilder builder = new ExplicitSemanticAnalysisBuilder(baseDir);
            String modelDir = buildModel(builder, baseDir);

            ModelMetadata mm = MetadataWriter.load(modelDir, ModelMetadata.class);
            RawSpaceModel<SparseVector> esa = new RawSpaceModel<>(modelDir, mm, SparseVector.class);

            Assert.assertTrue(esa.isSparse());
            Assert.assertEquals(mm, esa.modelMetadata);

            VectorIterator<SparseVector> vectors = esa.getVectorIterator();
            if (vectors.hasNext()) {
                SparseVector vector = vectors.next();
                Assert.assertTrue(vector.content.size() > 0);
                Assert.assertTrue(vector.content.size() < mm.dimensions);
                Assert.assertTrue(vector.content.values().stream().mapToDouble(a -> a).sum() != 0);
            }

            new File(baseDir).delete();

        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testWord2VecModelBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-lsa-test").toString();
            Word2VecModelBuilder builder = new Word2VecModelBuilder(baseDir, DIM, 5, 5);
            testDenseBuilder(baseDir, builder);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGloveModelBuilder() {
        try {
            String baseDir = Files.createTempDirectory("indra-lsa-test").toString();
            GloveModelBuilder builder = new GloveModelBuilder(baseDir, DIM, 5, 5);
            testDenseBuilder(baseDir, builder);
        } catch (IOException e) {
            Assert.fail();
        }
    }

}
