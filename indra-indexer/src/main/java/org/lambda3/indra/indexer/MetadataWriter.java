package org.lambda3.indra.indexer;

import org.lambda3.indra.JSONUtil;
import org.lambda3.indra.Metadata;

import java.io.File;
import java.io.IOException;

public class MetadataWriter {

    public static void write(File metadataFile, Metadata metadata) {
        try {
            JSONUtil.writeMapAsJson(metadata.asMap(), metadataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
