package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;

import java.io.IOException;
import java.util.Properties;

public class LatentSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public LatentSemanticAnalysisBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        super(outDir, dimensions, windowSize, minWordFrequency);
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public String getModelName() {
        return "LSA";
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put(LatentSemanticAnalysis.LSA_DIMENSIONS_PROPERTY, this.dimensions);
        return properties;
    }

    @Override
    public SemanticSpace getSemanticSpace() {
        try {
            return new LatentSemanticAnalysis();
        } catch (IOException e) {
            //TODO trhow exception here
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processPosRunningInformation(SemanticSpace sspace) {
        //nothing to do.
    }
}
