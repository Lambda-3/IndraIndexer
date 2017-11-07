package org.lambda3.indra.loader;

import java.util.HashMap;
import java.util.Map;

public class SparseVector extends Vector<Map<Integer, Double>> {

    public SparseVector(String content) {
        super(content.split("\t"));
    }

    @Override
    public Map<Integer, Double> digestContent(String content) {
        Map<Integer, Double> map = new HashMap<>();

        String[] parts = content.split(" ");
        for (String part : parts) {
            String[] np = part.split(":");

            map.put(Integer.parseInt(np[0]), Double.parseDouble(np[1]));
        }

        return map;
    }
}
