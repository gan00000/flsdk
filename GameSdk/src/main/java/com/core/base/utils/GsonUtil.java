package com.core.base.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Gson 工具类（静态方法封装）
 * 核心功能：JSON与对象/集合互转、格式化、脱敏，解决空值、日期、复杂类型解析问题
 */
public class GsonUtil {

    // 基础Gson实例（默认配置：null值不序列化、时间格式yyyy-MM-dd HH:mm:ss）
    private static final Gson BASE_GSON = new GsonBuilder()
            .serializeNulls() // 序列化null值（避免字段丢失）
            .setDateFormat("yyyy-MM-dd HH:mm:ss") // 统一日期格式
            .disableHtmlEscaping() // 禁止HTML转义（如保留&、<、>等符号）
            .create();

    // 格式化Gson实例（用于生成易读的JSON字符串）
    private static final Gson FORMAT_GSON = new GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .disableHtmlEscaping()
            .setPrettyPrinting() // 格式化输出（换行、缩进）
            .create();

    // -------------------------- 核心转换方法 --------------------------

    /**
     * 对象转JSON字符串（基础版，无格式化）
     * @param obj 任意对象（Bean、Map、List等）
     * @return JSON字符串（null输入返回""）
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        return BASE_GSON.toJson(obj);
    }

    /**
     * 对象转JSON字符串（格式化版，用于日志打印、调试）
     * @param obj 任意对象
     * @return 格式化后的JSON字符串
     */
    public static String toJsonPretty(Object obj) {
        if (obj == null) {
            return "";
        }
        return FORMAT_GSON.toJson(obj);
    }

    /**
     * JSON字符串转指定类型对象（支持普通Bean、泛型Bean）
     * @param json JSON字符串
     * @param clazz 目标类（如 User.class）
     * @return 目标对象（JSON为空/格式错误返回null）
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (SStringUtil.isJsonEmpty(json)) {
            return null;
        }
        try {
            return BASE_GSON.fromJson(json, clazz);
        } catch (Exception e) {
            //throw new RuntimeException("JSON解析失败：" + json, e);
            PL.e("JSON解析失败：" + e.getMessage());
        }
        return null;
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        if (reader == null) {
            return null;
        }
        try {
            return BASE_GSON.fromJson(reader, clazz);
        } catch (Exception e) {
            //throw new RuntimeException("JSON解析失败：" + json, e);
            PL.e("JSON解析失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * JSON字符串转泛型集合（如 List<User>、Map<String, Object>）
     * @param json JSON字符串
     * @param typeOfT typeOfT 目标类型（如 TypeToken<List<User>>(){}.getType()）
     * @return 泛型集合
     *
     *  // 1. 定义目标类型：使用 TypeToken 来表示 List<User>
     *  // 2. 将 JSON 字符串解析为 List<User>
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        if (SStringUtil.isJsonEmpty(json)) {
            return null;
        }
        try {
            return BASE_GSON.fromJson(json, typeOfT);
        } catch (Exception e) {
            //throw new RuntimeException("JSON解析泛型集合失败：" + json, e);
            PL.e("JSON解析泛型集合失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * JSON字符串转List（简化方法，无需手动创建TypeToken）
     * @param json JSON数组字符串（如 ["a","b"]、[{"id":1},{"id":2}]）
     * @param clazz List中元素的类（如 String.class、User.class）
     * @return List集合
     *
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        // 构建 List<T> 的Type（核心：解决泛型擦除问题）
        Type listType = TypeToken.getParameterized(List.class, clazz).getType();
        return fromJson(json, listType);
    }

    /**
     * JSON字符串转Map（key为String，value为Object）
     * @param json JSON对象字符串（如 {"name":"张三","age":20}）
     * @return Map<String, Object>
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return fromJson(json, mapType);
    }

    // -------------------------- 辅助方法 --------------------------


    /**
     * JSON字段脱敏（如手机号、身份证号，用于日志隐藏敏感信息）
     * @param json JSON字符串
     * @param sensitiveFields 需要脱敏的字段名（如 "phone"、"idCard"）
     * @return 脱敏后的JSON字符串
     */
//    public static String desensitizeJson(String json, String... sensitiveFields) {
//        if (isEmpty(json) || sensitiveFields == null || sensitiveFields.length == 0) {
//            return json;
//        }
//        try {
//            // 解析为JsonElement，遍历字段脱敏
//            JsonElement jsonElement = JsonParser.parseString(json);
//            desensitizeElement(jsonElement, sensitiveFields);
//            return BASE_GSON.toJson(jsonElement);
//        } catch (Exception e) {
//            throw new RuntimeException("JSON脱敏失败：" + json, e);
//        }
//    }

    /**
     * 递归脱敏JsonElement（支持对象、数组嵌套）
     */
//    private static void desensitizeElement(JsonElement element, String... sensitiveFields) {
//        if (element.isJsonObject()) {
//            // 对象类型：遍历所有字段
//            element.getAsJsonObject().entrySet().forEach(entry -> {
//                String key = entry.getKey();
//                JsonElement value = entry.getValue();
//                // 匹配敏感字段，进行脱敏
//                for (String field : sensitiveFields) {
//                    if (field.equals(key) && value.isJsonPrimitive() && value.getAsString() != null) {
//                        String original = value.getAsString();
//                        entry.setValue(BASE_GSON.toJsonTree(desensitizeStr(original)));
//                        break;
//                    }
//                }
//                // 递归处理嵌套对象/数组
//                desensitizeElement(value, sensitiveFields);
//            });
//        } else if (element.isJsonArray()) {
//            // 数组类型：遍历每个元素
//            element.getAsJsonArray().forEach(item -> desensitizeElement(item, sensitiveFields));
//        }
//    }

    /**
     * 字符串脱敏规则（可自定义扩展）
     * 目前支持：手机号（138****1234）、身份证（110****1234）、姓名（张*、李**）
     */
//    private static String desensitizeStr(String str) {
//        if (str == null || str.length() == 0) {
//            return str;
//        }
//        // 手机号脱敏（11位）
//        if (str.matches("1[3-9]\\d{9}")) {
//            return str.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
//        }
//        // 身份证脱敏（15/18位）
//        if (str.matches("\\d{15}|\\d{17}[0-9Xx]")) {
//            return str.replaceAll("(\\d{3})\\d{11}(\\d{4})", "$1****$2");
//        }
//        // 姓名脱敏（1字留全，2字遮1位，3字以上遮中间）
//        if (str.length() == 1) {
//            return str;
//        } else if (str.length() == 2) {
//            return str.charAt(0) + "*";
//        } else {
//            return str.charAt(0) + "****" + str.charAt(str.length() - 1);
//        }
//    }
}