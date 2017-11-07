package org.lambda3.indra.corpus;

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

