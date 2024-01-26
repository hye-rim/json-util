

# JsonUtil 프로젝트

## 개요
`JsonUtil`은 JSON 데이터를 효율적으로 처리하기 위한 유틸리티 클래스로, 주어진 JSON 문자열 내에서 특정 조건에 따라 데이터를 수정하는 기능을 제공합니다. 이 유틸리티는 주로 식별자를 기반으로 JSON 객체 내의 특정 필드를 수정하는 데 사용됩니다.

## 기능
- **modifyJsonByIdentifiers**: 주어진 JSON 문자열에서 지정된 경로의 배열 내의 객체들을 식별자 리스트를 기반으로 수정합니다.
- **modifyJsonByIdentifiers (Map 버전)**: 식별자와 해당 식별자에 대한 값을 매핑하는 Map을 사용하여 JSON 문자열을 수정합니다.
- **JsonObjectWrapper**: JSON 객체의 참조 타입을 래핑하여, 원본 `JsonObject`와의 참조 관계를 유지합니다.

## 사용 방법
1. `JsonUtil` 클래스를 Spring 프로젝트에 컴포넌트로 등록합니다.
2. 원하는 JSON 문자열과 수정할 경로, 식별자 등을 메소드에 전달하여 호출합니다.
3. 반환된 JSON 문자열을 확인합니다.

## 예제
```java
String jsonString = "{\"test\": ... }"; // 수정할 JSON 문자열
String targetFullPath = "test/project"; // 수정할 객체가 있는 경로
List<String> identifiers = List.of("BackOffice"); // 식별자 리스트
String identifierField = "name"; // 객체 내에서 식별자로 사용할 필드명
String targetField = "status"; // 추가할 필드명
boolean matchValue = true; // 식별자가 일치할 경우 설정할 값
boolean defaultValue = false; // 식별자가 일치하지 않을 경우 설정할 값

String result = jsonUtil.modifyJsonByIdentifiers(jsonString, targetFullPath, identifiers, identifierField, targetField, matchValue, defaultValue);
```

## 테스트
`JsonUtilApplicationTests` 클래스는 `JsonUtil`의 주요 기능을 테스트하는 JUnit 테스트 케이스를 제공합니다. 이 테스트 클래스는 다음을 포함합니다:
- `testModifyJsonByIdentifiersWithList`: 식별자 리스트를 사용하여 JSON 문자열을 수정하는 기능을 테스트합니다.
- `testModifyJsonByIdentifiersWithMap`: 식별자 Map을 사용하여 JSON 문자열을 수정하는 기능을 테스트합니다.

테스트를 실행하기 위해서는 Spring Boot 테스트 환경이 필요합니다. `@SpringBootTest` 어노테이션을 사용하여 Spring의 전체 애플리케이션 컨텍스트를 로드하는 테스트 환경을 구성합니다.

## 요구 사항
- Java 8 이상
- Spring Framework
- Gson 라이브러리

## 빌드 및 실행
프로젝트는 Maven 또는 Gradle을 사용하여 빌드할 수 있습니다. `JsonUtil` 클래스를 프로젝트에 포함시킨 후, Spring 애플리케이션 컨텍스트에서 해당 유틸리티를 사용할 수 있습니다.

---

