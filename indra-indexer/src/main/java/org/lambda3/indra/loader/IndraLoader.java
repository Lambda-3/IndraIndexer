package org.lambda3.indra.loader;

import java.io.Closeable;

public interface IndraLoader extends Closeable {

    void load(RawSpaceModel rsm);
}
