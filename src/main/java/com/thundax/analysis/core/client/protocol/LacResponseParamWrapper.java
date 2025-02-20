package com.thundax.analysis.core.client.protocol;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LacResponseParamWrapper implements Jsonable {
    private static final String STATUS_SUCCESS = "000";

    private String status;
    private String msg;
    private List<LacResponseParam> results;

    public LacResponseParamWrapper() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<LacResponseParam> getResults() {
        return results;
    }

    public void setResults(List<LacResponseParam> results) {
        this.results = results;
    }

    @Override
    public void fromMap(Map<String, ?> map) {
        if (map.containsKey("status")) {
            this.status = map.get("status").toString();
        }

        if (map.containsKey("msg")) {
            this.msg = map.get("msg").toString();
        }

        if (map.containsKey("results")) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("results");
            this.results = list.stream().map(LacResponseParam::new).collect(Collectors.toList());
        }
    }

    public static boolean isSuccess(LacResponseParamWrapper o) {
        return o != null && STATUS_SUCCESS.equals(o.status);
    }
}
