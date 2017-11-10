package org.lambda3.indra.loader.mongo;

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

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.lambda3.indra.core.codecs.BinaryCodecs;
import org.lambda3.indra.indexer.DenseVectorGenerator;
import org.lambda3.indra.model.ModelMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MongoVectorsSerializer {
    private static Logger logger = LoggerFactory.getLogger(MongoVectorsSerializer.class);

    public static void write(DenseVectorGenerator gen, MongoClientURI mongoURI, ModelMetadata metadata) {

        if (metadata.sparse) { //TODO
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
                    byte[] marshalledVector = BinaryCodecs.marshall(entry.getValue());
                    DBObject obj = BasicDBObjectBuilder.start()
                            .add("term", entry.getKey()).add("vector", marshalledVector).get();

                    objects.add(obj);
                    c += 1;

                    if (objects.size() % 101 == 0) {
                        logger.info("{} - Sending batch of documents: {} ..", c, objects.get(0).get("term"));
                        modelColl.insertMany(objects);
                        objects.clear();
                    }
                } catch (Exception e) {
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
            Document metaDoc = new Document(metadata.asMap());
            metaColl.insertOne(metaDoc);

        }
    }
}
