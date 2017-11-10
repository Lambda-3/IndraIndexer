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
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.corpus.Document;
import org.lambda3.indra.corpus.DocumentGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class PlainTextDocumentGeneratorTest {

    static final String BASE_DIR = PlainTextDocumentGeneratorTest.class.getClassLoader()
            .getResource("plainTextCorpus").getPath();
    static final String INPUT_DIR = Paths.get(BASE_DIR, "input").toString();

    private List<Document> countDocuments(String dir, DocumentGenerator.ContentType type, String regex) {
        CorpusMetadata md = CorpusMetadataBuilder.newCorpusMetadata("pessoa", "pt").build();

        Pattern pattern = (regex != null ? Pattern.compile(regex) : null);
        DocumentGenerator builder = new DocumentGenerator(DocumentGenerator.FileType.TEXT, type, pattern, dir);
        Iterator<Document> generator = builder.iterator();

        List<Document> docs = new LinkedList<>();
        while (generator.hasNext()) {
            docs.add(generator.next());

        }

        return docs;
    }

    @Test
    public void countDocumentAllTypeFileTest() {
        List<Document> docs = countDocuments(INPUT_DIR, DocumentGenerator.ContentType.FILE, null);
        Assert.assertEquals(docs.size(), 6);
    }

    @Test(timeOut = 20000)
    public void countDocumentMoreTypeFileTest() {
        String moreDir = Paths.get(INPUT_DIR, "more").toString();
        List<Document> docs = countDocuments(moreDir, DocumentGenerator.ContentType.FILE, null);
        Assert.assertEquals(docs.size(), 3);
    }

    @Test
    public void marSalgadoDocumentTest() {
        String file = Paths.get(INPUT_DIR, "mar_salgado.txt").toString();
        List<Document> docs = countDocuments(file, DocumentGenerator.ContentType.LINE, null);

        Assert.assertEquals(docs.size(), 12);
        Assert.assertTrue(docs.get(0).content.contains("salgado"));
        Assert.assertTrue(docs.get(11).content.contains("espelhou"));
    }

    @Test(timeOut = 20000)
    public void tabacariaDocumentTest() {
        String file = Paths.get(INPUT_DIR, "tabacaria.txt").toString();
        List<Document> docs = countDocuments(file, DocumentGenerator.ContentType.LINE, null);

        Assert.assertEquals(docs.size(), 167);
        Assert.assertTrue(docs.get(26).content.contains("aprendizagem"));
    }

    @Test
    public void regex1FilterDocumentTest() {
        List<Document> docs = countDocuments(INPUT_DIR, DocumentGenerator.ContentType.FILE, "bio(.*)");

        Assert.assertEquals(docs.size(), 2);
        for (Document doc : docs) {
            Assert.assertTrue(doc.content.startsWith("Fernando"));
        }
    }

    @Test
    public void regex2FilterDocumentTest() {
        List<Document> docs = countDocuments(INPUT_DIR, DocumentGenerator.ContentType.FILE, "(.*).txt$");

        Assert.assertEquals(docs.size(), 3);
        for (Document doc : docs) {
            Assert.assertTrue(doc.content.contains("!"));
        }
    }
}
