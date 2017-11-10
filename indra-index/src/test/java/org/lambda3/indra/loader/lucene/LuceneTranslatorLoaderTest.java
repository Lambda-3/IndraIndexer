package org.lambda3.indra.loader.lucene;

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

import org.apache.commons.io.FileUtils;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.MutableTranslatedTerm;
import org.lambda3.indra.core.lucene.LuceneTranslator;
import org.lambda3.indra.corpus.CorpusMetadata;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class LuceneTranslatorLoaderTest {

    private Map<String, Set<String>> cases = new HashMap<>();

    @BeforeTest
    public void loadCases() {
        cases.put("higina", Collections.singleton("higiene"));
        cases.put("mp-five", Collections.singleton("mp-5"));
        cases.put("zambilla", Collections.singleton("zambilla"));
        cases.put("two-superpower", Collections.singleton("bipolar"));
        cases.put("popblast", new HashSet<>(Arrays.asList("records", "editora", "poplast")));
        cases.put("sugita", new HashSet<>(Arrays.asList("suguita", "doutor")));
        cases.put("sabiduría", new HashSet<>(Arrays.asList("sabedoria", "sua", "da")));
        cases.put("forthecarouselhouse", new HashSet<>(Arrays.asList("tem", "e", "cá", "casa", "carrocel", "da", "do")));
    }

    @Test
    public void ptTest() {

        try {
            String ptDir = getClass().getClassLoader().getResource("translations/pt").getPath();
            File tmpFile = Files.createTempDirectory("indra-trans").toFile();
            LuceneTranslatorLoader.load(ptDir, tmpFile.getAbsolutePath());
            CorpusMetadata cm = MetadataIO.load(tmpFile.getAbsolutePath(), CorpusMetadata.class);

            LuceneTranslator translator = new LuceneTranslator(tmpFile.getAbsolutePath());
            Assert.assertEquals(cm, translator.getCorpusMetadata());

            for (String cs : cases.keySet()) {
                MutableTranslatedTerm term = new MutableTranslatedTerm(cs, Collections.singletonList(cs));
                translator.translate(Collections.singletonList(term));

                Set<String> setBase = cases.get(cs);
                List<String> translated = term.getTranslatedTokens().get(cs);

                Assert.assertEquals(setBase.size(), translated.size());
                for (String t : translated) {
                    Assert.assertTrue(setBase.contains(t));
                }
            }

            FileUtils.deleteDirectory(tmpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
