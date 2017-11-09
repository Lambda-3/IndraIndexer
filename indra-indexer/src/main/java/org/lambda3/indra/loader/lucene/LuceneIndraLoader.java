package org.lambda3.indra.loader.lucene;

import org.apache.commons.math3.linear.RealVectorUtil;
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
import org.lambda3.indra.core.codecs.BinaryCodecs;
import org.lambda3.indra.core.lucene.LuceneVectorSpace;
import org.lambda3.indra.loader.LocalStoredIndraLoader;
import org.lambda3.indra.loader.SparseVector;
import org.lambda3.indra.loader.VectorIterator;
import org.lambda3.indra.model.ModelMetadata;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class LuceneIndraLoader extends LocalStoredIndraLoader<SparseVector> {
    private IndexWriter writer;

    public LuceneIndraLoader(String baseDir, ModelMetadata metadata) throws IOException {
        super(baseDir, metadata);
        Directory dir = FSDirectory.open(Paths.get(this.modelDir, LuceneVectorSpace.TERMS_INDEX));
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

        Map<Integer, Double> vecMap = RealVectorUtil.vectorToMap(sv.content);

        doc.add(new StoredField(LuceneVectorSpace.VECTOR_FIELD, BinaryCodecs.marshall(vecMap)));
        doc.add(new StringField(LuceneVectorSpace.TERM_FIELD, sv.term, Field.Store.YES));

        for (int i : vecMap.keySet()) {
            doc.add(new StringField(LuceneVectorSpace.INDEXES_FIELD, i + "", Field.Store.YES));
        }

        return doc;
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

}
