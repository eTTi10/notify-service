package com.lguplus.fleta.util;

import com.lguplus.fleta.exception.ServiceException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DTO 직접 반환을 위한 Converter
 * @version 1.0
 */
public class DtoConverter {

    private DtoConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 쿼리 결과 목록을 DTO(setter) 에 주입한다.
     * @param results   쿼리 결과 목록
     * @param classType dto
     * @return List<T>
     */
    public static <T> List<T> convertList(List<?> results, Class<T> classType) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> aliasResults = getAliasList(results);

        Set<String> aliasSet = aliasResults.stream()
            .map(Map::keySet)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

        Map<String, Method> setterMethod = getSetterMethod(aliasSet, classType);

        List<T> resultModels = new ArrayList<>();

        try {
            for (Map<String, Object> element: aliasResults) {
                Constructor<T> constructor = classType.getConstructor();
                T model = constructor.newInstance();
                executeReflection(element, setterMethod, model);
                resultModels.add(model);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ServiceException("클래스 변환 오류", e);
        }

        return resultModels;
    }

    /**
     * 단건 쿼리 결과를 DTO(setter) 에 주입한다.
     * @param element   단건 쿼리 결과
     * @param classType dto
     * @return Optional<T>
     */
    public static <T> Optional<T> convertSingle(Object element, Class<T> classType) {
        if (element == null) {
            return Optional.empty();
        }

        Map<String, Object> aliasResults = elementToMap(element);
        if (aliasResults.size() == 0) {
            return Optional.empty();
        }
        Set<String> aliasSet = aliasResults.keySet();
        Map<String, Method> setterMethod = getSetterMethod(aliasSet, classType);

        T model;

        try {
            Constructor<T> constructor = classType.getConstructor();
            model = constructor.newInstance();
            executeReflection(aliasResults, setterMethod, model);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ServiceException("클래스 변환 오류", e);
        }

        return Optional.of(model);
    }

    /**
     * 컬럼명 aliasSet 와 일치하는 class 에 있는 setter 를 찾는다.
     * @param aliasSet  setter 이름
     * @param classType dto
     * @return Map<String, Method>
     */
    private static <T> Map<String, Method> getSetterMethod(Set<String> aliasSet, Class<T> classType) {
        Map<String, Method> map = new HashMap<>();

        for (String alias: aliasSet) {
            String setterMethodName = "set" + StringCaseUtils.autoPascalCase(alias);

            Arrays.stream(classType.getMethods())
                .filter(m -> m.getName().startsWith("set") && setterMethodName.equalsIgnoreCase(m.getName()))
                .forEach(m -> map.put(alias, m));
        }

        return map;
    }

    /**
     * setter 에 대한 정보를 가진 맵을 가지고 reflection (setter 파라미터에 맞는 형 변환 후 값 대입)을 수행한다.
     * @param element         Map 으로 변환된 쿼리 결과
     * @param setterMethodMap setter Map
     * @param model           dto
     */
    private static <T> void executeReflection(Map<String, Object> element, Map<String, Method> setterMethodMap, T model) throws InvocationTargetException, IllegalAccessException {
        for (Map.Entry<String, Object> entry: element.entrySet()) {
            String alias = entry.getKey();
            Object value = entry.getValue();

            // Objects.nonNull(value) 체크는 일단 생략
            if (setterMethodMap.containsKey(alias)) {
                Method method = setterMethodMap.get(alias);
                Optional<?> castValue = VariableCastUtils.castValue(value, method.getParameterTypes()[0]);
                method.invoke(model, castValue.orElse(null));
            }
        }
    }

    /**
     * List<Object> 형식의 쿼리 결과를 List<Map<String, Object>> 로 변환한다. 쿼리 결과는 Object:Map<alias, tuple> 값이어야 한다.
     * @param results 쿼리 결과 목록
     * @return List<Map < String, Object>>
     */
    private static List<Map<String, Object>> getAliasList(List<?> results) {
        return results.stream().map(DtoConverter::elementToMap).collect(Collectors.toList());
    }

    /**
     * Object 형식의 레코드 하나가 Map<alias, tuple> 값으로, 형변환을 통해 String, Object 형태로 변경하여 반환한다.
     * @param element 쿼리 결과(하나의 레코드)
     * @return Map<String, Object>
     */
    private static Map<String, Object> elementToMap(Object element) {
        Map<?, ?> map = (Map<?, ?>) element;

        Map<String, Object> resultMap = new TreeMap<>();

        for (Map.Entry<?, ?> entry: map.entrySet()) {
            String key = (String) entry.getKey(); // 칼럼 알리아스
            Object value = entry.getValue(); // 칼럼 값

            resultMap.put(key, value);
        }

        return resultMap;
    }
}
