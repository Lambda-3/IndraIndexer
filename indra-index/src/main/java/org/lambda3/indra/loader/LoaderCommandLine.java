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
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.indexer.IndraIndexerCommandLine;
import org.lambda3.indra.loader.annoy.AnnoyIndraLoader;
import org.lambda3.indra.loader.lucene.LuceneIndraLoader;
import org.lambda3.indra.model.ModelMetadata;
import org.lambda3.indra.util.DenseVector;
import org.lambda3.indra.util.RawSpaceModel;
import org.lambda3.indra.util.SparseVector;
import org.lambda3.indra.util.Vector;

import java.awt.*;
import java.io.IOException;

public class LoaderCommandLine {

    private static final String LOADER_NAME = "Indra Loader v. %s";

    public static void main(String... args) {
        String version = IndraIndexerCommandLine.class.getPackage().getImplementationVersion();

        LoaderCommandLine.MainCommand main = new LoaderCommandLine.MainCommand();
        JCommander jc = new JCommander(main);
        jc.setProgramName(String.format(LOADER_NAME, version));
        LoaderCommandLine.IndexerCommand indexCmd = new LoaderCommandLine.IndexerCommand();
        jc.addCommand("loader", indexCmd);

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
            ModelMetadata metadata = MetadataIO.load(indexCmd.inputModelDir, ModelMetadata.class);

            IndraLoader loader;
            if (metadata.sparse)
                loader = new LuceneIndraLoader(indexCmd.output, metadata);
            else
                loader = new AnnoyIndraLoader(indexCmd.output, metadata);

            RawSpaceModel<? extends Vector> rsm;
            if (metadata.sparse) {
                rsm = new RawSpaceModel<>(indexCmd.inputModelDir, metadata, SparseVector.class);
            } else {
                rsm = new RawSpaceModel<>(indexCmd.inputModelDir, metadata, DenseVector.class);
            }

            loader.load(rsm);
            loader.close();

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
    private static class IndexerCommand {
        @Parameter(names = {"-i", "--input"}, required = true, description = "Directory in which the generated DSM's files are placed.", order = 0)
        String inputModelDir;

        @Parameter(names = {"-o", "--output"}, required = true, description = "The output directory.", order = 4)
        String output;
    }

}
