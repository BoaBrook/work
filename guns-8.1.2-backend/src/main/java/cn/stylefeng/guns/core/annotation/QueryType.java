package cn.stylefeng.guns.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryType {

    Type type() default Type.EQ;

    enum Type {

        EQ,

        NE,

        GT,

        GE,

        LT,

        LE,

        LIKE,

        LIKE_LEFT,

        LIKE_RIGHT,

        IN,

        NOT_IN,

        BETWEEN,

        NOT_BETWEEN,

        IS_NULL,

        IS_NOT_NULL,

        IS_EMPTY,

        IS_NOT_EMPTY,

        IS_TRUE,

        IS_FALSE,

        IS_UNKNOWN,

        IS_NOT_UNKNOWN,
    }

}
