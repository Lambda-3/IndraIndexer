package org.lambda3.indra.preprocessing;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class IndraPreProcessorTest {

    @Test
    public void checkFilesTest() {
        String inputDir = getClass().getClassLoader().getResource("it/input").getPath();
        List<File> files = new IndraPreProcessor().doCheckFiles(inputDir, ".*file$");

        String[] fNames = {"afile", "bfile", "cfile"};
        Assert.assertEquals(files.size(), fNames.length);
        for (int i = 0; i < files.size(); i++) {
            Assert.assertEquals(files.get(i).getName(), fNames[i]);
        }
    }
}
