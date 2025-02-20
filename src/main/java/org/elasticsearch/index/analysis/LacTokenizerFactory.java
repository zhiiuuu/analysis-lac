package org.elasticsearch.index.analysis;

import com.thundax.analysis.config.Configuration;
import com.thundax.analysis.lucene.LacTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class LacTokenizerFactory extends AbstractTokenizerFactory {

    private final Configuration configuration;

    public LacTokenizerFactory(IndexSettings indexSettings, String name, Settings settings) {
        super(indexSettings, settings, name);
        configuration = new Configuration(settings);
    }

    public static LacTokenizerFactory getTokenizerFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        return new LacTokenizerFactory(indexSettings, name, settings);
    }

    @Override
    public Tokenizer create() {
        return new LacTokenizer(configuration);
    }
}
