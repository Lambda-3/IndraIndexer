package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;

import java.io.IOException;

public class ExplicitSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public ExplicitSemanticAnalysisBuilder() throws IOException {
        this.sspace = new ExplicitSemanticAnalysis();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
