package org.lambda3.indra.loader;

public class DenseVector extends Vector<double[]> {

    public DenseVector(String content) {
        super(content.split("\t"));
    }

    @Override
    public double[] digestContent(String content) {
        String[] parts = content.split(" ");
        double[] vector = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Double.parseDouble(parts[i]);
        }

        return vector;
    }
}
