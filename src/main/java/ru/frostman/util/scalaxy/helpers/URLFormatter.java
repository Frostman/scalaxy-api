package ru.frostman.util.scalaxy.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class URLFormatter {
    private static final char START_VAR = '{';
    private static final char END_VAR = '}';

    private String format;

    public URLFormatter(String format) {
        this.format = format;
    }

    public String format(String... params) {
        Map<String, String> map = new HashMap<String, String>();

        for (String param : params) {
            String[] split = param.split("=");
            if (split.length != 2) {
                throw new IllegalArgumentException("Unsupported parameters format");
            }
            map.put(split[0], split[1]);
        }

        return format(map);
    }

    public String format(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();

        StringBuilder varName = new StringBuilder();
        boolean inVarName = false;
        for (int i = 0; i < format.length(); i++) {
            char curr = format.charAt(i);
            if (curr == START_VAR) {
                varName = new StringBuilder();
                inVarName = true;
            } else if (curr == END_VAR) {
                String varNameStr = varName.toString();
                String var = null;
                if (params != null) {
                    var = params.get(varNameStr);
                }
                if (var != null) {
                    sb.append(var);
                } else {
                    throw new RuntimeException("Parameter '" + varNameStr + "' required to format '" + format + "'.");
                }
                inVarName = false;
            } else {
                if (inVarName) {
                    varName.append(curr);
                } else {
                    sb.append(curr);
                }
            }
        }

        return sb.toString();
    }
}
