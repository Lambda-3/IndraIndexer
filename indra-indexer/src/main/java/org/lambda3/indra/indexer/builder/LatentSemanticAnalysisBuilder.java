package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.lsa.LatentSemanticAnalysis;

import java.io.IOException;

public class LatentSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public static final Integer DEFAULT_DIM = null;
    private Integer dimensions;

    public LatentSemanticAnalysisBuilder() {
        //TODO review all.
        if (dimensions != null) {
            this.dimensions = dimensions;
            properties.put(LatentSemanticAnalysis.LSA_DIMENSIONS_PROPERTY, dimensions);
        }

        try {
            sspace = new LatentSemanticAnalysis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "LatentSemanticAnalysisBuilder - dimensions: " + (dimensions != null ? dimensions : "default");
    }
}
