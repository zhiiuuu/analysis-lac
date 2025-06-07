package org.elasticsearch.plugin.analysis.lac;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.LacAnalyzerProvider;
import org.elasticsearch.index.analysis.LacTokenizerFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件入口
 * implements AnalysisPlugin说明这是一个分词器类插件
 */
public class AnalysisLacPlugin extends Plugin implements AnalysisPlugin {
    /**
     * 以下"lac" 在es设置时这么使用 详情见.md
     * curl -XPOST http://localhost:9200/index/_mapping -H 'Content-Type:application/json' -d'
     * {
     *     "properties": {
     *         "content": {
     *             "type": "text",
     *             "analyzer": "lac",
     *             "search_analyzer": "lac"
     *         }
     *     }
     * }'
     */
    public static String PLUGIN_NAME = "analysis-lac";

    // 分词器
    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();

        extra.put("lac", LacTokenizerFactory::getTokenizerFactory);

        return extra;
    }

    // 分析器
    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> extra = new HashMap<>();

        extra.put("lac", LacAnalyzerProvider::getAnalyzerProvider);

        return extra;
    }

}
