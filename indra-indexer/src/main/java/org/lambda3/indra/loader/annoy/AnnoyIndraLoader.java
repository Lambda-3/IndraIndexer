package org.lambda3.indra.loader.annoy;

import com.spotify.annoy.jni.base.Annoy;
import org.lambda3.indra.core.annoy.AnnoyVectorSpace;
import org.lambda3.indra.exception.IndraRuntimeException;
import org.lambda3.indra.loader.DenseVector;
import org.lambda3.indra.loader.LocalStoredIndraLoader;
import org.lambda3.indra.loader.VectorIterator;
import org.lambda3.indra.model.ModelMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AnnoyIndraLoader extends LocalStoredIndraLoader<DenseVector> {

    private static final Integer NTREES = 1000;
    private int dimensions;
    private long vocabSize;
    private String indexFile;
    private File mappingsFile;

    public AnnoyIndraLoader(String baseDir, ModelMetadata metadata) {
        super(baseDir, metadata);
        this.dimensions = (int) metadata.dimensions;
        this.vocabSize = metadata.vocabSize;
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
