package org.lambda3.indra.indexer;

import java.util.Map;
import java.util.Objects;

public final class DenseVectorEntry implements Map.Entry<String, double[]> {
    private String word;
    private double[] vector;

    public DenseVectorEntry(String word, double[] vector) {
        this.word = Objects.requireNonNull(word);
        this.vector = Objects.requireNonNull(vector);
    }

    @Override
    public String getKey() {
        return word;
    }

    @Override
    public double[] getValue() {
        return vector;
    }

    @Override
    public double[] setValue(double[] value) {
        throw new UnsupportedOperationException();
    }
}
