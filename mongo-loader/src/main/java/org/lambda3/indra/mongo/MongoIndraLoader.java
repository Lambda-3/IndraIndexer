package org.lambda3.indra.mongo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.mongodb.MongoClientURI;
import org.lambda3.indra.client.ModelMetadata;
import org.lambda3.indra.indexer.DenseVectorGenerator;
import org.lambda3.indra.indexer.Word2VecGenerator;

import java.io.File;

/**
 * Main Entry point of all loaders/importers/generators.
 */
public final class MongoIndraLoader {

    private static final String LOADER_ID = "org.lambda3.indra.mongo.MongoIndraLoader";

    public static void main(String... args) {
        MainCommand main = new MainCommand();
        JCommander jc = new JCommander(main);
        jc.setProgramName(LOADER_ID);

        ImportW2VCommand importW2VCmd = new ImportW2VCommand();
        jc.addCommand("import-w2v", importW2VCmd);

        try {
            jc.parse(args);
            if (jc.getParsedCommand() != null) {
                if (jc.getParsedCommand().equals("import-w2v")) {
                    ModelMetadata metadata = importW2VCmd.buildMetadata();
                    DenseVectorGenerator vectorGenerator = Word2VecGenerator.readFromFile(importW2VCmd.dumpFile);
                    MongoClientURI mongoClientURI = new MongoClientURI(importW2VCmd.mongoURI);
                    MongoVectorsSerializer.write(vectorGenerator, mongoClientURI, metadata);
                }
            } else {
                jc.usage();
            }
        } catch (ParameterException e) {
            e.printStackTrace();
            jc.usage();
        }
    }

    /**
     * Common parameters for all commands.
     */
    @Parameters(commandDescription = "See commands below.")
    private static final class MainCommand {
        @Parameter(names = "--help", help = true, description = "You know this..")
        boolean help;
    }

    /**
     * Captures only the relevants features of a Word2Vec model consumed by Indra.
     */
    @Parameters(commandDescription = "Import Word2Vec models.", separators = "=")
    private static final class ImportW2VCommand {
        @Parameter(names = {"-f", "--file"}, required = true, description = "W2V input model file.", order = 0)
        File dumpFile;

        @Parameter(names = {"-m", "--mongoURI"}, required = true, description = "Mongo URI target destination.", order = 10)
        String mongoURI;

        @Parameter(names = {"--binary-vectors"}, description = "Encode vector as a binary payload.", arity = 1, order = 30)
        boolean binary = true;

        @Parameter(names = {"--apply-stemmer"}, description = "Number of times the stemmer must be applied. 0 for none.", arity = 1, order = 31)
        int applyStemmer = 3;

        @Parameter(names = {"--apply-lowercase"}, description = "Lowercase words before query?", arity = 1, order = 32)
        boolean applyLowercase = true;

        @Parameter(names = {"--remove-accents"}, description = "Remove accents before query?", arity = 1, order = 33)
        boolean removeAccents = true;

        @Parameter(names = {"--apply-stopwords"}, description = "Apply stop words before query?", arity = 1, order = 34)
        boolean applyStopWords = true;

        @Parameter(names = {"--dimensions"}, description = "Number of dimensions being imported. No cut or check is performed!", order = 40)
        int dimensions = 300;

        @Parameter(names = {"--min-words-len"}, description = "Min length of each word", order = 50)
        int minWordsLen = 3;

        @Parameter(names = {"--max-words-len"}, description = "Max length of each word", order = 60)
        int maxWordsLen = 100;

        ModelMetadata buildMetadata() {
            return ModelMetadata.createDefault()
                    .loaderId(LOADER_ID) // Could be improved to use the project version
                    .sparse(false) //w2v is always dense
                    .applyStemmer(applyStemmer)
                    .applyStopWords(applyStopWords)
                    .applyLowercase(applyLowercase)
                    .removeAccents(removeAccents)
                    .binary(binary)
                    .minWordLength(minWordsLen)
                    .maxWordLength(maxWordsLen)
                    .dimensions(dimensions);
        }

        @Override
        public String toString() {
            return "ImportW2VCommand{" +
                    "dumpFile=" + dumpFile +
                    ", mongoURI='" + mongoURI + '\'' +
                    ", binary=" + binary +
                    ", applyStemmer=" + applyStemmer +
                    ", applyLowercase=" + applyLowercase +
                    ", removeAccents=" + removeAccents +
                    ", applyStopWords=" + applyStopWords +
                    ", dimensions=" + dimensions +
                    ", minWordsLen=" + minWordsLen +
                    ", maxWordsLen=" + maxWordsLen +
                    '}';
        }
    }

}
