package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;
import org.lambda3.indra.ModelMetadata;

import java.io.IOException;

public class ExplicitSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public ExplicitSemanticAnalysisBuilder(ModelMetadata metadata, String outdir) throws IOException {
        super(metadata, outdir);
        this.sspace = new ExplicitSemanticAnalysis();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
