package com.example.casclient1.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Descripton: JSON工具类
 * @Author: Hchenbin
 * @Date:2022/6/23 15:35
 */
public final class JSON {
    private static final Logger LOG = LoggerFactory.getLogger(JSON.class);
    private static final ObjectMapper MAPPER = objectMapper();

    public static <T> T parse(String content, Class<T> tClass){
        try {
            return MAPPER.readValue(content, tClass);
        } catch (JsonProcessingException e) {
            LOG.error("json parse exception", e);
            return null;
        }
    }

    public static String stringify(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOG.error("json stringify exception", e);
            return null;
        }
    }

    public static String marshal(Object value) throws Exception {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonGenerationException e) {
            throw new Exception(e);
        } catch (JsonMappingException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        }
    }


    public static List listToJsonField(List lists) {
        String jsonStr = JSONArray.toJSONString(lists, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
        List list = (List)JSONArray.parseObject(jsonStr, List.class);
        return list;
    }

    public static Map<String, Object> entityToMap(Object object) {
        String jsonStr = JSONObject.toJSONString(object);
        Map<String, Object> map = (Map)JSONObject.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {
        }, new Feature[0]);
        return map;
    }

    public static Map<String, String> entityToMaps(Object object) {
        String jsonStr = JSONObject.toJSONString(object);
        Map<String, String> map = (Map)JSONObject.parseObject(jsonStr, new TypeReference<Map<String, String>>() {
        }, new Feature[0]);
        return map;
    }

    public static Map<String, Object> stringToMap(String object) {
        Map<String, Object> map = (Map)JSONObject.parseObject(object, new TypeReference<Map<String, Object>>() {
        }, new Feature[0]);
        return map;
    }

    public static <T> T getJsonToBean(String jsonData, Class<T> clazz) {
        return com.alibaba.fastjson.JSON.parseObject(jsonData, clazz);
    }

    public static JSONArray getJsonToJsonArray(String json) {
        return JSONArray.parseArray(json);
    }

    public static <T> JSONArray getListToJsonArray(List<T> list) {
        return JSONArray.parseArray(getObjectToString(list));
    }

    public static String getObjectToString(Object object) {
        return com.alibaba.fastjson.JSON.toJSONString(object, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
    }

    public static String getObjectToStringAsDate(Object object) {
        return com.alibaba.fastjson.JSON.toJSONStringWithDateFormat(object, "yyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
    }

    public static String getObjectToStringDateFormat(Object object, String dateFormat) {
        return com.alibaba.fastjson.JSON.toJSONStringWithDateFormat(object, dateFormat, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
    }

    public static <T> T getJsonToBeanEx(Object dto, Class<T> clazz) throws IllegalArgumentException {
        if (dto == null) {
            throw new IllegalArgumentException("此条数据不存在");
        } else {
            return com.alibaba.fastjson.JSON.parseObject(getObjectToString(dto), clazz);
        }
    }

    public static <T> List<T> getJsonToList(String jsonData, Class<T> clazz) {
        return com.alibaba.fastjson.JSON.parseArray(jsonData, clazz);
    }

    public static List<Map<String, Object>> getJsonToListMap(String jsonData) {
        return (List) com.alibaba.fastjson.JSON.parseObject(jsonData, new TypeReference<List<Map<String, Object>>>() {
        }, new Feature[0]);
    }

    public static List<Map<String, Object>> getJsonToList(JSONArray jsonArray) {
        return (List) com.alibaba.fastjson.JSON.parseObject(com.alibaba.fastjson.JSON.toJSONString(jsonArray), new TypeReference<List<Map<String, Object>>>() {
        }, new Feature[0]);
    }

    public static <T> T getJsonToBean(Object dto, Class<T> clazz) {
        return com.alibaba.fastjson.JSON.parseObject(getObjectToString(dto), clazz);
    }

    public static <T> List<T> getJsonToList(Object dto, Class<T> clazz) {
        return com.alibaba.fastjson.JSON.parseArray(getObjectToString(dto), clazz);
    }


    private static ObjectMapper objectMapper() {
        ObjectMapper result = new ObjectMapper();
        //序列化的时候序列对象的所有属性
        result.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //反序列化的时候如果多了其他属性,不抛出异常
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //如果是空对象的时候,不抛异常
        result.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return result;
    }

    private JSON() {
    }
}
