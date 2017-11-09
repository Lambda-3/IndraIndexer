package org.lambda3.indra.loader.annoy;

import com.spotify.annoy.jni.base.Annoy;
import com.spotify.annoy.jni.base.AnnoyIndex;
import org.apache.commons.lang3.ArrayUtils;
import org.lambda3.indra.loader.IndraLoader;
import org.lambda3.indra.loader.RawSpaceModel;
import org.lambda3.indra.loader.Vector;
import org.lambda3.indra.loader.VectorIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnoyIndraLoader implements IndraLoader {

    private final Integer NTREES = 1000;

    @Override
    public void load(RawSpaceModel rsm) {

        List<List<Float>> allVecs = new ArrayList<>();
        try {
            VectorIterator vectors = rsm.getVectorIterator();
            while (vectors.hasNext()) {
                double[] vecDouble = vectors.next().content.toArray();
                float[] vecFloat = new float[vecDouble.length];

                for (int i=0; i< vecDouble.length; i++)
                    vecFloat[i] = (float)vecDouble[i];

                Float[] doubleArray = ArrayUtils.toObject(vecFloat);
                List<Float> vec = Arrays.asList(doubleArray);
                allVecs.add(vec);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String filename = String.format("./?.annoy", rsm.modelMetadata.corpusMetadata.corpusName);

        Annoy.newIndex((int)rsm.modelMetadata.dimensions)
                .addAllItems(allVecs)
                .build(NTREES)
                .save(filename);


    }

    @Override
    public void close() throws IOException {
        //TODO implement me.
    }
}
