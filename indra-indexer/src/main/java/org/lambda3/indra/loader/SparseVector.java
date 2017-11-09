package org.lambda3.indra.loader;

import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVector;

public class SparseVector extends Vector {

    public SparseVector(int dimensions, String content) {
        super(dimensions, content.split("\t"));
    }

    @Override
    public RealVector digestContent(String content) {
        RealVector vector = new OpenMapRealVector(dimensions);

        String[] parts = content.split(" ");
        for (String part : parts) {
            String[] np = part.split(":");
            vector.addToEntry(Integer.parseInt(np[0]), Double.parseDouble(np[1]));
        }

        return vector;
    }
}
