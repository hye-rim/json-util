package com.example.jsonutil;

import com.hr.util.JsonUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class JsonUtilApplicationTests {

    @Autowired
    private JsonUtil jsonUtil;

    @Test
    public void testModifyJsonByIdentifiersWithList() {
        String jsonString = "{\"test\": {\"name\": \"hazel\", \"project\": [{\"name\": \"BackOffice\",\"language\": \"JAVA\"},{\"name\": \"API\",\"language\": \"JAVA\"}]}}";
        String targetFullPath = "test/project";
        String identifierField = "name";
        String targetField = "status";
        boolean matchValue = true;
        boolean defaultValue = false;

        List<String> identifiers = new ArrayList<>();
        identifiers.add("BackOffice");

        String result = jsonUtil.modifyJsonByIdentifiers(jsonString, targetFullPath, identifiers, identifierField, targetField, matchValue, defaultValue);

        String expectedJson = "{\"test\":{\"name\":\"hazel\",\"project\":[{\"name\":\"BackOffice\",\"language\":\"JAVA\",\"status\":\"true\"},{\"name\":\"API\",\"language\":\"JAVA\",\"status\":\"false\"}]}}";
        assertEquals(expectedJson, result, "The modified JSON does not match the expected result.");
    }

    @Test
    public void testModifyJsonByIdentifiersWithMap() {
        String jsonString = "{\n" +
                "  \"test\": {\n" +
                "    \"name\": \"ddo\",\n" +
                "    \"project\": [\n" +
                "      {\n" +
                "        \"name\": \"BackOffice\",\n" +
                "        \"language\": \"JAVA\",\n" +
                "        \"manager\": \"kelvin\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"API\",\n" +
                "        \"language\": \"JAVA\",\n" +
                "        \"manager\": \"hazel\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"FE\",\n" +
                "        \"language\": \"React\",\n" +
                "        \"manager\": \"jeibi\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        String targetFullPath = "test/project";
        String identifierField = "manager";
        String targetField = "status";

        String modifiedJson = jsonUtil.modifyJsonByIdentifiers(jsonString, targetFullPath,
                Map.of("kelvin", "휴가중", "hazel", "근무중"),
                identifierField, targetField, "퇴사");

        String expectedJson = "{\"test\":{\"name\":\"ddo\",\"project\":[{\"name\":\"BackOffice\",\"language\":\"JAVA\",\"manager\":\"kelvin\",\"status\":\"휴가중\"},{\"name\":\"API\",\"language\":\"JAVA\",\"manager\":\"hazel\",\"status\":\"근무중\"},{\"name\":\"FE\",\"language\":\"React\",\"manager\":\"jeibi\",\"status\":\"퇴사\"}]}}";
        assertEquals(expectedJson, modifiedJson, "The modified JSON does not match the expected result.");
    }


}
