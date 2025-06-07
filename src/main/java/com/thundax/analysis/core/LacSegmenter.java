package com.thundax.analysis.core;

import com.thundax.analysis.config.Configuration;
import com.thundax.analysis.core.client.PaddleClient;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * 自定义分词器逻辑
 * 负责把文本读入+调用远程Paddle服务分词+输出一个一个Lexeme(词元)对象
 * 不可继承的最终类
 */
public final class LacSegmenter {
    // 每次从 Reader 中读取字符的最大块大小（8192 字符），为了防止读太多爆内存。
    private static final int CHUNK_SIZE = 8192;
    // Paddle 服务的地址，从配置中获取，用来发送 HTTP 请求调用 Paddle 分词。
    private final String serviceUrl;
    // 用于接收 Lucene 输入的字符流，这就是待分词的原始文本。
    private Reader reader;
    // 存放分词结果（词元列表），来自远程分词服务（Paddle 返回结果）。
    private List<Lexeme> lexemeList = null;

    /**
     * 将配置中的远程 Paddle URL 保存下来；
     * 并初始化读取器和词元列表。
     *
     * @param reader
     * @param configuration
     */
    public LacSegmenter(Reader reader, Configuration configuration) {
        // 获取配置的分词服务地址
        this.serviceUrl = configuration.getServiceUrl();
        // 初始化 reader 和清空词元列表
        this.init(reader);
    }

    /**
     * 初始化
     */
    private void init(Reader reader) {
        // 设置新的输入文本
        this.reader = reader;
        // 清空已有的词元结果列表（方便重用对象）。
        this.lexemeList = null;
    }

    /**
     * 把 Reader 中的字符流一次性读完，拼成完整的字符串。
     *
     * @param reader
     * @return
     */
    private String readString(Reader reader) {
        StringBuilder buffer = new StringBuilder();

        try {
            char[] chunk = new char[CHUNK_SIZE];
            int readSize;
            while ((readSize = reader.read(chunk, 0, chunk.length)) != -1) {
                buffer.append(chunk, 0, readSize); // 不断读取 chunk 加入缓存
            }

        } catch (IOException e) {
            System.out.println(e.getMessage()); // 简单异常打印（建议改为日志）
        }
        return buffer.toString(); // 返回完整文本字符串
    }

    /**
     * 分词，获取下一个词元
     * 这也是为什么你在 LacTokenizer.incrementToken() 里每次只处理一个 Lexeme 的原因。
     *
     * @return Lexeme 词元对象
     * @throws IOException IOException
     */
    public synchronized Lexeme next() throws IOException {
        // 首次调用时会读取文本，发送到 Paddle 服务。
        if (this.lexemeList == null) {
            String text = this.readString(this.reader);
            if (text.isEmpty()) {
                return null;
            }
            // PaddleClient.request() 会返回一个 List<Lexeme>，表示分好的词。
            this.lexemeList = PaddleClient.request(this.serviceUrl, text);
        }
        // 每次调用 next() 就从中取一个词返回。
        return this.lexemeList.isEmpty() ? null : this.lexemeList.remove(0);
    }

    /**
     * 重置分词器到初始状态
     *
     * @param reader reader
     */
    public synchronized void reset(Reader reader) {
        // 当分词器重用时（Lucene 会重用分词器对象），这个方法用于重新设置输入流，并清空旧状态。
        this.init(reader);
    }
}