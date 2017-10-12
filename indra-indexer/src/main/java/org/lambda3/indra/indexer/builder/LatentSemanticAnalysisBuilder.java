package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import org.lambda3.indra.ModelMetadata;

import java.io.IOException;
import java.util.Properties;

public class LatentSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public LatentSemanticAnalysisBuilder(ModelMetadata metadata, String outDir) {
        super(metadata, outDir);

        try {
            sspace = new LatentSemanticAnalysis();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put(LatentSemanticAnalysis.LSA_DIMENSIONS_PROPERTY, metadata.numOfDimensions);
        return properties;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
