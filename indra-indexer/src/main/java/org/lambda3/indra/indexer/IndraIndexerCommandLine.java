package org.lambda3.indra.indexer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.lambda3.indra.corpus.*;
import org.lambda3.indra.indexer.builder.PredictiveModelBuilder;
import org.lambda3.indra.indexer.builder.SSpaceModelBuilder;
import org.lambda3.indra.indexer.builder.ModelBuilder;
import org.lambda3.indra.ModelMetadata;
import org.lambda3.indra.indexer.builder.ExplicitSemanticAnalysisBuilder;
import org.lambda3.indra.indexer.builder.LatentSemanticAnalysisBuilder;
import org.lambda3.indra.pp.transform.MultiWordsTransformer;
 import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class IndraIndexerCommandLine {

    private static final String Indexer_ID = "org.lambda3.indra.indexer.IndraIndexerCommandLine";

    public static void main(String... args) {
        //String version = IndraPreProcessor.class.getPackage().getImplementationVersion();

        MainCommand main = new MainCommand();
        JCommander jc = new JCommander(main);
        jc.setProgramName(Indexer_ID);
        IndexerCommand indexCmd = new IndexerCommand();
        jc.addCommand("indexer", indexCmd);



        try {
            jc.parse(args);
        } catch (ParameterException e) {
            e.printStackTrace();
            jc.usage();
        }

        if (jc.getParsedCommand() == null) {
            jc.usage();
        }


        try {
            ModelBuilder builder;
            // making sure all data will be flushed.
            Corpus corpus = new CorpusLoader(indexCmd.corpusFile).read(indexCmd.getCorpusMetadata());

            if (indexCmd.modelName.equalsIgnoreCase("ESA"))
                builder = new ExplicitSemanticAnalysisBuilder(indexCmd.getModelMetadata(), indexCmd.output);
            else if (indexCmd.modelName.equalsIgnoreCase("LSA"))
                builder = new LatentSemanticAnalysisBuilder(indexCmd.getModelMetadata(), indexCmd.output);
            else if (indexCmd.modelName.equalsIgnoreCase("GLOVE"))
                builder = PredictiveModelBuilder.createGloveModelBuilder(indexCmd.getModelMetadata(),indexCmd.output);
            else if (indexCmd.modelName.equalsIgnoreCase("W2V"))
                builder = PredictiveModelBuilder.createWord2VecModelBuilder(indexCmd.getModelMetadata(),indexCmd.output);
            else
                throw new IllegalStateException();

            builder.build(corpus);

        } catch (IOException e) {
            e.printStackTrace();
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


    @Parameters(commandDescription = "Generate Models.", separators = "=")
    private static class IndexerCommand {
        @Parameter(names = {"-m", "--model-name"}, required = true, description = "Input name of the model.", order = 0)
        String modelName;

        @Parameter(names = {"-c", "--corpus-file"}, required = true, description = "Input text corpus file.", order = 1)
        File corpusFile;

        @Parameter(names = {"-l", "--lang"}, required = true, description = "Corpus language.", order = 2)
        String language;

        @Parameter(names = {"-d", "--dimensions"}, required = true, description = "The number of dimentions.", order = 3)
        int numOfDimensions;

        @Parameter(names = {"-o", "--output"}, required = true, description = "Output directory.", order = 4)
        String output;

        @Parameter(names = {"-w", "--windows-size"}, description = "Window Size.", order = 10)
        int windowsSize = 5;

        @Parameter(names = {"-f", "--min-word-frequency"}, description = "Min word frequency.", order = 15)
        int minWordFrequency = 5;

        @Parameter(names = {"--sparse"}, description = "Is it sparse?.",arity = 1, order = 20)
        boolean sparse = true;

        @Parameter(names = {"--desc"}, description = "Corpus description.", order = 30)
        String description = null;

        @Parameter(names = {"--encoding"}, description = "File text encoding.", order = 40)
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
        File stopWords = null;

        @Parameter(names = {"--multi-word-tokens"}, description = "File containing the set of multi-words tokens.", order = 120)
        File multiWordTokens = null;


        public CorpusMetadata getCorpusMetadata() {
            CorpusMetadataBuilder cmb = CorpusMetadataBuilder.newCorpusMetadata(corpusFile.getName(), language);
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
                        "Problem reading --stop-words file. " + stopWords);
                cmb.stopWords(stopWordsSet);
            }

            if (multiWordTokens != null) {
                String transName = MultiWordsTransformer.class.getSimpleName();
                Set<String> multiWordTokensSet = getStringSetFromFile(multiWordTokens,
                        "Problem reading --multi-words-tokens file. " + minTokenLength);
                Map<String, Collection<String>> transformers = Collections.singletonMap(transName, multiWordTokensSet);
                cmb.transformers(transformers);
            }

            return cmb.build();
        }

        public Set<String> getStringSetFromFile(File file, String errorMessage) {
            try {
                Set<String> stopWordsSet = new BufferedReader(new FileReader(file)).
                        lines().collect(Collectors.toSet());

                return stopWordsSet;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println(errorMessage);
                System.exit(3);
            }
            return null;
        }

        public ModelMetadata getModelMetadata() {

            return new ModelMetadata(modelName, sparse, getCorpusMetadata(), getParams());

        }

        public Map<String, Object> getParams(){


            Map<String, Object> params = new HashMap<>();
            params.put(PredictiveModelBuilder.MIN_WORD_FREQUENCY,  minWordFrequency);
            params.put(PredictiveModelBuilder.WINDOW_SIZE,  windowsSize);
            params.put(PredictiveModelBuilder.VECTOR_SIZE,  numOfDimensions);
            return params;
        }








    }

}
