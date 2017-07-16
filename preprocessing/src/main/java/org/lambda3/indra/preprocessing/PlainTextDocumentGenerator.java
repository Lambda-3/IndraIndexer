package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.Document;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlainTextDocumentGenerator extends DocumentGenerator {

    public enum ContentType {
        LINE_DOCUMENT, FILE_DOCUMENT
    }

    public final ContentType type;
    public Iterator<File> files;
    public final Preprocessor pp;

    public PlainTextDocumentGenerator(CorpusMetadata metadata, ContentType type, Pattern pattern, String... files) {
        super(metadata);
        this.type = type;
        this.files = defineFiles(files, pattern).iterator();
        this.pp = new StandardPreprocessor(metadata);
    }

    @Override
    public Iterator<Document> iterator() {
        return new DocumentIterator(files, type);
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

        return Collections.unmodifiableList(fList);
    }


    static class DocumentIterator implements Iterator<Document> {
        Iterator<File> files;
        ContentType type;
        File currentFile;
        BufferedReader reader = null;
        Document next;

        DocumentIterator(Iterator<File> files, ContentType type) {
            this.files = files;
            this.type = type;
            next();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Document next() {
            try {
                switch (type) {
                    case LINE_DOCUMENT:
                        return nextLine();
                    case FILE_DOCUMENT:
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
            if (files.hasNext()) {
                StringBuilder content = new StringBuilder();
                currentFile = files.next();
                reader = new BufferedReader(new FileReader(currentFile));
                reader.lines().forEach(l -> content.append(l + " "));

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
                if (files.hasNext()) {
                    currentFile = files.next();
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
                    next = Document.simpleDocument(line);
                }
            } else {
                next = null;
            }


            return thisNext;
        }
    }
}
