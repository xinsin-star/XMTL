package top.xinsin.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

@Data
@Accessors(chain = true)
public class CommandEntity {
    private String name;
    private String description;
    private Class<?>[] children;
    private Class<?> clazz;
    private Method method;
}
