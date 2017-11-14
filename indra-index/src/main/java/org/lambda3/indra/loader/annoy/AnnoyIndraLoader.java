package org.lambda3.indra.loader.annoy;

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

import com.spotify.annoy.jni.base.Annoy;
import org.lambda3.indra.core.annoy.AnnoyVectorSpace;
import org.lambda3.indra.exception.IndraRuntimeException;
import org.lambda3.indra.loader.LocalStoredIndraLoader;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.DenseVector;
import org.lambda3.indra.util.VectorIterator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AnnoyIndraLoader extends LocalStoredIndraLoader<DenseVector> {

    private static final Integer NTREES = 1000;
    private int dimensions;
    private String indexFile;
    private File mappingsFile;

    public AnnoyIndraLoader(String baseDir, ModelMetadata metadata) {
        super(baseDir, metadata);
        if (metadata.sparse) {
            throw new UnsupportedOperationException("Annoy loader does not supports sparse vectors.");
        }

        this.dimensions = (int) metadata.dimensions;
        this.indexFile = Paths.get(this.modelDir, AnnoyVectorSpace.TREE_FILE).toString();
        this.mappingsFile = Paths.get(this.modelDir, AnnoyVectorSpace.WORD_MAPPING_FILE).toFile();
    }

    private List<Float> toList(double[] vector) {
        List<Float> lf = new ArrayList<>(vector.length);

        for (double aVector : vector) {
            lf.add((float) aVector);
        }

        return lf;
    }

    @Override
    protected void doLoad(VectorIterator<DenseVector> iter) {
        Annoy.Builder annoyBuilder = Annoy.newIndex(dimensions);

        FileWriter fw = null;
        try {
            fw = new FileWriter(this.mappingsFile);
            fw.write(vocabSize + "\n");

            int counter = 0;
            while (iter.hasNext()) {
                DenseVector dv = iter.next();
                List<Float> vec = toList(dv.content.toArray());
                annoyBuilder.addItem(counter, vec);

                fw.write(String.format("%d|%s", counter, dv.term));
                fw.write("\n");

                counter++;

                if (counter % PRINT_MESSAGE_EACH == 0) {
                    System.out.println(String.format("indexing %d terms out of %d...", counter, this.vocabSize));
                }
            }

            annoyBuilder.build(NTREES).save(indexFile);
        } catch (IOException e) {
            throw new IndraRuntimeException("error indxing annoy", e);
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void close() throws IOException {
        //do nothing
    }
}
