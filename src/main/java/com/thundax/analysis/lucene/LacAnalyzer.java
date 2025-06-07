package com.thundax.analysis.lucene;

import com.thundax.analysis.config.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * 一个基于Paddle-LAC的Lucene分析器(LacAnalyzer),用于封装分词器"Tokenizer"
 */
public final class LacAnalyzer extends Analyzer {

    private Configuration configuration;

    public LacAnalyzer() {
    }

    public LacAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
    }


    /**
     * 构建一个 Tokenizer（也就是 LacTokenizer），传入配置；
     * 封装成 TokenStreamComponents 并返回。
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new LacTokenizer(configuration);
        return new TokenStreamComponents(tokenizer);
    }

}
