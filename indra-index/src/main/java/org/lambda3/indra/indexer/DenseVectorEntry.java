package org.lambda3.indra.indexer;

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

import java.util.Map;
import java.util.Objects;

public final class DenseVectorEntry implements Map.Entry<String, double[]> {
    private String word;
    private double[] vector;

    public DenseVectorEntry(String word, double[] vector) {
        this.word = Objects.requireNonNull(word);
        this.vector = Objects.requireNonNull(vector);
    }

    @Override
    public String getKey() {
        return word;
    }

    @Override
    public double[] getValue() {
        return vector;
    }

    @Override
    public double[] setValue(double[] value) {
        throw new UnsupportedOperationException();
    }
}
