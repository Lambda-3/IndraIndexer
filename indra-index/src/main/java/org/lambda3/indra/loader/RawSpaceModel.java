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

import org.lambda3.indra.indexer.ModelWriter;
import org.lambda3.indra.model.ModelMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class RawSpaceModel<V extends Vector> {

    public final ModelMetadata modelMetadata;
    private File vectorFileAbsolutePath;
    private Class<V> clazz;

    public RawSpaceModel(String baseDir, ModelMetadata modelMetadata, Class<V> clazz) {
        this.vectorFileAbsolutePath = Paths.get(baseDir, ModelWriter.MODEL_CONTENT_FILE_NAME).toFile();
        this.modelMetadata = modelMetadata;
        this.clazz = clazz;
    }

    public boolean isSparse() {
        return modelMetadata.sparse;
    }

    public VectorIterator<V> getVectorIterator() throws FileNotFoundException {
        return new VectorIterator<V>(this.vectorFileAbsolutePath, modelMetadata.dimensions, clazz);
    }
}
