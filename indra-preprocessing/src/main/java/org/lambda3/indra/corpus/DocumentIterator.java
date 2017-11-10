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

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

public class DocumentIterator implements Iterator<Document> {
    private Iterator<File> fileIterator;
    private DocumentGenerator.ContentType type;
    private File currentFile;
    private BufferedReader reader = null;
    private Document next;

    DocumentIterator(DocumentGenerator.ContentType type, File... fileIterator) {
        this(type, Arrays.asList(fileIterator).iterator());

    }

    DocumentIterator(DocumentGenerator.ContentType type, Iterator<File> fileIterator) {
        this.type = type;
        this.fileIterator = fileIterator;
        next();
    }


    protected Document createDocument(String content) {
        return Document.simpleDocument(content);
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public synchronized Document next() {
        try {
            switch (type) {
                case LINE:
                    return nextLine();
                case FILE:
                    return nextFile();
                default:
                    throw new RuntimeException("invalid ContentType (" + type + ")");
            }
        } catch (IOException e) {
            System.out.println("Problem reading file " + currentFile.getAbsolutePath());
            System.exit(9);
            return null;
        }
    }

    private Document nextFile() throws FileNotFoundException {
        Document thisNext = next;
        if (fileIterator.hasNext()) {
            StringBuilder content = new StringBuilder();
            currentFile = fileIterator.next();
            reader = new BufferedReader(new FileReader(currentFile));
            reader.lines().forEach(l -> {
                content.append(l);
                content.append(" ");
            });

            next = Document.simpleDocument(content.toString());
        } else {
            next = null;
        }

        return thisNext;
    }

    private Document nextLine() throws IOException {
        Document thisNext = next;

        String line = null;
        while (reader == null || (line = reader.readLine()) == null) {
            if (fileIterator.hasNext()) {
                currentFile = fileIterator.next();
                reader = new BufferedReader(new FileReader(currentFile));
            } else {
                break;
            }
        }

        if (line != null) {
            line = line.trim();
            if (line.isEmpty()) {
                return next();
            } else {
                next = createDocument(line);
            }
        } else {
            next = null;
        }

        return thisNext;
    }
}
