package org.elasticsearch.index.analysis;

import com.thundax.analysis.config.Configuration;
import com.thundax.analysis.lucene.LacAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class LacAnalyzerProvider extends AbstractIndexAnalyzerProvider<LacAnalyzer> {

    private final LacAnalyzer analyzer;

    public LacAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(name, settings);

        Configuration configuration = new Configuration(settings);

        analyzer = new LacAnalyzer(configuration);
    }

    public static LacAnalyzerProvider getAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new LacAnalyzerProvider(indexSettings, env, name, settings);
    }

    @Override
    public LacAnalyzer get() {
        return this.analyzer;
    }

}
