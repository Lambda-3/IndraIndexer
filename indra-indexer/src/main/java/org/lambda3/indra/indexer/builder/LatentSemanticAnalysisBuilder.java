package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import org.lambda3.indra.ModelMetadata;

import java.io.IOException;

public class LatentSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public LatentSemanticAnalysisBuilder(ModelMetadata metadata, String outdir) {
        super(metadata, outdir);

        properties.put(LatentSemanticAnalysis.LSA_DIMENSIONS_PROPERTY, metadata.numOfDimensions);

        try {
            sspace = new LatentSemanticAnalysis();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
