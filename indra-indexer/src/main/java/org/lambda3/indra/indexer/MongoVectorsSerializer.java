package org.lambda3.indra.indexer;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.lambda3.indra.client.ModelMetadata;
import org.lambda3.indra.core.codecs.BinaryCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MongoVectorsSerializer {
    private static Logger logger = LoggerFactory.getLogger(MongoVectorsSerializer.class);

    public static void write(DenseVectorGenerator gen, MongoClientURI mongoURI, ModelMetadata metadata) {

        if (metadata.isSparse()) { //TODO
            throw new UnsupportedOperationException("Sparse vectors not supported yet");
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
                try {
                    DBObject obj;

                    if (!metadata.isBinary()) {
                        obj = BasicDBObjectBuilder.start()
                                .add("term", entry.getKey()).add("vector", entry.getValue()).get();
                    } else {
                        byte[] marshalledVector = BinaryCodecs.marshall(entry.getValue());
                        obj = BasicDBObjectBuilder.start()
                                .add("term", entry.getKey()).add("vector", marshalledVector).get();
                    }

                    objects.add(obj);
                    c += 1;

                    if (objects.size() % 101 == 0) {
                        logger.info("{} - Sending batch of documents: {} ..", c, objects.get(0).get("term"));
                        modelColl.insertMany(objects);
                        objects.clear();
                    }
                }
                catch (Exception e) {
                    logger.error("Fail process {}. Skipped.", entry.getKey(), e);
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

    private static Map<String, Object> asMap(ModelMetadata metadata) {
        return new HashMap<String, Object>(){{
            put(ModelMetadata.SPARSE_PARAM, metadata.isSparse());
            put(ModelMetadata.APPLY_STEMMER_PARAM, metadata.getApplyStemmer());
            put(ModelMetadata.APPLY_STOP_WORDS_PARAM, metadata.isApplyStopWords());
            put(ModelMetadata.REMOVE_ACCENTS_PARAM, metadata.isRemoveAccents());
            put(ModelMetadata.APPLY_LOWERCASE_PARAM, metadata.isApplyLowercase());
            put(ModelMetadata.BINARY_PARAM, metadata.isBinary());
            put(ModelMetadata.MIN_WORD_LENGTH_PARAM, metadata.getMinWordLength());
            put(ModelMetadata.MAX_WORD_LENGTH_PARAM, metadata.getMaxWordLength());
            put(ModelMetadata.DIMENSIONS_PARAM, metadata.getDimensions());
            put(ModelMetadata.STOP_WORDS_PARAM, metadata.getStopWords());
            put(ModelMetadata.LOADER_ID_PARAM, metadata.getLoaderId());
        }};
    }

}
