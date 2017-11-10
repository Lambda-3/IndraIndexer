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

import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVector;

public class SparseVector extends Vector {

    public SparseVector(int dimensions, String content) {
        super(dimensions, content.split("\t"));
    }

    @Override
    public RealVector digestContent(String content) {
        RealVector vector = new OpenMapRealVector(dimensions);

        String[] parts = content.split(" ");
        for (String part : parts) {
            int sep = part.lastIndexOf(":");
            vector.addToEntry(Integer.parseInt(part.substring(0, sep)), Double.parseDouble(part.substring(sep + 1)));
        }

        return vector;
    }
}
