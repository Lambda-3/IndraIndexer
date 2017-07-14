package org.lambda3.indra.preprocessing;

import org.lambda3.indra.indexer.Document;

public class PreprocessorTest {

    private String content = "Eduardo Jorge Martins Alves Sobrinho (born October 26, 1949),[1] most known simply" +
            " as Eduardo Jorge, is a Brazilian public health physician and politician. He is most known for creating " +
            "(or co-creating) the federal laws on family planning, voluntary sterilization, production of generic " +
            "drugs, regulation of asbestos use, and linking budgetary resources for Sistema Único de Saúde.[2]";

    private Document doc = Document.simpleDocument(content);

    public void lowercaseTest() {
            
    }
}
