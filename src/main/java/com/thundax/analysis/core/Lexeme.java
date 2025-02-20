package com.thundax.analysis.core;

public class Lexeme implements Comparable<Lexeme> {

    /**
     * 词元的起始位移，词元的相对起始位置，词元的长度
     */
    private int offset;
    private int begin;
    private int length;

    /**
     * 词元文本，词元类型
     */
    private String lexemeText;
    private String lexemeType;


    public Lexeme(int offset, int begin, int length, String lexemeType, String lexemeText) {
        this.offset = offset;
        this.begin = begin;
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
        this.lexemeText = lexemeText;
        this.lexemeType = lexemeType;
    }

    /**
     * 判断词元相等算法
     * 起始位置偏移、起始位置、终止位置相同
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
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int absBegin = getBeginPosition();
        int absEnd = getEndPosition();
        return (absBegin * 37) + (absEnd * 31) + ((absBegin * absEnd) % getLength()) * 11;
    }

    /**
     * 词元在排序集合中的比较算法
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
                //this.length < other.getLength()
                return 1;
            }

        } else {
            //this.begin > other.getBegin()
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
