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

    SSpaceModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        super(outDir, dimensions, windowSize, minWordFrequency);
    }

    @Override
    public void build(Corpus corpus) {
        SemanticSpace sspace = getSemanticSpace();
        Iterator<? extends Document> iter = corpus.getDocumentsIterator();

        try {
            while (iter.hasNext()) {
                Document doc = iter.next();

                String content = doc.content;
                if (content != null && !content.isEmpty())
                    sspace.processDocument(new BufferedReader(new StringReader(content)));
            }

            sspace.processSpace(getProperties());
            processPosRunningInformation(sspace);

            ModelMetadata metadata = getModelMetadata(corpus);
            ModelWriter.save(outDir, metadata, sspace);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract Properties getProperties();

    public abstract SemanticSpace getSemanticSpace();

    public abstract void processPosRunningInformation(SemanticSpace sspace);
}
