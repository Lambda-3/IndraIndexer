package org.lambda3.indra.pp;

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

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusWriter;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.corpus.DocumentGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class IndraPreProcessor {

    public List<File> doCheckFiles(String corpusFiles, String patternRegex) {
        Pattern pattern = (patternRegex != null ? Pattern.compile(patternRegex) : null);
        return DocumentGenerator.defineFiles(corpusFiles.split(" "), pattern);
    }

    public void doPreProcess(CorpusMetadata metadata, String corpusFiles, String patternRegex, DocumentGenerator.FileType fileType,
                             DocumentGenerator.ContentType contentType, File outputDir) {
        CorpusWriter writer = null;
        try {
            StandardPreProcessor pp = new StandardPreProcessor(metadata);
            writer = new CorpusWriter(metadata, outputDir);

            Pattern pattern = (patternRegex != null ? Pattern.compile(patternRegex) : null);
            Iterator<Document> generator = new DocumentGenerator(fileType, contentType, pattern,
                    corpusFiles.split(" ")).iterator();

            while (generator.hasNext()) {
                Document doc = generator.next();

                Iterable<String> iterable = () -> pp.process(doc.content);
                writer.addDocument(new Document(doc.id, String.join(" ", iterable)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
