package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.common.SemanticSpace;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.indexer.ModelWriter;
import org.lambda3.indra.model.ModelMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;

public abstract class SSpaceModelBuilder extends ModelBuilder {

    SSpaceModelBuilder(String outDir, int dimensions, int windowSize) {
        super(outDir, dimensions, windowSize, NOT_APPLIED);
    }

    @Override
    public ModelMetadata build(Corpus corpus) {
        SemanticSpace sspace = getSemanticSpace();
        Iterator<? extends Document> iter = corpus.getDocumentsIterator();

        try {
            while (iter.hasNext()) {
                Document doc = iter.next();

                String content = doc.content;
                if (content != null && !content.isEmpty())
                    sspace.processDocument(new BufferedReader(new StringReader(content)));
            }

            sspace.processSpace(new Properties());
            processPosRunningInformation(sspace);

            this.vocabSize = sspace.getWords().size();
            ModelMetadata metadata = getModelMetadata(corpus);
            ModelWriter.save(outDir, metadata, sspace);

            return metadata;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract SemanticSpace getSemanticSpace();

    public abstract void processPosRunningInformation(SemanticSpace sspace);
}
