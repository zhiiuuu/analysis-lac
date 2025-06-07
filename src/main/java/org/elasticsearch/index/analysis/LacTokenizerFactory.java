package org.elasticsearch.index.analysis;
// 这个包是 Elasticsearch 插件卡法中常用的包,表示它属于分析器(Analysis)模块

import com.thundax.analysis.config.Configuration;
import com.thundax.analysis.lucene.LacTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * 分词器的工厂类
 * 1.接收Elasticsearch索引环境与插件设置
 * 2.基于这些设置构造一个Tokenizer
 * 3.提供工厂方法让ES在分析时可以动态创建Tokenizer实例
 */
public class LacTokenizerFactory extends AbstractTokenizerFactory {
    /**
     * "lac"分词器的配置信息
     */
    private final Configuration configuration;

    /**
     * 构造方法
     * 通过Settings创建了 Configuration 实例,给分词器使用
     *
     * @param indexSettings 索引相关的设置(比如分片)
     * @param name          名称
     * @param settings      用户在elasticsearch.yml的配置或index mapping中设置的配置
     */
    public LacTokenizerFactory(IndexSettings indexSettings, String name, Settings settings) {
        super(indexSettings, settings, name);
        configuration = new Configuration(settings);
    }

    /**
     * 提供给elasticsearch插件自动发现分词器的入口方法
     * AnalysisPlugin中注册时会嗲用这个方法
     *
     * @param indexSettings 索引相关的设置
     * @param environment   ES实际运行的全局环境,比如路径
     * @param name          名称
     * @param settings      当前索引的配置
     * @return LacTokenizerFactory实例
     */
    public static LacTokenizerFactory getTokenizerFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        return new LacTokenizerFactory(indexSettings, name, settings);
    }

    /**
     * 这个方法在 Elasticsearch 建立索引或分析文本时调用
     * 返回真正干活的 LacTokenizer,他使用前面创建的Configuration
     */
    @Override
    public Tokenizer create() {
        return new LacTokenizer(configuration);
    }
}
