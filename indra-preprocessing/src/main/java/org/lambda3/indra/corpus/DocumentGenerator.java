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

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentGenerator {

    public enum ContentType {
        LINE, FILE
    }

    public enum FileType {
        TEXT, WIKI
    }

    private Iterator<Document> iterator;

    public DocumentGenerator(FileType fileType, ContentType contentType, Pattern pattern, String... files) {
        List<File> fileList = Collections.unmodifiableList(defineFiles(files, pattern));
        switch (fileType) {
            case TEXT:
                this.iterator = new DocumentIterator(contentType, fileList.iterator());
                break;
            case WIKI:
                this.iterator = new WikipediaDocumentIterator(contentType, fileList.iterator());
                break;
            default:
                throw new RuntimeException(String.format("File type not supported (%s).", contentType.toString()));
        }
    }

    public Iterator<Document> iterator() {
        return iterator;
    }

    public static List<File> defineFiles(String[] files, Pattern pattern) {
        List<File> fList = new LinkedList<>();

        Queue<File> queue = new LinkedList<>();
        Arrays.stream(files).map(File::new).forEach(queue::add);

        while (!queue.isEmpty()) {
            File inf = queue.poll();
            if (inf.isDirectory()) {
                Collections.addAll(queue, inf.listFiles());
            } else {
                Matcher m = (pattern != null ? pattern.matcher(inf.getName()) : null);
                if (m == null || m.find()) {
                    fList.add(inf);
                }
            }
        }

        fList.sort(Comparator.comparing(File::getAbsolutePath));
        return fList;
    }
}

