package org.lambda3.indra.loader.lucene;

/*-
 * ==========================License-Start=============================
 * indra-index
 * --------------------------------------------------------------------
 * Copyright (C) 2017 Lambda^3
 * --------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ==========================License-End===============================
 */

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
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.SparseVector;
import org.lambda3.indra.util.VectorIterator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class LuceneIndraLoader extends LocalStoredIndraLoader<SparseVector> {
    private IndexWriter writer;

    public LuceneIndraLoader(String baseDir, ModelMetadata metadata) throws IOException {
        super(baseDir, metadata);
        if (!metadata.sparse) {
            throw new UnsupportedOperationException("Lucene loader does not supports dense vectors.");
        }

        Directory dir = FSDirectory.open(Paths.get(this.modelDir, LuceneVectorSpace.TERMS_INDEX));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(256.0);

        this.writer = new IndexWriter(dir, iwc);
    }

    @Override
    protected void doLoad(VectorIterator<SparseVector> iter) {


        int counter = 0;
        while (iter.hasNext()) {
            SparseVector sv = iter.next();
            try {
                Document doc = createSparseDocument(sv);
                writer.addDocument(doc);

                counter++;
                if (counter % PRINT_MESSAGE_EACH == 0) {
                    System.out.println(String.format("indexing %d terms out of %d...", counter, this.vocabSize));
                }
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
