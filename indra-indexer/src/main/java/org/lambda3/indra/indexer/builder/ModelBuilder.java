package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.corpus.Corpus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;


abstract class ModelBuilder {
    protected ModelMetadata metadata;
    protected String outdir;


    public ModelBuilder(ModelMetadata metadata, String outdir) {
        this.metadata = metadata;
        this.outdir = outdir;
    }

    public abstract void build(Corpus corpus);


    public void savemodel(Map<Object, Map<Integer, Double>> model, String outdir, ModelMetadata mmdata) {
        try {
            BufferedWriter bw = null;
            FileWriter fw = null;

            fw = new FileWriter(outdir);
            bw = new BufferedWriter(fw);

            try {
                //bw.write(mmdata);
                for (Object word : model.entrySet()) {
                    bw.write(word + "\t" + model.get(word));
                }
                bw.close();

            } finally {
                bw.close();
            }

        } catch (Exception var15) {
            throw new RuntimeException(var15);
        }

    }


}

