package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;
import org.lambda3.indra.preprocessing.transform.Transformer;

import java.util.List;

public abstract class Preprocessor {

    public final CorpusMetadata metadata;
    protected List<Transformer> transformers;

    public Preprocessor(CorpusMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Document process(Document document);

    public void addTransformer(Transformer transformer) {
        this.transformers.add(transformer);
    }
}
