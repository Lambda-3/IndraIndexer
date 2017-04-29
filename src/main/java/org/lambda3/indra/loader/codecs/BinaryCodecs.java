package org.lambda3.indra.loader.codecs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BinaryCodecs {
    /**
     * Sparse vectors serialization to byte array.
     * This is the current implementation expected by Indra. Must be used for binary encoded vectors.
     * Look for MongoVectorSpace#unmarshall.
     *
     * TODO: Avoid this duplication and possible miss behavior between gnerated models and their consumption.
     * Suggestion: Currently we have two options:
     *  (1) Merge this with the whole Indra.
     *  (2) Create a shared library between both projects (i.e indra-codecs)
     */
    public static byte[] marshall(Map<Integer, Double> vector) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (DataOutputStream tdos = new DataOutputStream(baos)) {
                tdos.writeInt(vector.size());
                for (Integer key : vector.keySet()) {
                    tdos.writeInt(key);
                    tdos.writeFloat(vector.get(key).floatValue());
                }
                return baos.toByteArray();
            }
        }
    }

    /**
     * Dense vectors serialization to byte array.
     * This is different the earlier model builders (dinfra) even for dense vectors.
     * The difference must be taken into account by Indra consumer looking the metadata.loader-id.
     */
    public static byte[] marshall(double[] vector) throws IOException  {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (DataOutputStream tdos = new DataOutputStream(baos)) {
                for (int i = 0; i < vector.length; i++) {
                    tdos.writeDouble((vector[i]));
                }
                return baos.toByteArray();
            }
        }
    }

    public static double[] unmarshall(byte[] bytes) throws IOException {
        List<Double> vector = new ArrayList<>(300); // just a guess to improve performance, no big deal if bigger or smaller.
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        try {
            while (true) {
                vector.add(dis.readDouble());
            }
        }
        catch(EOFException e) {
            // done reading.
        }

        return vector.stream().mapToDouble(Double::doubleValue).toArray();
    }
}
