package org.lambda3.indra.loader.w2v;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.lambda3.indra.loader.DenseVectorEntry;
import org.lambda3.indra.loader.DenseVectorGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public final class Word2VecGenerator implements DenseVectorGenerator {

    private Word2Vec model;

    private Word2VecGenerator(File modeFile) {
        this.model = WordVectorSerializer.readWord2VecModel(modeFile);
    }

    @Override
    public Iterator<Map.Entry<String, double[]>> iterator() {
        return new VectorsIterator(model);
    }

    public static void buildFromFile(File inputFile, File outputFile) throws IOException {
        //TODO: Where does all these Parametrizations comes from?

        SentenceIterator iterator = new LineSentenceIterator(inputFile);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        Word2Vec w2v = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iterator)
                .tokenizerFactory(t)
                .build();

        w2v.fit();

        WordVectorSerializer.writeWord2VecModel(w2v, outputFile);
    }

    public static Word2VecGenerator readFromFile(File modeFile) {
        return new Word2VecGenerator(modeFile);
    }

    private class VectorsIterator implements Iterator<Map.Entry<String, double[]>> {
        Word2Vec model;
        Iterator<VocabWord> vocabWordIterator;

        VectorsIterator(Word2Vec model) {
            this.model = Objects.requireNonNull(model);
            this.vocabWordIterator = model.vocab().tokens().iterator();
        }

        @Override
        public boolean hasNext() {
            return vocabWordIterator.hasNext();
        }

        @Override
        public Map.Entry<String, double[]> next() {
            VocabWord vocabWord = vocabWordIterator.next();
            double[] vector = model.getWordVector(vocabWord.getWord());
            return new DenseVectorEntry(vocabWord.getWord(), vector);
        }
    }
}
