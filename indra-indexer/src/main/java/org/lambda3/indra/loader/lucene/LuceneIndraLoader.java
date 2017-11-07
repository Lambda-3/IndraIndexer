package org.lambda3.indra.loader.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lambda3.indra.loader.LocalStoredIndraLoader;
import org.lambda3.indra.loader.SparseVector;
import org.lambda3.indra.loader.VectorIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;

public class LuceneIndraLoader extends LocalStoredIndraLoader<SparseVector> {
    //TODO rever to the fields in LuceneVectorSpace
    private static final String TERMS_INDEX = "terms";

    private static final String TERM_FIELD = "term";
    private static final String VECTOR_FIELD = "vector";
    private static final String INDEXES_FIELD = "indexes";

    private IndexWriter writer;
    private String baseDir;

    public LuceneIndraLoader(String baseDir) throws IOException {
        super(baseDir);
        Directory dir = FSDirectory.open(Paths.get(this.modelDir.getAbsolutePath(), TERMS_INDEX));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(256.0);

        this.writer = new IndexWriter(dir, iwc);
    }

    @Override
    protected void doLoad(VectorIterator<SparseVector> iter) {

        while (iter.hasNext()) {
            SparseVector sv = iter.next();
            try {
                Document doc = createSparseDocument(sv);
                writer.addDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Document createSparseDocument(SparseVector sv) throws IOException {
        Document doc = new Document();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(sv.content);

        doc.add(new StoredField(VECTOR_FIELD, bos.toByteArray()));
        doc.add(new StringField(TERM_FIELD, sv.term, Field.Store.YES));

        for (int i : sv.content.keySet()) {
            doc.add(new StringField(INDEXES_FIELD, i + "", Field.Store.YES));
        }

        return doc;
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
