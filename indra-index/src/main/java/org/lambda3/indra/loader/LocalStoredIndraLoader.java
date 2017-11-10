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

import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.model.ModelMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public abstract class LocalStoredIndraLoader<V extends Vector> implements IndraLoader<V> {

    protected String modelDir;

    public LocalStoredIndraLoader(String baseDir, ModelMetadata metadata) {
        File modelDirFile = Paths.get(baseDir, metadata.getConciseName()).toFile();
        if (!modelDirFile.exists()) {
            modelDirFile.mkdirs();
        }

        this.modelDir = modelDirFile.getAbsolutePath();
    }

    protected abstract void doLoad(VectorIterator<V> iter);

    @Override
    public void load(RawSpaceModel<V> rsm) throws FileNotFoundException {
        MetadataIO.write(this.modelDir, rsm.modelMetadata);
        doLoad(rsm.getVectorIterator());
    }

}
