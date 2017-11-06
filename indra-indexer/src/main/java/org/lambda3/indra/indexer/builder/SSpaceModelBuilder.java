package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.common.SemanticSpace;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.indexer.ModelWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;

public abstract class SSpaceModelBuilder extends ModelBuilder {

    SemanticSpace sspace;

    public SSpaceModelBuilder(ModelMetadata metadata, String outdir) {
        super(metadata, outdir);
    }

    @Override
    public void build(Corpus corpus) {

        Iterator<? extends Document> iter = corpus.getDocumentsIterator();

        try {
            while (iter.hasNext()) {
                Document doc = iter.next();

                String content = doc.content;
                if (content != null && !content.isEmpty())
                    sspace.processDocument(new BufferedReader(new StringReader(content)));
            }

            sspace.processSpace(getProperties());
            metadata.params.put(PredictiveModelBuilder.VECTOR_SIZE,sspace.getVectorLength());
            ModelWriter.save(outDir, metadata, sspace);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract Properties getProperties();
}
