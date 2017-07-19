package org.lambda3.indra.corpus;

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

        int id = (int) page.get(ID);
        String title = (String) page.get(URL);
        int offset = title.indexOf(WIKI_DESC);
        if (offset > 0) {
            title = title.substring(offset + WIKI_DESC.length());
        }
        String text = (String) page.get(TEXT);

        return new WikipediaDocument(id, text, title);
    }
}
