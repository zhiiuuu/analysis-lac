package com.thundax.analysis.core;

import com.thundax.analysis.config.Configuration;
import com.thundax.analysis.core.client.PaddleClient;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public final class LacSegmenter {

    private static final int CHUNK_SIZE = 8192;

    private final String serviceUrl;

    private Reader reader;
    private List<Lexeme> lexemeList = null;

    public LacSegmenter(Reader reader, Configuration configuration) {
        this.serviceUrl = configuration.getServiceUrl();
        this.init(reader);
    }

    /**
     * 初始化
     */
    private void init(Reader reader) {
        this.reader = reader;
        this.lexemeList = null;
    }

    private String readString(Reader reader) {
        StringBuilder buffer = new StringBuilder();

        try {
            char[] chunk = new char[CHUNK_SIZE];
            int readSize;
            while ((readSize = reader.read(chunk, 0, chunk.length)) != -1) {
                buffer.append(chunk, 0, readSize);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        org.apache.lucene.analysis.Tokenizer t;
        return buffer.toString();
    }

    /**
     * 分词，获取下一个词元
     *
     * @return Lexeme 词元对象
     * @throws IOException IOException
     */
    public synchronized Lexeme next() throws IOException {
        if (this.lexemeList == null) {
            String text = this.readString(this.reader);
            if (text.isEmpty()) {
                return null;
            }
            this.lexemeList = PaddleClient.request(this.serviceUrl, text);
        }
        return this.lexemeList.isEmpty() ? null : this.lexemeList.remove(0);
    }

    /**
     * 重置分词器到初始状态
     *
     * @param reader reader
     */
    public synchronized void reset(Reader reader) {
        this.init(reader);
    }

}
