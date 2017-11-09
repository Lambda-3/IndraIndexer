package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;

import java.io.IOException;
import java.util.Properties;

public class ExplicitSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public ExplicitSemanticAnalysisBuilder(String outDir) throws IOException {
        super(outDir, NOT_APPLIED, NOT_APPLIED);
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public String getModelName() {
        return "ESA";
    }

    @Override
    public SemanticSpace getSemanticSpace() {
        try {
            return new ExplicitSemanticAnalysis();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void processPosRunningInformation(SemanticSpace sspace) {
        this.dimensions = sspace.getVectorLength();
    }
}
