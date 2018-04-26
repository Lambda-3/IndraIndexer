package org.lambda3.indra.indexer.builder;

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

import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.exception.IndraRuntimeException;
import org.lambda3.indra.model.ModelMetadata;

public abstract class ModelBuilder {
    //TODO write warning when a paramter is NOT_APPLIED but is was provided by the command line.
    protected static final int NOT_APPLIED = -1;

    String outDir;
    int dimensions;
    int windowSize;
    int minWordFrequency;
    long vocabSize;

    ModelBuilder(String outDir, int dimensions, int windowSize, int minWordFrequency) {
        this.outDir = outDir;
        this.dimensions = dimensions;
        this.windowSize = windowSize;
        this.minWordFrequency = minWordFrequency;
    }

    public abstract ModelMetadata build(Corpus corpus);

    abstract ModelMetadata build(Corpus corpus, boolean keepModel);

    ModelMetadata getModelMetadata(Corpus corpus) {
        if (dimensions <= 0) {
            throw new IndraRuntimeException("dimensions should be greater than 0 (dim=" + dimensions + ")");
        }

        if (vocabSize <= 0) {
            throw new IndraRuntimeException("vocabSize should be greater than 0 (vocabSize=" + vocabSize + ")");
        }


        return new ModelMetadata(getModelName(), isSparse(), this.dimensions, this.vocabSize, this.windowSize,
                this.minWordFrequency, corpus.metadata);
    }

    public abstract boolean isSparse();

    public abstract String getModelName();
}

