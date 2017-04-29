package org.lambda3.indra.loader.tests;

import org.datavec.api.util.ClassPathResource;
import org.lambda3.indra.loader.DenseVectorGenerator;
import org.lambda3.indra.loader.w2v.Word2VecGenerator;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;

//TODO: Expand test coverage
public final class W2VTest {

    @Test(enabled = false)
    public void generateTest() throws IOException {
        File inputFile = new ClassPathResource("wikipedia/data.txt").getFile();
        File outputFile = Files.createTempFile("w2v-model", ".bin").toFile();
        Word2VecGenerator.buildFromFile(inputFile, outputFile);
        DenseVectorGenerator vectorGenerator = Word2VecGenerator.readFromFile(outputFile);
        Iterator<Map.Entry<String, double[]>> iterator = vectorGenerator.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, double[]> entry = iterator.next();
            //System.out.println(entry.getKey() + " " + Arrays.toString(entry.getValue()));
        }
    }

}
