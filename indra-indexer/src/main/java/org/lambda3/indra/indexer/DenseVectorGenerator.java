package org.lambda3.indra.indexer;

import java.util.Iterator;
import java.util.Map;

public interface DenseVectorGenerator {

    Iterator<Map.Entry<String, double[]>> iterator();

}
