package com.dazo66;

import com.dazo66.test.TestBeanA;
import com.dazo66.test.TestBeanB;
import com.dazo66.test.TestBeanC;
import com.dazo66.test.TestBeanD;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dazo
 */
public class BeanJoiner {

    public static void main(String[] args) {

    }

    /**
     * 对两个对象list进行关联
     * 关联方式 参考 @see {com.dazo66.JoinerKey} 和 @see {com.dazo66.JoinerValue}
     * @param target 目标list
     * @param source 源list
     * @return

     */
    public static <T, S> T join(T target, S source) {
        return join(Collections.singletonList(target), Collections.singletonList(source)).get(0);
    }

    /**
     * 对两个对象list进行关联
     * 关联方式 参考 @see {com.dazo66.JoinerKey} 和 @see {com.dazo66.JoinerValue}
     * @param target 目标list
     * @param source 源list
     * @return

     */
    private static <T, S> List<T> join(List<T> target, List<S> source) {
        // 判空 这样不用处理
        if (target == null || target.isEmpty() || source == null || source.isEmpty()) {
            return target;
        }
        // 获取映射关系 构建映射的key相关的get和set字段
        Map<String, FieldRelaPair> targetJoinerMap = getJoinerMap(target.get(0).getClass());
        Map<String, FieldRelaPair> sourceJoinerMap = getJoinerMap(source.get(0).getClass());
        // 循环source的映射关系 对每个映射关系进行注入
        for (Map.Entry<String, FieldRelaPair> sourceFieldEntry : sourceJoinerMap.entrySet()) {
            if (!targetJoinerMap.containsKey(sourceFieldEntry.getKey())) {
                // 不存在映射关系
                continue;
            }
            // 构建原始map
            Map<?, ?> map;
            FieldRelaPair targetFieldFieldRelaPair = targetJoinerMap.get(sourceFieldEntry.getKey());
            if (targetFieldFieldRelaPair.isGroup()) {
                map = listToGroupMap(source, sourceFieldEntry.getValue().getKey(), sourceFieldEntry.getValue().getValue());
            } else {
                map = listToMap(source, sourceFieldEntry.getValue().getKey(), sourceFieldEntry.getValue().getValue());
            }
            targetFieldFieldRelaPair.getKey().setAccessible(true);
            for (T t : target) {
                try {
                    // 反射获取值
                    Object key = targetFieldFieldRelaPair.getKey().get(t);
                    // 如果不存在就跳过
                    if (key == null) {
                        continue;
                    }
                    // 如果不存在就跳过
                    Object value = map.get(key);
                    if (value == null || targetFieldFieldRelaPair.getValue() == null) {
                        continue;
                    }
                    // 反射写入值
                    targetFieldFieldRelaPair.getValue().setAccessible(true);
                    targetFieldFieldRelaPair.getValue().set(t, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("反射获取字段失败", e);
                }
            }
        }
        return target;
    }

    private static <T> Map<?, ?> listToMap(List<T> list, Field key, Field value) {
        key.setAccessible(true);
        if (value == null) {
            return list.stream().collect(Collectors.toMap(t -> {
                try {
                    return key.get(t);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("反射获取字段失败", e);
                }
            }, t -> t));
        }
        value.setAccessible(true);
        return list.stream().collect(Collectors.toMap(t -> {
            try {
                return key.get(t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("反射获取字段失败", e);
            }
        }, t -> {
            try {
                return value.get(t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("反射获取字段失败", e);
            }
        }));
    }

    private static <T> Map<Object, List<Object>> listToGroupMap(List<T> list, Field key, Field value) {
        key.setAccessible(true);
        if (value == null) {
            return list.stream().collect(Collectors.groupingBy(t -> {
                try {
                    return key.get(t);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("反射获取字段失败", e);
                }
            }));
        }
        value.setAccessible(true);
        return list.stream().collect(Collectors.groupingBy(t -> {
            try {
                return key.get(t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("反射获取字段失败", e);
            }
        }, Collectors.mapping(t -> {
            try {
                return value.get(t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("反射获取字段失败", e);
            }
        }, Collectors.toList())));
    }

    private static Map<String, FieldRelaPair> getJoinerMap(Class clazz) {
        Map<String, FieldRelaPair> joinerMap = new HashMap<>();
        Set<String> classAnnotation = new HashSet<>();
        Annotation[] annotationsByType1 = clazz.getAnnotationsByType(JoinerValue.class);
        for (Annotation annotation : annotationsByType1) {
            classAnnotation.add(((JoinerValue) annotation).value());
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field keyField : declaredFields) {
            JoinerKey[] annotationsByType = keyField.getAnnotationsByType(JoinerKey.class);
            for (JoinerKey joinerKey : annotationsByType) {
                String[] values = joinerKey.value();
                for (String value : values) {
                    if (classAnnotation.contains(value)) {
                        joinerMap.put(value, new FieldRelaPair(keyField, null, false));
                        continue;
                    }
                    for (Field valueField : declaredFields) {
                        for (JoinerValue joinerValue : valueField.getAnnotationsByType(JoinerValue.class)) {
                            if (joinerValue.value().equals(value)) {
                                joinerMap.put(value, new FieldRelaPair(keyField, valueField, joinerValue.isGroup()));
                            }
                        }
                        // 如果没有就null 取原对象
                        if (!joinerMap.containsKey(value)) {
                            joinerMap.put(value, new FieldRelaPair(keyField, null, false));
                        }
                    }
                }
            }
        }
        return joinerMap;
    }

    private static class FieldRelaPair {
        private final Field key;
        private final Field value;
        private final boolean isGroup;

        public FieldRelaPair(Field key, Field value, boolean isGroup) {
            this.key = key;
            this.value = value;
            this.isGroup = isGroup;
        }
        private Field getKey() {
            return key;
        }
        private Field getValue() {
            return value;
        }

        private boolean isGroup() {
            return isGroup;
        }
    }

}
