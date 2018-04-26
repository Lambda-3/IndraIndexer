package org.lambda3.indra.loader.lucene;

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
import org.lambda3.indra.AnalyzedTerm;
import org.lambda3.indra.composition.SumVectorComposer;
import org.lambda3.indra.core.lucene.LuceneVectorSpace;
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

public class LuceneIndraLoaderTest {

    private boolean deleteFiles = true;

    @Test
    public void insertAndReadTest() {
        String baseDir = null;
        try {
            baseDir = Files.createTempDirectory("indra-esa-test").toString();
            ModelBuilderTest modelTest = new ModelBuilderTest();
            RawSpaceModel esa = modelTest.createExplicitSemanticAnalysisBuilder();

            LuceneIndraLoader loader = new LuceneIndraLoader(baseDir, esa.modelMetadata);
            loader.load(esa);
            loader.close();

            String modelDir = Paths.get(baseDir, esa.modelMetadata.modelName,
                    esa.modelMetadata.corpusMetadata.language, esa.modelMetadata.corpusMetadata.corpusName).toString();
            LuceneVectorSpace vs = new LuceneVectorSpace(modelDir);
            Assert.assertEquals(esa.modelMetadata, vs.getMetadata());

            VectorIterator iter = esa.getVectorIterator();

            while (iter.hasNext()) {
                TermVector sv = iter.next();
                AnalyzedTerm at = new AnalyzedTerm(sv.term, Collections.singletonList(sv.term));
                Map<String, RealVector> vectors = vs.getVectors(Collections.singletonList(at), new SumVectorComposer());

                RealVector vector = vectors.get(at.getTerm());
                Assert.assertEquals(sv.content, vector);
            }

            if (deleteFiles) {
                modelTest.deleteTmpFiles();
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
}
