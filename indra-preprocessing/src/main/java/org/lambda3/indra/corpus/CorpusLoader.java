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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CorpusLoader {

    public static Corpus load(File corpusBaseDir) throws IOException {
        CorpusMetadata metadata = MetadataIO.load(corpusBaseDir.getAbsolutePath(), CorpusMetadata.class);

        File contentFile = Paths.get(corpusBaseDir.getAbsolutePath(), CorpusWriter.CORPUS_CONTENT_FILE_NAME).toFile();
        return new Corpus(metadata, DocumentGenerator.ContentType.LINE, contentFile);
    }
}


