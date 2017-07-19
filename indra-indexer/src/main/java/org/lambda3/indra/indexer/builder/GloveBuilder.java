package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.ModelMetadata;

public class GloveBuilder extends PredictiveModelBuilder {

    public GloveBuilder(ModelMetadata metadata, String outdir) {
        super(metadata, outdir, null);
    }
}
