package org.lambda3.indra.loader.tests;

import org.lambda3.indra.loader.codecs.BinaryCodecs;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class BinaryCodecsTest {

    @Test
    public void denseSerializationTest() throws IOException {
        double[] v1 = new double[] {1.52, 1/3, -3.1};
        byte[] b = BinaryCodecs.marshall(v1);
        double[] v2 = BinaryCodecs.unmarshall(b);
        Assert.assertEquals(v1, v2);
    }

}
