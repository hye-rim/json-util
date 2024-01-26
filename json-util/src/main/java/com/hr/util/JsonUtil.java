package com.hr.util;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonUtil {

    /**
     * JsonObject의 참조 타입을 래핑하기 위해 사용
     * Java의 참조 타입의 특성 상 리스트에서 객체를 직접 수정하면 원본 JsonObject에도 반영되지만,
     * 직접 JsonObject 인스턴스를 사용할 경우, 리스트의 객체와 원본 JsonObject 간의 연결이 꾾어짐.
     * 이를 방지하기 위해 JsonObject를 래핑하는 JsonObjectWrapper 클래스를 사용하여 원본 JsonObject와의 참조 관계를 유지하도록 함
     */
    public class JsonObjectWrapper {
        JsonObject jsonObject;

        public JsonObjectWrapper(JsonObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public String toString() {
            return jsonObject.toString();
        }
    }
    /**
     * 주어진 JSON 문자열에서 지정된 경로의 배열 내의 객체들을 식별자를 기반으로 수정
     *
     * @param jsonString        수정할 원본 JSON 문자열.
     * @param targetFullPath    수정할 객체가 있는 경로.
     * @param identifiers       식별자 리스트. 해당 식별자를 포함하는 객체만 값을 변경
     * @param identifierField   객체 내에서 식별자로 사용할 필드명.
     * @param targetField       추가할 필드명.
     * @param matchValue        식별자가 일치할 경우 설정할 값.
     * @param defaultValue      식별자가 일치하지 않을 경우 설정할 값.
     * @return                  식별자를 기반으로 수정된 JSON 문자열.
     */
    public String modifyJsonByIdentifiers(String jsonString, String targetFullPath, List<String> identifiers,
                                          String identifierField, String targetField, Object matchValue, Object defaultValue) {

        try {
            // 1. JSON 문자열에서 지정된 경로의 배열 내의 객체들을 List로 추출
            List<JsonObjectWrapper> extractedList = extractObjectsFromJson(jsonString, targetFullPath);

            // 2. 추출한 리스트에서 식별자 필드를 기반으로 값을 변경
            modifyListBasedOnIdentifiers(extractedList, identifiers, identifierField, targetField, matchValue, defaultValue);

            // 3. 원본 JSON 문자열에 수정된 리스트를 반영하여 최종 결과를 반환
            return updateJsonWithModifiedList(jsonString, targetFullPath, extractedList);

        } catch (Exception e) {
            return e.getMessage();
        }
    }


    /**
     * 주어진 JSON 문자열을 수정하여 식별자를 기반으로 특정 필드의 값을 변경
     *
     * @param jsonString            수정할 원본 JSON 문자열.
     * @param targetFullPath        수정할 객체가 있는 경로.
     * @param identifierMatchValues 식별자와 해당 식별자에 대한 값을 매핑하는 Map.
     *                              예) {"1": "가", "2": "나"}와 같이 식별자 "1"에 대한 값은 "가"로 설정되어야 함을 나타남
     * @param identifierField       객체 내에서 식별자로 사용할 필드명.
     * @param targetField           추가/수정할 필드명.
     * @param defaultValue          식별자가 Map에 없을 경우 설정할 기본값.
     * @return                      식별자를 기반으로 수정된 JSON 문자열.
     */
    public String modifyJsonByIdentifiers(String jsonString, String targetFullPath,
                                          Map<String, Object> identifierMatchValues, String identifierField,
                                          String targetField, Object defaultValue) {

        try {
            // 1. JSON 문자열에서 지정된 경로의 배열 내의 객체들을 List로 추출
            List<JsonObjectWrapper> extractedList = extractObjectsFromJson(jsonString, targetFullPath);

            // 2. 추출한 리스트에서 식별자 필드를 기반으로 값을 변경
            modifyListBasedOnIdentifiers(extractedList, identifierMatchValues, identifierField, targetField, defaultValue);

            // 3. 원본 JSON 문자열에 수정된 리스트를 반영하여 최종 결과를 반환
            return updateJsonWithModifiedList(jsonString, targetFullPath, extractedList);

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 주어진 JsonObjectWrapper 리스트를 수정하여 식별자를 기반으로 특정 필드의 값을 변경
     *
     * @param list                  JsonObjectWrapper 객체의 리스트.
     * @param identifierMatchValues 식별자와 해당 식별자에 대한 값을 매핑하는 Map.
     * @param identifierField       객체 내에서 식별자로 사용할 필드명.
     * @param targetField           추가/수정할 필드명.
     * @param defaultValue          식별자가 Map에 없을 경우 설정할 기본값.
     */
    private void modifyListBasedOnIdentifiers(List<JsonObjectWrapper> list,
                                              Map<String, Object> identifierMatchValues, String identifierField,
                                              String targetField, Object defaultValue) {
        for (JsonObjectWrapper wrapper : list) {
            JsonObject obj = wrapper.jsonObject;
            String idValue = obj.has(identifierField) ? obj.get(identifierField).getAsString() : null;

            if (idValue != null && identifierMatchValues.containsKey(idValue)) {
                obj.addProperty(targetField, identifierMatchValues.get(idValue).toString());
            } else {
                if (defaultValue != null) {
                    obj.addProperty(targetField, defaultValue.toString());
                } else {
                    obj.add(targetField, JsonNull.INSTANCE);
                }
            }
        }
    }


    /**
     * 주어진 JSON 문자열에서 원하는 경로의 JsonElement(객체 또는 배열)을 List로 추출
     *
     * @param jsonString 주어진 JSON 문자열.
     * @param fullPath   추출하려는 경로.
     * @return JsonObject 또는 JsonArray의 리스트.
     */
    public List<JsonObjectWrapper> extractObjectsFromJson(String jsonString, String fullPath) throws JsonSyntaxException, IllegalArgumentException {
        List<JsonObjectWrapper> result = new ArrayList<>();

        JsonObject jsonObject;
        try {
            jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException(MessageProperty.MESSAGE_FORMAT_FAIL, e);
        }

        List<String> pathSegments = Arrays.asList(fullPath.split("/"));

        JsonElement targetElement = getElementUsingFullPath(jsonObject, pathSegments);
        if (targetElement == null) {
            throw new IllegalArgumentException(MessageProperty.MESSAGE_NO_DATA);
        }

        if (targetElement.isJsonObject()) {
            result.add(new JsonObjectWrapper(targetElement.getAsJsonObject()));
        } else if (targetElement.isJsonArray()) {
            for (JsonElement element : targetElement.getAsJsonArray()) {
                if (element.isJsonObject()) {
                    result.add(new JsonObjectWrapper(element.getAsJsonObject()));
                }
            }
        } else {
            throw new IllegalArgumentException(MessageProperty.MESSAGE_FAIL);
        }

        return result;
    }


    /**
     * 주어진 JsonObject에서 지정된 경로 세그먼트를 사용하여 대상 JsonElement를 추출.
     *
     * @param jsonObject  탐색을 시작할 기본 JsonObject.
     * @param pathSegments 탐색을 위한 경로 세그먼트 리스트.
     * @return 대상 JsonElement
     */
    private  JsonElement getElementUsingFullPath(JsonObject jsonObject, List<String> pathSegments) {
        if (pathSegments.isEmpty()) {
            return null;
        }

        String firstSegment = pathSegments.get(0);
        if (jsonObject.has(firstSegment)) {
            if (pathSegments.size() == 1) {
                return jsonObject.get(firstSegment);
            } else {
                return getElementUsingFullPath(jsonObject.getAsJsonObject(firstSegment), pathSegments.subList(1, pathSegments.size()));
            }
        }
        return null;
    }



    /**
     * 리스트에서 식별자 필드를 기반으로 값을 변경.
     *
     * @param JsonObjectList  수정할 JsonObject의 리스트.
     * @param identifierValues  식별자 값 리스트.
     * @param identifierField  식별자 필드명.
     * @param targetField     수정 대상 필드명.
     * @param matchValue      일치하는 값.
     * @param defaultValue    기본 값.
     */
    public void modifyListBasedOnIdentifiers(List<JsonObjectWrapper> JsonObjectList, List<String> identifierValues, String identifierField, String targetField, Object matchValue, Object defaultValue) {
        for (JsonObjectWrapper wrapper : JsonObjectList) {
            JsonObject obj = wrapper.jsonObject;
            if (!obj.has(identifierField)) {
                throw new IllegalArgumentException(MessageProperty.MESSAGE_NO_DATA + identifierField);
            }

            if (identifierValues.contains(obj.get(identifierField).getAsString())) {
                obj.addProperty(targetField, matchValue.toString());
            } else {
                if (defaultValue != null) {
                    obj.addProperty(targetField, defaultValue.toString());
                } else {
                    obj.add(targetField, JsonNull.INSTANCE);
                }
            }
        }
    }

    /**
     * 원본 JSON 문자열에 수정된 리스트를 반영.
     *
     * @param originalJson       원본 JSON 문자열.
     * @param targetArrayFullPath 대상 배열의 전체 경로.
     * @param modifiedList       수정된 JsonObject 리스트.
     * @return 수정된 JSON 문자열.
     */
    public String updateJsonWithModifiedList(String originalJson, String targetArrayFullPath, List<JsonObjectWrapper> modifiedList) {
        JsonObject originalJsonObject = JsonParser.parseString(originalJson).getAsJsonObject();
        JsonArray newArray = new JsonArray();

        for (JsonObjectWrapper wrapper : modifiedList) {
            newArray.add(wrapper.jsonObject);
        }

        List<String> pathSegments = Arrays.asList(targetArrayFullPath.split("/"));
        setArrayUsingFullPath(originalJsonObject, pathSegments, newArray);

        return originalJsonObject.toString();
    }

    private void setArrayUsingFullPath(JsonObject jsonObject, List<String> pathSegments, JsonArray newArray) {
        if (pathSegments.size() == 1) { //경로 세그먼트의 크기가 1인 경우, 즉 마지막 세그먼트에 도달한 경우.  jsonObject에 마지막 세그먼트(키)를 사용하여 newArray를 추가
            jsonObject.add(pathSegments.get(0), newArray);
        } else {
            setArrayUsingFullPath(jsonObject.getAsJsonObject(pathSegments.get(0)), pathSegments.subList(1, pathSegments.size()), newArray);
        }
    }

}

