package org.lambda3.indra.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

public class VectorIterator implements Iterator<Vector> {

    public final boolean sparse;
    private Iterator<String> iterator;

    public VectorIterator(boolean sparse, File vectorsFile) throws FileNotFoundException {
        this.sparse = sparse;
        this.iterator = new BufferedReader(new FileReader(vectorsFile)).lines().iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Vector next() {
        String content = iterator.next();

        return null;
    }
}
