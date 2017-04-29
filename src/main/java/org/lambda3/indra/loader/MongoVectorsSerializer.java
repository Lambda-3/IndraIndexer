package org.lambda3.indra.loader;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.lambda3.indra.client.ModelMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class MongoVectorsSerializer {
    private static Logger logger = LoggerFactory.getLogger(MongoVectorsSerializer.class);

    public static void write(DenseVectorGenerator gen, MongoClientURI mongoURI, ModelMetadata metadata) {

        if (metadata.isSparse()) { //TODO
            throw new UnsupportedOperationException("Sparse vectors not supported yet");
        }

        if (metadata.isBinary()) { //TODO
            throw new UnsupportedOperationException("Binary encoded vector is not supported yet.");

        }

        logger.info("Writing to {}", mongoURI);

        try (MongoClient mongoClient = new MongoClient(mongoURI)) {
            final String dbName = mongoURI.getDatabase();
            final MongoDatabase db = mongoClient.getDatabase(dbName);
            final MongoCollection<DBObject> modelColl = db.getCollection("terms", DBObject.class);
            final MongoCollection<Document> metaColl = db.getCollection("metadata");

            logger.info("Deleting old data.");
            modelColl.drop();
            metaColl.drop();

            Iterator<Map.Entry<String, double[]>> it = gen.iterator();

            List<DBObject> objects = new ArrayList<>(100);

            int c = 0;
            while (it.hasNext()) {
                Map.Entry<String, double[]> entry = it.next();
                DBObject obj = BasicDBObjectBuilder.start()
                        .add("term", entry.getKey()).add("vector", entry.getValue()).get();

                objects.add(obj);
                c += 1;

                if (objects.size() % 101 == 0) {
                    logger.info("{} - Sending batch of documents: {} ..", c, objects.get(0).get("term"));
                    modelColl.insertMany(objects);
                    objects.clear();
                }
            }

            if (!objects.isEmpty()) {
                modelColl.insertMany(objects);
                objects.clear();
            }

            logger.info("Building indices");
            modelColl.createIndex(new BasicDBObject("term", 1));

            logger.info("Updating metadata");
            Document metaDoc = new Document(asMap(metadata));
            metaColl.insertOne(metaDoc);

        }
    }

    /**
     * This is the current implementation expected by Indra. Must be used for binary encoded vectors.
     * Look for MongoVectorSpace#unmarshall.
     *
     * TODO: Avoid this duplication and possible miss behavior between gnerated models and their consumption.
     * Suggestion: Currently we have two options:
     *  (1) Merge this with the whole Indra.
     *  (2) Create a shared library between both projects (i.e indra-codecs)
     */
    private static byte[] marshall(Map<Integer, Double> vector) throws IOException {
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

    private static Map<String, Object> asMap(ModelMetadata metadata) {
        return new HashMap<String, Object>(){{
            put("sparse", metadata.isSparse());
            put("apply-stemmer", metadata.isApplyStemmer());
            put("apply-stop-words", metadata.isApplyStopWords());
            put("remove-accents", metadata.isRemoveAccents());
            put("binary", metadata.isBinary());
            put("min-word-length", metadata.getMinWordLength());
            put("max-word-length", metadata.getMaxWordLength());
            put("dimensions", metadata.getDimensions());
            put("stop-words", metadata.getStopWords());
        }};
    }

}
