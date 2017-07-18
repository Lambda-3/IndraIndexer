package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import org.lambda3.indra.indexer.ModelMetadata;

import java.io.IOException;

public class LatentSemanticAnalysisBuilder extends SSpaceModelBuilder {


    public LatentSemanticAnalysisBuilder(ModelMetadata mmdata, String outdir) {
        super(mmdata, outdir);

        properties.put(LatentSemanticAnalysis.LSA_DIMENSIONS_PROPERTY, mmdata.numOfDimensions);

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
