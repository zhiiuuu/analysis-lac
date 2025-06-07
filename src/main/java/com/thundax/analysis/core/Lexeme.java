package com.thundax.analysis.core;

/**
 * 表示一个词元,包含文本/起止位置/类型
 */
public class Lexeme implements Comparable<Lexeme> {

    /**
     * 词元的起始位移，词元的相对起始位置，词元的长度
     */
    private int offset;
    private int begin;
    private int length;

    /**
     * 词元文本,如"世界"
     */
    private String lexemeText;
    /**
     * 词元类型(如"n"表示名词,"v"表示动词等)
     */
    private String lexemeType;

    /**
     * 初始化词元
     */
    public Lexeme(int offset, int begin, int length, String lexemeType, String lexemeText) {
        // 校验词元长度 >= 0,否则抛异常
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        this.offset = offset;
        this.begin = begin;
        this.length = length;
        this.lexemeText = lexemeText;
        this.lexemeType = lexemeType;
    }

    /**
     * 判断词元相等算法
     * 起始位置偏移、起始位置、终止位置相同
     * 比较两个词元是否表示同一个文本片段（仅比较位置与长度，不比较文本内容和类型）。
     *
     * @param o object to compare
     * @return boolean indicating
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        // Java16+de模式匹配(Pattern Matching)语法 等同于 (Lexeme other = (Lexeme) o;)
        if (o instanceof Lexeme other) {
            return this.offset == other.getOffset()
                    && this.begin == other.getBegin()
                    && this.length == other.getLength();
        } else {
            return false;
        }
    }

    /**
     * 词元哈希编码算法
     * 自定义 hash 生成逻辑，用于 HashMap 等容器中区分词元。
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int absBegin = getBeginPosition();
        int absEnd = getEndPosition();
        // 质数乘法
        return (absBegin * 37) + (absEnd * 31) + ((absBegin * absEnd) % getLength()) * 11;
    }

    /**
     * 词元在排序集合中的比较算法
     * 1.先比 begin：靠前的排前面
     * 2.再比 length：长的排前面
     * 3.最终影响分词重叠处理、合并等行为
     *
     * @param other the other
     * @return equals
     */
    @Override
    public int compareTo(Lexeme other) {
        //起始位置优先
        if (this.begin < other.getBegin()) {
            return -1;

        } else if (this.begin == other.getBegin()) {
            //词元长度优先
            if (this.length > other.getLength()) {
                return -1;
            } else if (this.length == other.getLength()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getBegin() {
        return begin;
    }

    /**
     * 获取词元在文本中的起始位置
     *
     * @return int
     */
    public int getBeginPosition() {
        return offset + begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * 获取词元在文本中的结束位置
     *
     * @return int
     */
    public int getEndPosition() {
        return offset + begin + length;
    }

    /**
     * 获取词元的字符长度
     *
     * @return int
     */
    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        if (this.length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
    }

    /**
     * 获取词元的文本内容
     *
     * @return String
     */
    public String getLexemeText() {
        if (lexemeText == null) {
            return "";
        }
        return lexemeText;
    }

    public void setLexemeText(String lexemeText) {
        if (lexemeText == null) {
            this.lexemeText = "";
            this.length = 0;
        } else {
            this.lexemeText = lexemeText;
            this.length = lexemeText.length();
        }
    }

    /**
     * 获取词元类型
     *
     * @return int
     */
    public String getLexemeType() {
        return lexemeType;
    }


    public void setLexemeType(String lexemeType) {
        this.lexemeType = lexemeType;
    }

    /**
     * 合并两个相邻的词元
     *
     * @param l          l
     * @param lexemeType lexemeType
     * @return boolean 词元是否成功合并
     */
    public boolean append(Lexeme l, String lexemeType) {
        // 用于将两个相邻的词元合并成一个。规则：
        // 1.当前词元结束位置正好等于另一个词元起始位置；
        // 2.合并后更新长度与词元类型。
        if (l != null && this.getEndPosition() == l.getBeginPosition()) {
            this.length += l.getLength();
            this.lexemeType = lexemeType;
            return true;
        } else {
            return false;
        }
    }


    /**
     * to string
     *
     * @return string
     */
    @Override
    public String toString() {
        return this.getBeginPosition() + "-" + this.getEndPosition() + " : " + this.lexemeText + " : " + this.getLexemeType();
    }

}
