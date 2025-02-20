package com.thundax.analysis.lucene;

import com.thundax.analysis.config.Configuration;
import com.thundax.analysis.core.LacSegmenter;
import com.thundax.analysis.core.Lexeme;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;

public final class LacTokenizer extends Tokenizer {

    private final LacSegmenter segmenter;

    /**
     * 词元文本属性
     */
    private final CharTermAttribute termAtt;

    /**
     * 词元位移属性
     */
    private final OffsetAttribute offsetAtt;

    /**
     * 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
     */
    private final TypeAttribute typeAtt;

    /**
     * 记录最后一个词元的结束位置
     */
    private int endPosition;

    private int skippedPositions;

    private PositionIncrementAttribute posIncrAtt;

    public LacTokenizer(Configuration configuration) {
        super();

        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);

        segmenter = new LacSegmenter(input, configuration);
    }

    @Override
    public boolean incrementToken() throws IOException {
        //清除所有的词元属性
        clearAttributes();
        skippedPositions = 0;

        Lexeme nextLexeme = segmenter.next();
        if (nextLexeme != null) {
            posIncrAtt.setPositionIncrement(skippedPositions + 1);

            //将Lexeme转成Attributes
            //设置词元文本
            termAtt.append(nextLexeme.getLexemeText());
            //设置词元长度
            termAtt.setLength(nextLexeme.getLength());
            //设置词元位移
            offsetAtt.setOffset(correctOffset(nextLexeme.getBeginPosition()), correctOffset(nextLexeme.getEndPosition()));

            //记录分词的最后位置
            endPosition = nextLexeme.getEndPosition();
            //记录词元分类
            typeAtt.setType(nextLexeme.getLexemeType());
            //返会true告知还有下个词元
            return true;
        }
        //返会false告知词元输出完毕
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        segmenter.reset(input);
        skippedPositions = 0;
        endPosition = 0;
    }

    @Override
    public void end() throws IOException {
        super.end();
        // set final offset
        int finalOffset = correctOffset(this.endPosition);
        offsetAtt.setOffset(finalOffset, finalOffset);
        posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration(Settings.EMPTY);
        LacTokenizer tokenizer = new LacTokenizer(configuration);
        tokenizer.setReader(new java.io.StringReader("你好，世界！"));
        tokenizer.reset();
        while (tokenizer.incrementToken()) {
            System.out.println(tokenizer.termAtt.toString());
        }
        tokenizer.end();
    }

}
