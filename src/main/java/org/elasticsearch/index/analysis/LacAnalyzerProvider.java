package org.elasticsearch.index.analysis;

import com.thundax.analysis.config.Configuration;
import com.thundax.analysis.lucene.LacAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * 注册"LacAnalyzer"给Elasticsearch使用
 */
public class LacAnalyzerProvider extends AbstractIndexAnalyzerProvider<LacAnalyzer> {
    // 用于缓存构建好的"LacAnalyzer"实例,避免重复构建
    private final LacAnalyzer analyzer;

    public LacAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(name, settings);

        Configuration configuration = new Configuration(settings);

        analyzer = new LacAnalyzer(configuration);
    }

    // 便于在插件初始化中通过静态方式构造 LacAnalyzerProvider 实例。
    public static LacAnalyzerProvider getAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new LacAnalyzerProvider(indexSettings, env, name, settings);
    }

    /**
     * Elasticsearch在创建分析器时调用get(),返回构造好的分词器
     */
    @Override
    public LacAnalyzer get() {
        return this.analyzer;
    }

}
