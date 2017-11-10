package org.lambda3.indra.loader;

/*-
 * ==========================License-Start=============================
 * indra-index
 * --------------------------------------------------------------------
 * Copyright (C) 2017 Lambda^3
 * --------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ==========================License-End===============================
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

public class VectorIterator<V extends Vector> implements Iterator<V> {

    private final boolean sparse;
    private Iterator<String> iterator;
    private int dimensions;

    public VectorIterator(File vectorsFile, long dimensions, Class<V> clazz) throws FileNotFoundException {
        this.sparse = clazz.equals(SparseVector.class);
        this.dimensions = (int) dimensions;
        this.iterator = new BufferedReader(new FileReader(vectorsFile)).lines().iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V next() {
        String content = iterator.next();
        if (this.sparse) {
            return (V) new SparseVector(dimensions, content);
        } else {
            return (V) new DenseVector(content);
        }
    }
}
