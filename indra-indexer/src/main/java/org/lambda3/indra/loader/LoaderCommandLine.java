package org.lambda3.indra.loader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.lambda3.indra.MetadataIO;
import org.lambda3.indra.indexer.IndraIndexerCommandLine;
import org.lambda3.indra.loader.annoy.AnnoyIndraLoader;
import org.lambda3.indra.loader.lucene.LuceneIndraLoader;
import org.lambda3.indra.loader.mongo.MongoIndraLoader;
import org.lambda3.indra.model.ModelMetadata;

import java.io.IOException;

public class LoaderCommandLine {

    private static final String INDEXER_NAME = "Indra Loader v.%s";

    public static void main(String... args) {
        String version = IndraIndexerCommandLine.class.getPackage().getImplementationVersion();

        LoaderCommandLine.MainCommand main = new LoaderCommandLine.MainCommand();
        JCommander jc = new JCommander(main);
        jc.setProgramName(String.format(INDEXER_NAME, version));
        LoaderCommandLine.IndexerCommand indexCmd = new LoaderCommandLine.IndexerCommand();
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
            ModelMetadata metadata = MetadataIO.load(indexCmd.inputModelDir, ModelMetadata.class);

            IndraLoader loader;
            if (indexCmd.targetPlatform.equalsIgnoreCase("LUCENE"))
                loader = new LuceneIndraLoader(indexCmd.output, metadata);
            else if (indexCmd.targetPlatform.equalsIgnoreCase("ANNOY"))
                loader = new AnnoyIndraLoader(indexCmd.output, metadata);
            else if (indexCmd.targetPlatform.equalsIgnoreCase("MONGO"))
                loader = new MongoIndraLoader();
            else
                throw new IllegalStateException(String.format("Model '%s' is not supported. Please, choose one " +
                        "of the following: LUCENE, ANNOY or MONGO.", indexCmd.targetPlatform));

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
        @Parameter(names = {"-i", "--input"}, required = true, description = "Input directory.", order = 0)
        String inputModelDir;

        @Parameter(names = {"-t", "--target"}, required = true, description = ".", order = 1)
        String targetPlatform;

        @Parameter(names = {"-o", "--output"}, required = true, description = "The output directory.", order = 4)
        String output;
    }

}
