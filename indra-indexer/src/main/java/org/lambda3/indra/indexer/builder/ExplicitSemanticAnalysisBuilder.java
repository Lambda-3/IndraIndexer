package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;
import org.lambda3.indra.indexer.Corpus;
import org.lambda3.indra.indexer.ModelMetadata;

import java.io.IOException;

public class ExplicitSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public ExplicitSemanticAnalysisBuilder(ModelMetadata mmdata, String outdir) throws IOException {
        super(mmdata, outdir);
        this.sspace = new ExplicitSemanticAnalysis();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
