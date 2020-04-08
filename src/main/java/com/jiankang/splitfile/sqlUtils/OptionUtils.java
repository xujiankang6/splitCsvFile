package com.jiankang.splitfile.sqlUtils;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class OptionUtils {

    public static void main(String[] args) {
        String context = "{\"options\": [{\"label\": \"产品维护上量\", \"value\": \"1\"}, {\"label\": \"产品介绍\", \"value\": \"2\"}, {\"label\": \"学术活动邀请\", \"value\": \"3\"}, {\"label\": \"新客户开发\", \"value\": \"4\"}, {\"label\": \"客户交换\", \"value\": \"5\"}, {\"label\": \"品牌提示物发放\", \"value\": \"6\"}, {\"label\": \"节日礼节\", \"value\": \"7\"}, {\"label\": \"样品分发\", \"value\": \"8\"}, {\"label\": \"学术活动回访\", \"value\": \"9\"}, {\"label\": \"新院开发\", \"value\": \"10\"}], \"is_unique\": false, \"is_virtual\": false, \"is_required\": false}";
        String column = "\"call\".ext->>'fact_purpose'";
        Gson gson = new Gson();
        Map<String, Object> params = gson.fromJson(context, Map.class);
        List<Map<String, String>> options = (List<Map<String, String>>) params.get("options");
        StringBuilder builder = new StringBuilder("case  ");
        for (Map<String, String> item : options) {
            String label = item.get("label");
            Integer value = Integer.valueOf(item.get("value"));
            builder.append(" when " + column + "='" + value + "' then '" + label + "'");
        }
        builder.append("end ");
        System.out.println(builder.toString());

    }
}
