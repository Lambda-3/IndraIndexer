package org.lambda3.indra.indexer.writer;

import com.google.common.io.LittleEndianDataOutputStream;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.TermVector;

import java.io.IOException;

public class TermVectorModelWriter extends ModelWriter {

    private Iterable<TermVector> vectors;

    public TermVectorModelWriter(ModelMetadata metadata, Iterable<TermVector> vectors) {
        super(metadata);
        this.vectors = vectors;
    }

    @Override
    void writeSparse(LittleEndianDataOutputStream stream) throws IOException {
        throw new UnsupportedOperationException("TermVector does not support sparse models.");
    }

    @Override
    void writeDense(LittleEndianDataOutputStream stream) throws IOException {
        for (TermVector vector : vectors) {
            writeTerm(vector.term, stream);

            for (int i = 0; i < vector.content.getDimension(); i++) {
                stream.writeFloat((float) vector.content.getEntry(i));
            }
        }
    }
}
