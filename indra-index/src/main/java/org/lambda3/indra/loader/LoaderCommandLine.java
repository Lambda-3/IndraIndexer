package org.lambda3.indra.loader;

/*-
 * ==========================License-Start=============================
 * indra-index
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
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorUtil;
import org.lambda3.indra.AnalyzedTerm;
import org.lambda3.indra.composition.SumVectorComposer;
import org.lambda3.indra.core.annoy.AnnoyVectorSpaceFactory;
import org.lambda3.indra.core.lucene.LuceneVectorSpaceFactory;
import org.lambda3.indra.core.vs.VectorSpace;
import org.lambda3.indra.core.vs.VectorSpaceFactory;
import org.lambda3.indra.indexer.IndraIndexerCommandLine;
import org.lambda3.indra.loader.annoy.AnnoyIndraLoader;
import org.lambda3.indra.loader.lucene.LuceneIndraLoader;
import org.lambda3.indra.request.VectorRequest;
import org.lambda3.indra.util.RawSpaceModel;
import org.lambda3.indra.util.Vector;
import org.lambda3.indra.util.VectorIterator;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class LoaderCommandLine {

    private static final String LOADER_NAME = "Indra Loader v. %s\nindrashell ";

    public static RealVector getVector(VectorSpace vs, String term) {
        AnalyzedTerm at = new AnalyzedTerm(term, Collections.singletonList(term));
        Map<String, RealVector> vectors = vs.getVectors(Collections.singletonList(at), new SumVectorComposer());

        if (vectors.keySet().contains(term)) {
            return vectors.get(term);
        } else {
            return null;
        }
    }

    public static void main(String... args) {
        String version = IndraIndexerCommandLine.class.getPackage().getImplementationVersion();

        JCommander jc = new JCommander(new LoaderCommandLine.MainCommand());
        jc.setProgramName(String.format(LOADER_NAME, version));

        LoaderCommand loadCmd = new LoaderCommand();
        jc.addCommand(LoaderCommand.NAME, loadCmd);

        TestCommand testCmd = new TestCommand();
        jc.addCommand(TestCommand.NAME, testCmd);

        try {
            jc.parse(args);
        } catch (ParameterException e) {
            //e.printStackTrace();
            jc.usage();
            return;
        }

        String parsedCommand = jc.getParsedCommand();
        if (jc.getParsedCommand() == null) {
            jc.usage();
            return;
        }

        try {

            RawSpaceModel<? extends Vector> rsm;

            switch (parsedCommand) {
                case LoaderCommand.NAME:

                    rsm = new RawSpaceModel<>(loadCmd.inputModelDir);

                    IndraLoader loader;
                    System.out.println(String.format("Loading model=%s | lang=%s | corpus=%s...", rsm.modelMetadata.modelName,
                            rsm.modelMetadata.corpusMetadata.language, rsm.modelMetadata.corpusMetadata.corpusName));
                    if (rsm.modelMetadata.sparse)
                        loader = new LuceneIndraLoader(loadCmd.output, rsm.modelMetadata);
                    else
                        loader = new AnnoyIndraLoader(loadCmd.output, rsm.modelMetadata);

                    loader.load(rsm);
                    loader.close();
                    System.out.println("Done!");

                case TestCommand.NAME:
                    rsm = new RawSpaceModel(testCmd.rawDir);

                    VectorSpaceFactory factory;
                    if (rsm.modelMetadata.sparse)
                        factory = new LuceneVectorSpaceFactory(testCmd.indexDir);
                    else
                        factory = new AnnoyVectorSpaceFactory(testCmd.indexDir);

                    VectorRequest request = new VectorRequest().model(rsm.modelMetadata.modelName).
                            language(rsm.modelMetadata.corpusMetadata.language).
                            corpus(rsm.modelMetadata.corpusMetadata.corpusName);

                    VectorSpace vsm = factory.create(request);

                    VectorIterator<? extends Vector> iter = rsm.getVectorIterator();
                    int counter = 0;
                    while (iter.hasNext()) {

                        if (++counter % 10_000 == 0) {
                            System.out.println(String.format("Analysing term #%d out of %d...",
                                    counter, rsm.modelMetadata.vocabSize));
                        }

                        Vector v = iter.next();
                        RealVector indexedVector = getVector(vsm, v.term);
                        if (!indexedVector.equals(rsm.modelMetadata.sparse ? v.content : RealVectorUtil.loosePrecision(v.content))) {
                            throw new Error(String.format("Vectors are not equal for term '%s' - %s | %s", v.term,
                                    v.content, indexedVector));
                        }
                    }

                    System.out.println("Models are equivalents! :)");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Common parameters for all commands.
     */
    @Parameters(commandDescription = "See commands below.")
    private static final class MainCommand {
        @Parameter(names = "--help", help = true, description = "You know this...")
        boolean help;
    }

    @Parameters(commandDescription = "Generate Models.", separators = "=")
    private static class LoaderCommand {

        public static final String NAME = "load";

        @Parameter(names = {"-i", "--input"}, required = true, description = "Directory in which the generated DSM's files are placed.", order = 0)
        String inputModelDir;

        @Parameter(names = {"-o", "--output"}, required = true, description = "The output directory.", order = 4)
        String output;
    }

    @Parameters(commandDescription = "Test if a given model was correctly loaded.", separators = "=")
    private static class TestCommand {

        public static final String NAME = "test";

        @Parameter(names = {"-r", "--raw"}, required = true, description = "Directory in which the generated DSM's files are placed.", order = 0)
        String rawDir;

        @Parameter(names = {"-i", "--index"}, required = true, description = "The base directry in which indexed model is placed.", order = 4)
        File indexDir;
    }

}
