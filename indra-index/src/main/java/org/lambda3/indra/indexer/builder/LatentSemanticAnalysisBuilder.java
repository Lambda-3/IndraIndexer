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
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;

import java.io.IOException;
import java.util.Properties;

public class LatentSemanticAnalysisBuilder extends SSpaceModelBuilder {

    public LatentSemanticAnalysisBuilder(String outDir, int dimensions) {
        super(outDir, dimensions, NOT_APPLIED);
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public String getModelName() {
        return "LSA";
    }

    @Override
    public SemanticSpace getSemanticSpace() {
        try {
            return new LatentSemanticAnalysis(this.dimensions, true);
        } catch (IOException e) {
            //TODO trhow exception here
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processPosRunningInformation(SemanticSpace sspace) {
        //nothing to do.
    }
}
