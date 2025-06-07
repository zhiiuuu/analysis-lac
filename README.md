# LAC Analysis for Elasticsearch and OpenSearch

The LAC Analysis plugin integrates Lucene LAC analyzer. It supports major versions of Elasticsearch and OpenSearch.

The plugin comprises analyzer: lac, and tokenizer: lac.

## 🛠️ How to Install

### Install LAC Service by PaddleHub

> See [PaddleHub](https://www.paddlepaddle.org.cn/hubdetail?name=lac&en_category=LexicalAnalysis)

#### Create python environment

```bash
conda create -n paddlehub python=3.8

conda activate paddlehub
```

#### Install PaddleNLP

```bash
pip install paddlepaddle paddlehub
```

#### Start LAC Serving

```bash
hub serving start --modules lac --port 8866 --use_multiprocess --workers 8
```

#### Test
```bash
curl -X 'POST' 'http://127.0.0.1:8866/predict/lac' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{
    "text": [
        "今天是个好日子", "天气预报说：今天要下雨"
    ]
}'
```

### Install Analysis Plugin

#### For Elasticsearch:

```bash
bin/elasticsearch-plugin install https://github.com/thundax-lyp/analysis-lac/releases/download/8.12.2/elasticsearch-analysis-lac-8.12.2.jar
```

#### For OpenSearch:

```bash
bin/opensearch-plugin install https://github.com/thundax-lyp/analysis-lac/releases/download/8.12.2/elasticsearch-analysis-lac-8.12.2.jar
```

### ⚠️ **Tip**

Make sure to replace the version number with the one that matches your Elasticsearch or OpenSearch version.

## Getting Started

> Step 1. create an index

```bash
curl -XPUT http://localhost:9200/index
```

> Step 2. create a mapping

```bash
curl -XPOST http://localhost:9200/index/_mapping -H 'Content-Type:application/json' -d'
{
    "properties": {
        "content": {
            "type": "text",
            "analyzer": "lac",
            "search_analyzer": "lac"
        }
    }
}'
```

> Step 3. index some docs

```bash
curl -XPOST http://localhost:9200/index/_create/1 \
-H 'Content-Type:application/json' \
-d'{
    "content":"站一个制高点看上海,上海的弄堂是壮观的景象。"
}'
```

## Configuration

Config file `LacAnalyzer.cfg.xml` can be located at `{conf}/analysis-lac/config/LacAnalyzer.cfg.xml`
or `{plugins}/elasticsearch-analysis-lac-*/config/LacAnalyzer.cfg.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
    <entry key="service_url">http://127.0.0.1:8866/predict/lac/</entry>
</properties>
```

## Thanks

⚠️ **We borrow lots of codes from [IK Analysis](https://github.com/infinilabs/analysis-ik)** ⚠️

## 主入口 org.elasticsearch.plugin.analysis.lac.AnalysisLacPlugin
## Paddle-LAC Elasticsearch 分词插件整体结构图
```plaintext
┌──────────────────────────────┐
│ Elasticsearch 插件启动流程   │
└────────────┬────────────────┘
             │
             ▼
┌──────────────────────────────────────────────┐
│ AnalysisPlugin 实现类 (LacAnalysisPlugin)     │
│   - 提供 tokenizer/analyzer/tokenFilter 等注册 │
└────────────┬─────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────┐
│ LacTokenizerFactory implements TokenizerFactory│
│   - 由 Elasticsearch 注册 tokenizer 时调用    │
│   - 创建 LacTokenizer                         │
└────────────┬─────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────┐
│ LacAnalyzerProvider extends AbstractIndexAnalyzerProvider │
│   - 注册自定义 Analyzer 提供类                        │
│   - 调用 new LacAnalyzer(...)                         │
└────────────┬────────────────────────────────┘
             │
             ▼
┌───────────────────────┐
│ LacAnalyzer extends Analyzer │
│   - Lucene 自定义分析器       │
│   - 调用 createComponents     │
│   - 返回 TokenStreamComponents│
└────────────┬────────────┘
             │
             ▼
┌──────────────────────────────┐
│ LacTokenizer extends Tokenizer│
│   - 分词器核心实现            │
│   - 调用 LacSegmenter.next() │
│   - 生成 Token                │
└────────────┬────────────────┘
             │
             ▼
┌──────────────────────────────┐
│ LacSegmenter                 │
│   - 读取 Reader 输入全文      │
│   - 调用 PaddleClient        │
│   - 返回 List<Lexeme>        │
└────────────┬────────────────┘
             │
             ▼
┌────────────────────────────────────┐
│ PaddleClient                       │
│   - 构造 HTTP 请求调用 Paddle-LAC  │
│   - 返回词列表 List<Lexeme>        │
└────────────────────────────────────┘

```

## 关键类说明
| 类名                    | 职责说明                              |
| --------------------- | --------------------------------- |
| `LacAnalysisPlugin`   | 插件主类，注册 tokenizer/analyzer        |
| `LacAnalyzerProvider` | 分析器提供者，用于创建 `LacAnalyzer`         |
| `LacAnalyzer`         | 自定义 Lucene 分析器，调用 `LacTokenizer`  |
| `LacTokenizerFactory` | Lucene 分词器工厂（用于 Elasticsearch 注册） |
| `LacTokenizer`        | Lucene 分词器实现，调用 `LacSegmenter`    |
| `LacSegmenter`        | 封装分词逻辑，读取文本并调用 `PaddleClient`     |
| `PaddleClient`        | 发送 HTTP 请求到本地 Paddle-LAC 服务       |
| `Lexeme`              | 分词结果模型，包含 word、pos、offset 等信息     |

## 请求流程举例
例如，用户提交文本 “今天下雨了”：
1.LacTokenizerFactory 创建 LacTokenizer
2.LacTokenizer 构造 LacSegmenter
3.LacSegmenter 读取文本调用 PaddleClient
4.PaddleClient 发出 HTTP 请求：POST http://localhost:8866/predict/lac
5.得到词组及词性，如：
```JSON
{
"word": ["今天", "下雨", "了"],
"tag": ["TIME", "VERB", "ASPECT"]
}
```
6.封装为 Lexeme 对象供 Lucene 分词器迭代处理
7.Lucene 构建 Token 流 → 索引 or 查询流程