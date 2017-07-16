package org.lambda3.indra.preprocessing;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.lambda3.indra.indexer.CorpusMetadata;
import org.lambda3.indra.indexer.CorpusMetadataBuilder;
import org.lambda3.indra.preprocessing.transform.MultiWordsTransformer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IndraPreProcessor {

    public static void main(String... args) {
        String version = IndraPreProcessor.class.getPackage().getImplementationVersion();

        MainCommand main = new MainCommand();
        JCommander jc = new JCommander(main);
        jc.setProgramName(IndraPreProcessor.class.getSimpleName() + "-" + (version != null ? version : "IDE"));

        PreProcessCommand ppCmd = new PreProcessCommand();
        jc.addCommand(PreProcessCommand.CMD, ppCmd);

        CheckFilesCommand checkCmd = new CheckFilesCommand();
        jc.addCommand(CheckFilesCommand.CMD, checkCmd);

        try {
            jc.parse(args);
            if (jc.getParsedCommand() != null) {
                if (jc.getParsedCommand().equals(PreProcessCommand.CMD)) {
                    CorpusMetadata metadata = ppCmd.getMetadata();

                    DocumentGenerator cb;
                    StandardPreprocessor pp = new StandardPreprocessor(metadata);

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

    @Parameters(commandDescription = "See commands below.")
    private static final class CheckFilesCommand {
        static final String CMD = "check";
        @Parameter(names = {"-f", "--files"}, required = true, description = "Input text corpus files or directories.", order = 0)
        File corpusFiles;

        @Parameter(names = {"-r", "--regex"}, description = "Regex to filter files into the directories.", order = 1)
        String patternRegex = null;
    }

    @Parameters(commandDescription = "Pre process text corpora.", separators = "=")
    private static class PreProcessCommand {
        static final String CMD = "preprocess";

        @Parameter(names = {"-f", "--files"}, required = true, description = "Input text corpus files or directories.", order = 0)
        File corpusFiles;

        @Parameter(names = {"-n", "--name"}, required = true, description = "Corpus name.", order = 1)
        String corpusName;

        @Parameter(names = {"-l", "--lang"}, required = true, description = "Corpus language.", order = 2)
        String language;

        @Parameter(names = {"-r", "--regex"}, description = "Regex to filter files into the directories.", order = 1)
        String patternRegex = null;

        @Parameter(names = {"-d", "--desc"}, description = "Corpus description.", order = 31)
        String description = null;

        @Parameter(names = {"-e", "--encoding"}, description = "File text encoding.", order = 40)
        String encoding = null;

        @Parameter(names = {"--stemmer"}, description = "Number of times the stemmer must be applied. 0 for none.", order = 50)
        long applyStemmer = -1;

        @Parameter(names = {"--remove-accents"}, description = "Remove accents before query?", arity = 1, order = 60)
        Boolean removeAccents = null;

        @Parameter(names = {"--lower"}, description = "Lowercase words before query?", arity = 1, order = 70)
        Boolean applyLowercase = null;

        @Parameter(names = {"--replace-numbers"}, description = "Replace numbers for <NUMBER>.", arity = 1, order = 80)
        Boolean replaceNumbers = null;

        @Parameter(names = {"--min"}, description = "Min length of each word", order = 90)
        long minTokenLength = -1;

        @Parameter(names = {"--max"}, description = "Max length of each word", order = 100)
        long maxTokenLength = -1;

        @Parameter(names = {"--stop-words"}, description = "File containing the set of stop-words to be removed.", order = 110)
        String stopWords = null;

        @Parameter(names = {"--multi-word-tokens"}, description = "File containing the set of multi-words tokens.", order = 120)
        String multiWordTokens = null;

        public CorpusMetadata getMetadata() {
            CorpusMetadataBuilder cmb = CorpusMetadataBuilder.newCorpusMetadata(corpusName, language);
            if (description != null) {
                cmb.desc(description);
            }

            if (encoding != null) {
                cmb.encoding(encoding);
            }

            if (applyStemmer >= 0) {
                cmb.applyStemmer(applyStemmer);
            }

            if (removeAccents != null) {
                cmb.removeAccents(removeAccents);
            }

            if (applyLowercase != null) {
                cmb.applyLowercase(applyLowercase);
            }

            if (replaceNumbers != null) {
                cmb.replaceNumbers(replaceNumbers);
            }

            if (minTokenLength > 0) {
                cmb.minTokenLength(minTokenLength);
            }

            if (maxTokenLength > 0) {
                cmb.maxTokenLength(maxTokenLength);
            }

            if (stopWords != null) {
                Set<String> stopWordsSet = getStringSetFromFile(stopWords,
                        "Problem reading --stop-words file.");
                cmb.stopWords(stopWordsSet);
            }

            if (multiWordTokens != null) {
                String transName = MultiWordsTransformer.class.getSimpleName();
                Set<String> multiWordTokensSet = getStringSetFromFile(multiWordTokens,
                        "Problem reading --multi-words-tokens file.");
                Map<String, Collection<String>> transformers = Collections.singletonMap(transName, multiWordTokensSet);
                cmb.transformers(transformers);
            }

            return cmb.build();
        }


        public Set<String> getStringSetFromFile(String file, String errorMessage) {
            try {
                Set<String> stopWordsSet = new BufferedReader(new FileReader(new File(file))).
                        lines().collect(Collectors.toSet());

                return stopWordsSet;
            } catch (FileNotFoundException e) {
                System.out.println(errorMessage);
                System.exit(3);
            }
            return null;
        }
    }
}
