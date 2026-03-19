package cn.stylefeng.guns.core.utils;

import cn.stylefeng.guns.core.annotation.QuerySortType;
import cn.stylefeng.guns.core.annotation.QueryType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WrapperGenerator {

    /**
     * 复制源对象字段到目标对象，并生成 LambdaQueryWrapper
     * @param source 源对象（提供字段和值）
     * @param target 目标对象（接收值，用于生成查询条件）
     * @return 基于目标对象字段的 LambdaQueryWrapper
     */
    public static <T> QueryWrapper<T> generateQueryWrapper(Object source, Class<T> target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("源对象和目标对象不能为 null");
        }

        Class<?> sourceClass = source.getClass();
        List<FieldValue> fieldValues = new ArrayList<>();

        // 1. 反射获取源对象的所有字段（包括父类字段）
        List<Field> sourceFields = getAllFields(sourceClass);
        for (Field sourceField : sourceFields) {
            sourceField.setAccessible(true);
            String fieldName = sourceField.getName();

            try {
                // 2. 查找目标对象中是否有同名字段（包括父类）
                getFieldIncludeSuper(target, fieldName);

                // 3. 获取源字段值，并复制到目标字段
                Object value = sourceField.get(source);
                if (value != null) { // 只处理非 null 值
                    fieldValues.add(new FieldValue(sourceField, value)); // 记录字段名和值
                }
            } catch (NoSuchFieldException e) {
                // 目标对象没有该字段，跳过
                continue;
            } catch (IllegalAccessException e) {
                // 字段访问权限异常（通常是 private 字段未设置 accessible，这里已处理）
                throw new RuntimeException("字段访问失败：" + fieldName, e);
            } catch (IllegalArgumentException e) {
                // 类型不兼容（如 String 赋值给 Integer），跳过
                continue;
            }
        }

        // 4. 构建 LambdaQueryWrapper，添加非 null 字段的 eq 条件
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        for (FieldValue fv : fieldValues) {
            if(fv.field.isAnnotationPresent(QueryType.class)){
                QueryType queryType = fv.field.getAnnotation(QueryType.class);
                switch (queryType.type()) {
                    case EQ:
                        wrapper.eq(camelToUnderline(fv.field.getName()), fv.value);
                        break;
                    case NE:
                        wrapper.ne(camelToUnderline(fv.field.getName()), fv.value);
                        break;
                    case GT:
                        wrapper.gt(camelToUnderline(fv.field.getName()), fv.value);
                        break;
                    case LIKE:
                        wrapper.like(camelToUnderline(fv.field.getName()), fv.value);
                        break;
                    case IN:
                        if (!(fv.value instanceof List)) {
                            throw new IllegalArgumentException("IN 查询需要 List 类型的值");
                        }
                        List<?> valueList = (List<?>) fv.value;
                        wrapper.in(camelToUnderline(fv.field.getName()), valueList);
                        break;
                    default:break;
                }
            }else{
                wrapper.eq(camelToUnderline(fv.field.getName()), fv.value);
            }
        }
        //排序
        List<Field> targetFields = getAllFields(target);
        for (Field targetField : targetFields) {
            if(targetField.isAnnotationPresent(QuerySortType.class)){
                QuerySortType querySortType = targetField.getAnnotation(QuerySortType.class);
                switch (querySortType.type()) {
                    case ASC:
                        wrapper.orderByAsc(camelToUnderline(targetField.getName()));
                        break;
                    case DESC:
                        wrapper.orderByDesc(camelToUnderline(targetField.getName()));
                        break;
                    default:break;
                }
            }
        }
        return wrapper;
    }

    public static String camelToUnderline(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        // 在每个大写字母前添加下划线，并转换为小写
        String underscore = camelCase.replaceAll("([A-Z])", "_$1");

        // 如果以下划线开头，则去掉开头的下划线
        if (underscore.startsWith("_")) {
            underscore = underscore.substring(1);
        }

        return underscore.toLowerCase();
    }

    /**
     * 获取类及其所有父类的字段
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) { // 遍历到 Object 类为止
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 查找类或其父类中是否存在指定字段
     */
    private static Field getFieldIncludeSuper(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // 向上查找父类
            }
        }
        throw new NoSuchFieldException("字段不存在：" + fieldName);
    }

    /**
     * 辅助类：存储字段名和对应值
     */
    private static class FieldValue {
        Field field;
        Object value;

        FieldValue(Field field, Object value) {
            this.field = field;
            this.value = value;
        }
    }

}
