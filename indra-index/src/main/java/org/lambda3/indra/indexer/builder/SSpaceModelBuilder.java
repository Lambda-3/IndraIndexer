package org.lambda3.indra.indexer.builder;

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

import edu.ucla.sspace.common.SemanticSpace;
import org.lambda3.indra.corpus.Corpus;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.indexer.writer.SSpaceModelWriter;
import org.lambda3.indra.model.ModelMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;

public abstract class SSpaceModelBuilder extends ModelBuilder {

    SemanticSpace sspace;

    SSpaceModelBuilder(String outDir, int dimensions, int windowSize) {
        super(outDir, dimensions, windowSize, NOT_APPLIED);
    }

    @Override
    public ModelMetadata build(Corpus corpus) {
        return build(corpus, false);
    }

    @Override
    ModelMetadata build(Corpus corpus, boolean keepModel) {
        SemanticSpace sspace = getSemanticSpace();
        Iterator<? extends Document> iter = corpus.getDocumentsIterator();

        try {
            while (iter.hasNext()) {
                Document doc = iter.next();

                String content = doc.content;
                if (content != null && !content.isEmpty())
                    sspace.processDocument(new BufferedReader(new StringReader(content)));
            }

            sspace.processSpace(new Properties());
            processPosRunningInformation(sspace);

            this.vocabSize = sspace.getWords().size();
            ModelMetadata metadata = getModelMetadata(corpus);
            new SSpaceModelWriter(metadata, sspace).save(outDir);

            //for test proposes.
            if(keepModel) {
                this.sspace = sspace;
            }

            return metadata;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract SemanticSpace getSemanticSpace();

    public abstract void processPosRunningInformation(SemanticSpace sspace);
}
