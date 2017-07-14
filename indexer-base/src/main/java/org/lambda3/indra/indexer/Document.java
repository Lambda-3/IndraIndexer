package org.lambda3.indra.indexer;

public class Document {
    public final int id;
    public final String content;

    public Document(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public static Document simpleDocument(String content) {
        Document doc = new Document(0, content);
        return doc;
    }
}
