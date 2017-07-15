package org.lambda3.indra.preprocessing.lucene.filter;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberFilter extends TokenFilter {
    private static final String NUMBER_PLACEHOLDER = "<NUMBER>";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)(\\.|,)?(\\d+)?");

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final boolean all = true;

    public NumberFilter(TokenStream input) {
        super(input);

    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) return false;
        for (int i = 0; i < termAtt.length(); i++) {
            if (termAtt.buffer()[i] == '_') {
                return true;
            }
        }

        Matcher m = NUMBER_PATTERN.matcher(termAtt);
        m.reset();
        if (m.find()) {
            //replaceAll/replaceFirst will reset() this previous find.
            String transformed = all ? m.replaceAll(NUMBER_PLACEHOLDER) : m.replaceFirst(NUMBER_PLACEHOLDER);
            termAtt.setEmpty().append(transformed);
        }

        return true;
    }
}
