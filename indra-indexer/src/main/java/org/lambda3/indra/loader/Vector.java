package org.lambda3.indra.loader;

public abstract class Vector<T> {

    public final String term;
    public final T content;

    public Vector(String... parts) {
        this.term = parts[0];
        content = digestContent(parts[1]);
    }

    public abstract T digestContent(String content);
}
