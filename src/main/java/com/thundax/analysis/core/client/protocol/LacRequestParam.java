package com.thundax.analysis.core.client.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LacRequestParam implements Jsonable {

    private List<String> text = new ArrayList<>();

    public LacRequestParam(String s) {
        text.add(s);
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    @Override
    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", this.getText());
        return map;
    }

}
