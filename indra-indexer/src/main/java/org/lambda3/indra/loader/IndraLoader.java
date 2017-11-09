package org.lambda3.indra.loader;

import java.io.Closeable;
import java.io.FileNotFoundException;

public interface IndraLoader<V extends Vector> extends Closeable {

    void load(RawSpaceModel<V> rsm) throws FileNotFoundException;
}
