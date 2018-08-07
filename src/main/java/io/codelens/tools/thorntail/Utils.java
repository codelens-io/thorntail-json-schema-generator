package io.codelens.tools.thorntail;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static Class<?> getGenericType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type firstType = parameterizedType.getActualTypeArguments()[0];
            return firstType instanceof WildcardType ? Object.class : (Class) firstType;
        }
        return (Class) genericType;
    }

    public static String toKebabCase(String camelCase) {
        return camelCase.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
    }

    public static NodeType findJsonType(Class clz) {
        if (String.class.isAssignableFrom(clz)) {
            return NodeType.STRING;
        } else if (Number.class.isAssignableFrom(clz) || (clz.isPrimitive() && (clz.equals(int.class) || clz.equals(long.class) || clz.equals(byte.class) || clz.equals(short.class)))) {
            return NodeType.NUMBER;
        } else if (Boolean.class.isAssignableFrom(clz) || (clz.isPrimitive() && clz.equals(boolean.class))) {
            return NodeType.BOOLEAN;
        } else if (Collection.class.isAssignableFrom(clz) || Map.class.isAssignableFrom(clz)) {
            return NodeType.LIST;
        }
        return NodeType.STRING;
    }

    public static <T> Set<T> instantiate(Set<Class<? extends T>> classes) {
        return classes.stream().map(Utils::instantiate).collect(Collectors.toSet());
    } 
    
    public static <T> T instantiate(Class<T> clz) {
        try {
            return clz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot instantiate class: " + clz.getName(), e);
        }
    }

    public static String trimSpecialsChars(String value) {
        return value.replaceAll("[\n\r]", ",").replaceAll("[\t\b\f]", " ");
    }
    
    @SuppressWarnings("squid:S106")
    public static void err(String line) {
        System.err.println(line);
    }

    @SuppressWarnings("squid:S106")
    public static void out(String line) {
        System.out.println(line);
    }

    @SuppressWarnings("squid:S106")
    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }
}
