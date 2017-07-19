package org.lambda3.indra.corpus;

public class WikipediaDocument extends Document {
    public final String title;

    public WikipediaDocument(int id, String content, String title) {
        super(id, content);
        this.title = title;
    }
}
