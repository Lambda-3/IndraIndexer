package org.lambda3.indra.indexer;

public class Document {
    public final int id;
    public final String title;
    public StringBuilder content = new StringBuilder();

    public Document(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Document simpleDocument(String content) {
        Document doc = new Document(7, null);
        doc.addContent(content);
        return doc;
    }

    public void addContent(String content) {
        this.content.append(content);
    }

    public String getContent() {
        return this.content.toString();
    }
}
