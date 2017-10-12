package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;
import org.lambda3.indra.ModelMetadata;

import java.io.IOException;
import java.util.Properties;

public class ExplicitSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public ExplicitSemanticAnalysisBuilder(ModelMetadata metadata, String outDir) throws IOException {
        super(metadata, outDir);
        this.sspace = new ExplicitSemanticAnalysis();
    }

    @Override
    public Properties getProperties() {
        //TODO add properties non default properties here.
        return new Properties();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
