package org.lambda3.indra.preprocessing;

import org.lambda3.indra.preprocessing.transform.MultiWordsTransformer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class MultiWordsTransformerTest {

    private String text = "price action is the movement of a security's price. " +
            "price action is encompassed in technical and chart pattern analysis, which attempt to find " +
            "order in the sometimes seemingly random movement of price. Swings (high and low), tests of " +
            "resistance and consolidation are some examples of price action.";

    @Test       
    public void emptyStringTest() {
        List<String> tokens = Arrays.asList("price action", "");
        new MultiWordsTransformer(tokens).transform(new StringBuilder(text));
    }

    @Test(timeOut = 20000)
    public void foundTokensTest() {
        List<String> tokens = Arrays.asList("price action", "random movement", "high and low");
        StringBuilder content = new StringBuilder(text);
        new MultiWordsTransformer(tokens).transform(content);
        String strContent = content.toString();

        for (String token : tokens) {
            Assert.assertFalse(strContent.contains(token));
            Assert.assertTrue(strContent.contains(token.replace(" ", MultiWordsTransformer.TOKEN_SEPARATOR)));
            Assert.assertEquals(text, strContent.replace(MultiWordsTransformer.TOKEN_SEPARATOR, " "));
        }
    }

    @Test(timeOut = 20000)
    public void nonExistingTokensTest() {
        List<String> tokens = Arrays.asList("frevo and samba", "financial crisis", "best of luck");
        StringBuilder content = new StringBuilder(text);
        new MultiWordsTransformer(tokens).transform(content);
        String strContent = content.toString();

        Assert.assertEquals(content.toString(), text);
    }
}
