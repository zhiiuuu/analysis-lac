package com.thundax.analysis.core.client.protocol;

import java.util.List;
import java.util.Map;

public class LacResponseParam implements Jsonable {

    private List<String> word;
    private List<String> tag;

    public LacResponseParam() {

    }

    public LacResponseParam(Map<String, ?> map) {
        this.fromMap(map);
    }


    public List<String> getWord() {
        return word;
    }

    public void setWord(List<String> word) {
        this.word = word;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public boolean validate() {
        return this.word != null && this.tag != null && this.word.size() == this.tag.size();
    }

    @Override
    public void fromMap(Map<String, ?> map) {
        if (map.containsKey("word") && map.containsKey("tag")) {
            this.word = (List<String>) map.get("word");
            this.tag = (List<String>) map.get("tag");
        }
    }

}
