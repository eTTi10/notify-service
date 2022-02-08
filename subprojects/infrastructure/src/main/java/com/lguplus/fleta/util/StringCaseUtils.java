package com.lguplus.fleta.util;

import java.util.regex.Pattern;

/**
 * 문자열 Case 변환 Util
 * @version 1.0
 */
public class StringCaseUtils {

    private StringCaseUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 주어진 문자열에 _ 가 포함되어 있으면 snakeCase 로 판단하고 _ 가 없으면 camelCase 로 판단하여
     * 두 케이스에 대해 모두 pascalCase 로 변환합니다.
     */
    public static String autoPascalCase(String str) {
        if (str.contains("_")) {
            return snakeCaseToPascalCase(str);
        }
        return camelCaseToPascalCase(str);
    }

    /**
     * input_snake_case 형식을 InputSnakeCase 로 변경합니다.
     */
    public static String snakeCaseToPascalCase(String snakeCase) {
        return camelCaseToPascalCase(snakeCaseToCamelCase(snakeCase));
    }

    /**
     * input_snake_case 형식을 inputSnakeCase 로 변경합니다.
     */
    public static String snakeCaseToCamelCase(String snakeCase) {
        StringBuilder sb = new StringBuilder();
        for (String s: snakeCase.toLowerCase().split("_")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * inputCamelCase 형식을 InputCamelCase 로 변경합니다.
     */
    public static String camelCaseToPascalCase(String camelCase) {
        if (isMixedUpperLowerCase(camelCase)) {
            return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
        }
        return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1).toLowerCase();
    }

    /**
     * inputCamelCase 형식을 INPUT_CAMEL_CASE 로 변경합니다.
     */
    public static String camelCaseToSnakeCase(String field) {
        return toUpperCamelCaseToSnakeCase(field);
    }

    /**
     * inputCamelCase 형식을 INPUT_CAMEL_CASE / input_camel_case 로 변경합니다.
     */
    public static String toUpperCamelCaseToSnakeCase(String field) {
        String camelCase = field.replaceAll("([a-z])([A-Z]+)", "$1_$2");
        return camelCase.toUpperCase();
    }

    /**
     * inputCamelCase 형식을 INPUT_CAMEL_CASE / input_camel_case 로 변경합니다.
     */
    public static String toLowerCamelCaseToSnakeCase(String field) {
        String camelCase = field.replaceAll("([a-z])([A-Z]+)", "$1_$2");
        return camelCase.toLowerCase();
    }

    /**
     * 알파벳의 대문자와 소문자가 섞여 있을 경우 camelCase 에 대한 처리를 위한 함수입니다.
     * -> isItCamelCase : 앞 글자만 대문자로 변경되면 pascalCase 가 됨
     * -> SEQ : 대문자만 있으므로 앞글자는 대문자로 나머지는 소문자 처리함
     * -> seq : 소문자만 있으므로 앞글자는 대문자로 나머지는 소문자 처리함
     */
    private static boolean isMixedUpperLowerCase(String str) {
        final String upperRegex = "^[A-Z]+$";
        final String lowerRegex = "^[a-z]+$";

        return !(Pattern.compile(upperRegex).matcher(str).find() || Pattern.compile(lowerRegex).matcher(str).find());
    }
}
