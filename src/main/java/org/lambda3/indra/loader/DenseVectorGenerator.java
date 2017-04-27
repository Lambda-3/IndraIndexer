package org.lambda3.indra.loader;

import java.util.Iterator;
import java.util.Map;

public interface DenseVectorGenerator {

    Iterator<Map.Entry<String, double[]>> iterator();

}
