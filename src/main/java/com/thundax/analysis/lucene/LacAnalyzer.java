package com.thundax.analysis.lucene;

import com.thundax.analysis.config.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public final class LacAnalyzer extends Analyzer {

    private Configuration configuration;

    public LacAnalyzer() {
    }

    public LacAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
    }


    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new LacTokenizer(configuration);
        return new TokenStreamComponents(tokenizer);
    }

}
