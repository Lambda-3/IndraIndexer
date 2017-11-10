package org.lambda3.indra.indexer.builder;

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

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;

import java.io.IOException;
import java.util.Properties;

public class ExplicitSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public ExplicitSemanticAnalysisBuilder(String outDir) throws IOException {
        super(outDir, NOT_APPLIED, NOT_APPLIED);
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public String getModelName() {
        return "ESA";
    }

    @Override
    public SemanticSpace getSemanticSpace() {
        try {
            return new ExplicitSemanticAnalysis();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void processPosRunningInformation(SemanticSpace sspace) {
        this.dimensions = sspace.getVectorLength();
    }
}
