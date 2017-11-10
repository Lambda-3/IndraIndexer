package org.lambda3.indra.loader.mongo;

import org.apache.commons.math3.linear.RealVector;

public class MongoVector {

    public final String term;
    public final RealVector vector;

    public MongoVector(String term, RealVector vector) {
        this.term = term;
        this.vector = vector;
    }
}
