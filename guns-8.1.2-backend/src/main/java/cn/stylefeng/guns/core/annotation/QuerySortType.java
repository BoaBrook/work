package cn.stylefeng.guns.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QuerySortType {

    Type type() default Type.ASC;

    enum Type {
        ASC,
        DESC
    }

}
