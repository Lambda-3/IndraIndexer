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
