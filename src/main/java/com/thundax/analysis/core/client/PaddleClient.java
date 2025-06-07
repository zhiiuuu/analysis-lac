package com.thundax.analysis.core.client;

import com.thundax.analysis.core.Lexeme;
import com.thundax.analysis.core.client.protocol.Jsonable;
import com.thundax.analysis.core.client.protocol.LacRequestParam;
import com.thundax.analysis.core.client.protocol.LacResponseParam;
import com.thundax.analysis.core.client.protocol.LacResponseParamWrapper;
import okhttp3.*;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.json.JsonXContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thundax.analysis.config.Configuration.DEFAULT_SERVICE_URL;

public class PaddleClient {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MIME_APPLICATION_JSON = "application/json";

    // curl -X 'POST' 'http://127.0.0.1:8866/predict/lac' \
    //        -H 'accept: */*' \
    //        -H 'Content-Type: application/json' \
    //        -d '{"text": ["今天是个好日子", "天气预报说：今天要下雨"]}'

    /**
     * 将文本请求发送到 Paddle-LAC 服务，返回分词后的 Lexeme 列表。
     *
     * @param serviceUrl
     * @param text
     * @return
     * @throws IOException
     */
    public static List<Lexeme> request(String serviceUrl, String text) throws IOException {
        // 把文本构造成 {"text": ["这是一个测试问题"]} 格式。转为 JSON 字符串。
        RequestBody requestBody = RequestBody.create(toJson(new LacRequestParam(text).toMap()), MediaType.parse(MIME_APPLICATION_JSON));

        // 使用 OkHttp 同步请求 LAC 服务器。
        Request request = new Request.Builder()
                .url(serviceUrl)
                .addHeader(CONTENT_TYPE, MIME_APPLICATION_JSON)
                .post(requestBody)
                .build();

        ResponseBody responseBody = new OkHttpClient().newCall(request).execute().body();
        if (responseBody == null) {
            throw new IOException("could not send request, url: " + serviceUrl);
        }

        // 将 JSON 响应反序列化为 Java 对象
        String responseBodyString = responseBody.string();
        LacResponseParamWrapper wrapper = fromJson(responseBodyString, new LacResponseParamWrapper());
        if (!LacResponseParamWrapper.isSuccess(wrapper) || wrapper.getResults() == null || wrapper.getResults().isEmpty()) {
            throw new IOException("bad response, body: " + responseBodyString);
        }

        LacResponseParam responseParam = wrapper.getResults().get(0);
        if (responseParam == null || !responseParam.validate()) {
            throw new IOException("bad response, body: " + responseBodyString);
        }

        /**
         * 将每个词 + tag 转为 Lexeme：
         * offset=0：全文相对位置（默认）
         * begin=startPosition：词在输入文本中的位置
         * length=word.length()：注意是字符长度（不一定等于字节）
         * lexemeType=tag：LAC 给出的词性标注，如 n、v、PER 等
         * lexemeText=word：词本身
         */
        int size = responseParam.getTag().size();

        List<Lexeme> lexemeList = new ArrayList<>(size);

        int startPosition = 0;
        for (int idx = 0; idx < size; idx++) {
            String word = responseParam.getWord().get(idx);
            if (word == null || word.isEmpty()) {
                continue;
            }
            int lexemeLength = word.length();

            String tag = responseParam.getTag().get(idx);
            lexemeList.add(new Lexeme(0, startPosition, lexemeLength, tag, word));
            startPosition += lexemeLength;
        }

        return lexemeList;
    }

    /**
     * 使用 XContentHelper.convertToMap() 将 JSON 字符串转为 Map，再交由 jsonable.fromMap() 填充字段。
     *
     * @param jsonString
     * @param jsonable
     * @param <T>
     * @return
     */
    public static <T extends Jsonable> T fromJson(String jsonString, T jsonable) {
        Map<String, Object> map = XContentHelper.convertToMap(JsonXContent.jsonXContent, jsonString, false);
        jsonable.fromMap(map);
        return jsonable;
    }

    /**
     * 将 Java Map 转为 JSON 字符串，使用的是 Elasticsearch 的 XContentBuilder。
     *
     * @param map
     * @return
     * @throws IOException
     */
    public static String toJson(Map<String, ?> map) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().map(map);
        BytesReference bytes = BytesReference.bytes(builder);
        return bytes.utf8ToString();
    }

    public static void main(String[] args) throws Exception {
        List<Lexeme> lexemeList = request(DEFAULT_SERVICE_URL, "这是一个测试问题");
        System.out.println(lexemeList);
    }
}
