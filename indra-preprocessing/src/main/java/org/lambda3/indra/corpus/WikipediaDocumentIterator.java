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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.Iterator;

public class WikipediaDocumentIterator extends DocumentIterator {
    private static final String URL = "url";
    private static final String TEXT = "text";
    private static final String ID = "id";
    private static final String WIKI_DESC = "wikipedia.org/wiki/";

    WikipediaDocumentIterator(DocumentGenerator.ContentType type, Iterator<File> files) {
        super(type, files);
    }

    @Override
    public Document createDocument(String content) {
        JSONParser jsonParser = new JSONParser();
        JSONObject page = null;
        try {
            page = (JSONObject) jsonParser.parse(content);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String id = (String) page.get(ID);
        String title = (String) page.get(URL);
        int offset = title.indexOf(WIKI_DESC);
        if (offset > 0) {
            title = title.substring(offset + WIKI_DESC.length());
        }
        String text = (String) page.get(TEXT);

        return new WikipediaDocument(id, text, title);
    }
}
