package org.lambda3.indra.loader.annoy;

import com.spotify.annoy.jni.base.Annoy;
import com.spotify.annoy.jni.base.AnnoyIndex;
import edu.ucla.sspace.vector.*;
import org.apache.commons.lang3.ArrayUtils;
import org.lambda3.indra.core.annoy.AnnoyVectorSpace;
import org.lambda3.indra.loader.*;
import org.lambda3.indra.loader.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnnoyIndraLoader implements IndraLoader {

    private final Integer NTREES = 1000;
    private String baseDir;

    public AnnoyIndraLoader(String baseDir){
        this.baseDir = baseDir;
    }

    @Override
    public void load(RawSpaceModel rsm) {

        List<String> allItems = new ArrayList<>();
        Annoy.Builder annoyBuilder = Annoy.newIndex((int)rsm.modelMetadata.dimensions);
        int indexer = 0;
        try {
            VectorIterator vectors = rsm.getVectorIterator();
            while (vectors.hasNext()) {
                Vector vector = vectors.next();
                double[] vecDouble = vector.content.toArray();
                float[] vecFloat = new float[vecDouble.length];

                for (int i=0; i< vecDouble.length; i++)
                    vecFloat[i] = (float)vecDouble[i];

                Float[] doubleArray = ArrayUtils.toObject(vecFloat);
                List<Float> vec = Arrays.asList(doubleArray);
                annoyBuilder.addItem(indexer, vec);
                allItems.add(vector.term);
                indexer+=1;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        File modelDirFile = Paths.get(baseDir, rsm.modelMetadata.getConciseName()).toFile();
        if (!modelDirFile.exists()) {
            modelDirFile.mkdirs();
        }

        String modelDir = modelDirFile.getAbsolutePath();

        File fileAnnoy =  Paths.get(modelDir, AnnoyVectorSpace.TREE_FILE).toFile();
        annoyBuilder.build(NTREES).save(fileAnnoy.toString());

        File fileMapping =  Paths.get(modelDir, AnnoyVectorSpace.WORD_MAPPING_FILE).toFile();
        saveMapping(fileMapping,allItems);
        allItems.clear();

    }

    @Override
    public void close() throws IOException {
        //TODO implement me.
    }

    public void saveMapping(File file, List<String> allItems){
        FileWriter fw;
        try {
            fw = new FileWriter(file);

            try {
                fw.write(String.format("%d", allItems.size()));
                fw.write("\n");
                    for (int i = 0; i < allItems.size(); i++) {
                        fw.write(String.format("%d|%s", i,allItems.get(i)));
                        fw.write("\n");
                    }


                fw.flush();
                fw.close();
            } finally {
                fw.close();
            }

        } catch (IOException e) {
            //TODO review here - log
            throw new RuntimeException(e);
        }

    }
}
