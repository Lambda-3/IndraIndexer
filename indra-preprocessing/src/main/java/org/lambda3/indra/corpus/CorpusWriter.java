package org.lambda3.indra.corpus;

/*-
 * ==========================License-Start=============================
 * indra-preprocessing
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

import org.lambda3.indra.MetadataIO;

import java.io.*;
import java.nio.file.Paths;

public class CorpusWriter implements Closeable {

    static final String CORPUS_CONTENT_FILE_NAME = "corpus.txt";

    private Writer documentWriter;
    private boolean closed = false;

    public CorpusWriter(CorpusMetadata metadata, File baseDir) throws IOException {
        File corpusDir = Paths.get(baseDir.getAbsolutePath(), metadata.corpusName).toFile();
        if (!corpusDir.exists()) {
            corpusDir.mkdirs();
        }

        MetadataIO.write(corpusDir.getAbsolutePath(), metadata);

        File contentFile = Paths.get(corpusDir.getAbsolutePath(), CORPUS_CONTENT_FILE_NAME).toFile();
        this.documentWriter = new FileWriter(contentFile);
    }

    public void addDocument(Document document) throws IOException {
        if (!closed) {
            documentWriter.write(document.content);
            documentWriter.write("\n");
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed && documentWriter != null) {
            documentWriter.flush();
            documentWriter.close();
        }
    }
}
