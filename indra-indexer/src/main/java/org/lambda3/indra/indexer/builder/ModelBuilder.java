package org.lambda3.indra.indexer.builder;

import org.lambda3.indra.indexer.Corpus;
import org.lambda3.indra.indexer.ModelMetadata;

import java.io.*;
import java.util.Map;


abstract class ModelBuilder {
    protected ModelMetadata mmdata;
    protected String outdir;


    public ModelBuilder(ModelMetadata mmdata, String outdir){
        this.mmdata = mmdata;
        this.outdir = outdir;
    }

    public abstract void build(Corpus corpus);



    public void savemodel(Map<Object, Map<Integer, Double>> model, String outdir, ModelMetadata mmdata){
        try {
            BufferedWriter bw = null;
            FileWriter fw = null;

            fw = new FileWriter(outdir);
            bw = new BufferedWriter(fw);

            try {
                //bw.write(mmdata);
                for (Object word : model.entrySet()){
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

