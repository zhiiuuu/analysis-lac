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

/**
 * 自定义分词器类
 * 继承 Lucene 的 Tokenizer, 表示他是一个可插入 Lucene/ES 流水线的分词组件
 */
public final class LacTokenizer extends Tokenizer {
    /**
     * 自定义分词逻辑实现者，内部会读取字符流并返回 Lexeme(词元)
     */
    private final LacSegmenter segmenter;

    /**
     * 词元文本属性 如"你好"
     */
    private final CharTermAttribute termAtt;

    /**
     * 词元位移属性(如:开始是0,结束是2)
     */
    private final OffsetAttribute offsetAtt;

    /**
     * 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）(如:中文词,英文词,数字,标点等)
     */
    private final TypeAttribute typeAtt;

    /**
     * 记录最后一个词元的结束位置(Lucene 需要)
     */
    private int endPosition;

    /**
     * 如果跳过了某些字符(如过滤掉标点),这个值会帮你调整位置增量。
     */
    private int skippedPositions;

    /**
     * 表示两个词元之间的位置增量(通常为1,但可为0表示叠词)
     */
    private PositionIncrementAttribute posIncrAtt;

    /**
     * 构造函数
     */
    public LacTokenizer(Configuration configuration) {
        super();

        // 初始化各种属性
        // addAttribute(...)是Lucene标准写法,表示当前分词器处理哪些数据
        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);

        // segmenter 用于处理实际的分词逻辑，传入的是字符流 input（Lucene 自动提供）和配置。
        segmenter = new LacSegmenter(input, configuration);
    }

    /**
     * 核心逻辑
     */
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

    /**
     * 重置方法
     * 这个方法在 Lucene 每次开始分词前会自动调用，用于清空状态。
     */
    @Override
    public void reset() throws IOException {
        super.reset();
        segmenter.reset(input);
        skippedPositions = 0;
        endPosition = 0;
    }

    /**
     * 结束方法
     * 用于 Lucene 在整个分词过程完成时做收尾动作，更新最后偏移位置和位置增量。
     */
    @Override
    public void end() throws IOException {
        super.end();
        // set final offset
        int finalOffset = correctOffset(this.endPosition);
        offsetAtt.setOffset(finalOffset, finalOffset);
        posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
    }

    /**
     * 测试方法 需要启动paddlehub Configuration.DEFAULT_SERVICE_URL需要改成自己本地的地址
     */
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
