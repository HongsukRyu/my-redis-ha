package com.backend.api.common.utils;

public class DomainUtils {
    public static final String TYPE_STRING = "string";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_DATETIME = "datetime";
    public static final String TYPE_BOOLEAN = "boolean";

    public static final String EQ = "eq";
    public static final String LT = "lt";
    public static final String LE = "le";
    public static final String GT = "gt";
    public static final String GE = "ge";
    public static final String BETWEEN = "between";
    public static final String IN = "in";

    public static String convertParam(String key, Object value, String sign, String type, String... inValues) {
        if(value!=null) {
            StringBuilder conditionBuilder = new StringBuilder();
            conditionBuilder.append(key).append("::");
            conditionBuilder.append(type).append("::");
            conditionBuilder.append(sign).append("::");
            conditionBuilder.append(value).append("::");

            // IN 이었을 경우 처리
            if(sign.equals(IN)) {
                if(inValues == null)
                    conditionBuilder.append("||");
                else {
                    for(String val : inValues) {
                        conditionBuilder.append(val).append("||");
                    }
                }
            }

            return conditionBuilder.toString();
        }


        return "";
    }
}
