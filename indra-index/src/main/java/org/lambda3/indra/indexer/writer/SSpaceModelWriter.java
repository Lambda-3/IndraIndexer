package org.lambda3.indra.indexer.writer;

import com.google.common.io.LittleEndianDataOutputStream;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.vector.SparseVector;
import edu.ucla.sspace.vector.Vector;
import org.lambda3.indra.model.ModelMetadata;

import java.io.IOException;

public class SSpaceModelWriter extends ModelWriter {

    private SemanticSpace sspace;

    public SSpaceModelWriter(ModelMetadata metadata, SemanticSpace sspace) {
        super(metadata);
        this.sspace = sspace;
    }

    @Override
    void writeSparse(LittleEndianDataOutputStream stream) throws IOException {
        for (String word : sspace.getWords()) {
            SparseVector<Double> vector = (SparseVector<Double>) sspace.getVector(word);
            int[] indices = vector.getNonZeroIndices();

            writeTerm(word, stream);
            stream.writeInt(indices.length);

            for (int i : indices) {
                stream.writeInt(i);
                stream.writeFloat(vector.getValue(i).floatValue());
            }
        }
    }

    @Override
    void writeDense(LittleEndianDataOutputStream stream) throws IOException {
        for (String word : sspace.getWords()) {
            writeTerm(word, stream);

            Vector<Double> vector = sspace.getVector(word);
            for (int i = 0; i < vector.length(); i++) {
                stream.writeFloat(vector.getValue(i).floatValue());
            }
        }
    }
}
