# LAC Analysis for Elasticsearch and OpenSearch

The LAC Analysis plugin integrates Lucene LAC analyzer. It supports major versions of Elasticsearch and OpenSearch.

The plugin comprises analyzer: lac, and tokenizer: lac.

## 🛠️ How to Install

### Install LAC Service by PaddleHub**

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

Config file `LacAnalyzer.cfg.xml` can be located at `{conf}/analysis-lac/config/IKAnalyzer.cfg.xml`
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
