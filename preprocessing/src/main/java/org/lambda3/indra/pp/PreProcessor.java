package org.lambda3.indra.pp;

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.pp.transform.Transformer;

import java.util.LinkedList;
import java.util.List;

public abstract class PreProcessor {

    public final CorpusMetadata metadata;
    protected List<Transformer> transformers = new LinkedList<>();

    public PreProcessor(CorpusMetadata metadata) {
        this.metadata = metadata;
    }

    public abstract Document process(Document document);

    public abstract List<String> process(String text);

    public void addTransformer(Transformer transformer) {
        this.transformers.add(transformer);
    }
}
