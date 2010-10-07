package ru.frostman.util.scalaxy;

import org.json.simple.JSONArray;

/**
 * @author slukjanov aka Frostman
 */
public class ScalaxyResponse {
    private JSONArray data;
    private ScalaxyStatus status;
    private boolean success;

    public ScalaxyResponse(JSONArray data, ScalaxyStatus status, boolean success) {
        this.data = data;
        this.status = status;
        this.success = success;
    }

    public JSONArray getData() {
        return data;
    }

    public ScalaxyStatus getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[')
                .append(success ? "SUCCESS" : "FAIL")
                .append("] ")
                .append(status).append('(').append(status.getDescription()).append(')')
                .append(" : ")
                .append(data != null ? data.toJSONString() : "no data");
        
        return sb.toString();
    }
}
