package com.rjfield.marketwatcher.util;

import java.util.Map;

public class ResourceNameUtils {

    public static String UserIdFromResourceName(String resourceName) {
        Map<String, String> map = nameToMap(resourceName);
        return map.get("users");
    }
    static Map<String, String> nameToMap(String resourceName) {
        String[] parts = resourceName.split("/");
        Map<String, String> map = new java.util.HashMap<>();
        for (int i = 0; i < parts.length; i += 2) {
            map.put(parts[i], parts[i + 1]);
        }
        return map;
    }
}
