package org.lambda3.indra.pp;

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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.corpus.DocumentGenerator;
import org.lambda3.indra.pp.transform.MultiWordsTransformer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class IndraPreProcessorCommandLine {

    public static void main(String[] args) {
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
        } catch (ParameterException e) {
            e.printStackTrace();
            jc.usage();
        }

        if (jc.getParsedCommand() == null) {
            jc.usage();
        }

        switch (jc.getParsedCommand()) {
            case PreProcessCommand.CMD:
                new IndraPreProcessor().doPreProcess(ppCmd.getMetadata(), ppCmd.corpusFiles,
                        ppCmd.patternRegex, ppCmd.fileType, ppCmd.contentType, ppCmd.outputDir);
                break;
            case CheckFilesCommand.CMD:
                List<File> files = new IndraPreProcessor().doCheckFiles(checkCmd.corpusFiles, checkCmd.patternRegex);
                files.stream().forEach(System.out::println);
            default:
                System.out.println("invalid command");
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
        String corpusFiles;

        @Parameter(names = {"-r", "--regex"}, description = "Regex to filter files into the directories.", order = 1)
        String patternRegex;
    }

    @Parameters(commandDescription = "Pre process text corpora.", separators = "=")
    private static class PreProcessCommand {
        static final String CMD = "pp";

        @Parameter(names = {"-f", "--files"}, required = true, description = "Input text corpus files or directories.", order = 0)
        String corpusFiles;

        @Parameter(names = {"-o", "--output"}, required = true, description = "The output directory.", order = 1)
        File outputDir;

        @Parameter(names = {"-ft", "--file-type"}, required = true, description = "File type (wiki or text)", order = 2)
        DocumentGenerator.FileType fileType;

        @Parameter(names = {"-ct", "--contentType"}, required = true, description = "Content type (line or file)", order = 3)
        DocumentGenerator.ContentType contentType;

        @Parameter(names = {"-n", "--name"}, required = true, description = "Corpus name.", order = 4)
        String corpusName;

        @Parameter(names = {"-l", "--lang"}, required = true, description = "Corpus language.", order = 5)
        String language;

        @Parameter(names = {"-r", "--regex"}, description = "Regex to filter files into the directories.", order = 6)
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
        File stopWords = null;

        @Parameter(names = {"--multi-word-tokens"}, description = "File containing the set of multi-words tokens.", order = 120)
        File multiWordTokens = null;

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
    }
}
