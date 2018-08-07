package io.codelens.tools.thorntail.builders;

import io.codelens.tools.thorntail.ModelBuilder;
import io.codelens.tools.thorntail.SchemaModel;
import org.reflections.Reflections;
import org.wildfly.swarm.config.runtime.*;
import org.wildfly.swarm.spi.api.Fraction;
import org.wildfly.swarm.spi.api.annotations.Configurable;
import org.wildfly.swarm.spi.api.annotations.Configurables;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import static io.codelens.tools.thorntail.Utils.*;

/**
 * Model builder which scans the thorntail packages and the config-api for available config paths
 */
public class AutodetectModelBuilder implements ModelBuilder {

    private static final String THORNTAIL_CONFIG_BASE_PACKAGE = "org.wildfly.swarm";
    private Set<Class<? extends Fraction>> fractions;

    public AutodetectModelBuilder() {
        Reflections thorntailConfigReflections = new Reflections(THORNTAIL_CONFIG_BASE_PACKAGE);
        fractions = thorntailConfigReflections.getSubTypesOf(Fraction.class);
    }

    @Override
    public void build(SchemaModel model) {
        for (Class<? extends Fraction> fraction : fractions) {
            Class<?> keyedClass = fraction.getSuperclass();
            if (isResourceType(keyedClass)) {
                scanResource(model, keyedClass, "swarm." + instantiateKeyed(keyedClass).getKey());
                processFractionFields(model, fraction);
            }
        }
    }
    
    private void processFractionFields(SchemaModel model, Class<? extends Fraction> fraction) {
        for (Field fractionField : fraction.getDeclaredFields()) {
            if (fractionField.isAnnotationPresent(Configurable.class)) {
                model.addPath(fractionField.getAnnotation(Configurable.class).value(), "", fractionField);
            } else if (fractionField.isAnnotationPresent(Configurables.class)) {
                for (Configurable configurable : fractionField.getAnnotation(Configurables.class).value()) {
                    model.addPath(configurable.value(), "", fractionField);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private Keyed instantiateKeyed(Class keyedClass) {
        return instantiate((Class<? extends Keyed>)keyedClass);
    }

    private boolean isResourceType(Class<?> keyedClass) {
        return Keyed.class.isAssignableFrom(keyedClass) &&
                keyedClass.isAnnotationPresent(ResourceType.class) &&
                keyedClass.getAnnotation(ResourceType.class).value().equals("subsystem");
    }

    private void scanResource(SchemaModel model, Class clz, String prefix) {

        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(AttributeDocumentation.class)) {
                String fieldKey = toKebabCase(field.getName());
                String fieldDoc = field.getAnnotation(AttributeDocumentation.class).value();
                model.addPath(prefix + "." + fieldKey, fieldDoc, field);
            } else if (field.getName().equals("subresources")) {
                Class<?> subresourcesType = field.getType();
                processSubresources(model, subresourcesType, prefix);
            }
        }
    }
    
    private void processSubresources(SchemaModel model, Class<?> subresourcesType, String prefix) {
        for (Field subresourceField : subresourcesType.getDeclaredFields()) {
            if (subresourceField.isAnnotationPresent(ResourceDocumentation.class)) {
                String subresourceFieldKebabName = toKebabCase(subresourceField.getName());
                if (subresourceField.isAnnotationPresent(SingletonResource.class)) {
                    scanResource(model, subresourceField.getType(), prefix + "." + subresourceFieldKebabName);
                } else {
                    processCollectionSubresource(model, subresourcesType, subresourceField, subresourceFieldKebabName, prefix);
                }
            }
        }
    }
    
    private void processCollectionSubresource(SchemaModel model, Class<?> subresourcesType, Field subresourceField, String subresourceFieldKebabName, String prefix) {
        Class<?> subresType = subresourceField.getType();
        if (Collection.class.isAssignableFrom(subresType)) {
            Class<?> collectionGenericType = getGenericType(subresourceField);
            if (collectionGenericType != null) {
                scanResource(model, collectionGenericType, prefix + "." + subresourceFieldKebabName + ".KEY");
            } else {
                err("Cannot determine collection (" + subresourcesType.getName() + "." + subresourceField.getName() + ") type: " + subresType);
            }
        } else {
            throw new IllegalStateException("Unsupported subresrouce (" + subresourcesType.getName() + "." + subresourceField.getName() + ") type: " + subresType.getName());
        }
    }

}
