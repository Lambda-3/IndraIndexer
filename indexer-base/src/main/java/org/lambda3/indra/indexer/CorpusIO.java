package org.lambda3.indra.indexer;

import java.io.File;

public interface CorpusIO {

    public Corpus load(File file);

    public void save(Corpus corpus, File file);
}
