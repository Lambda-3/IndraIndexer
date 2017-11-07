package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;

import java.io.IOException;
import java.util.Properties;

public class ExplicitSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public ExplicitSemanticAnalysisBuilder(String outDir, int minWordFrequency) throws IOException {
        super(outDir, -1, -1, minWordFrequency);
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
    public Properties getProperties() {
        // no properties for ESA.
        return new Properties();
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
