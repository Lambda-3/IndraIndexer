package org.lambda3.indra.indexer;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return id == document.id &&
                Objects.equals(content, document.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
