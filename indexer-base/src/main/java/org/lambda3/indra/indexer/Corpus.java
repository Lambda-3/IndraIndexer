package org.lambda3.indra.indexer;

import java.io.File;
import java.util.Iterator;

public abstract class Corpus {

    public final String name;
    public final String lang;
    public final String encoding;
    private String desc;

    public Corpus(String name, String lang, String encoding) {
        this.name = name;
        this.lang = lang;
        this.encoding = encoding;
    }

    public abstract Iterator<? extends Document> getDocumentsIterator();

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}