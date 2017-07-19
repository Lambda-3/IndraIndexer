package org.lambda3.indra.indexer.builder;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.vector.Vector;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public abstract class SSpaceModelBuilder extends ModelBuilder {

    protected SemanticSpace sspace;
    protected Properties properties = new Properties();


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

            sspace.processSpace(properties);
            Map<Object, Map<Integer, Double>> model = new HashMap<>();

            for (String term : sspace.getWords()) {
                Vector<Double> vector = sspace.getVector(term);
                model.put(term, convertVector(vector));
            }
            savemodel(model, outdir, metadata);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<Integer, Double> convertVector(Vector<Double> vector) {

        if (vector != null) {
            Map<Integer, Double> result = new HashMap<>();

            double value;
            for (int i = 0; i < vector.length(); i++) {
                value = vector.getValue(i).doubleValue();
                if (value != 0)
                    result.put(i, value);
            }

            return result;
        }

        return null;
    }
}
