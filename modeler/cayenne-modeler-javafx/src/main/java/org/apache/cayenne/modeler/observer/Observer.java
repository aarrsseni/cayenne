package org.apache.cayenne.modeler.observer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.cayenne.CayenneRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Observer {

    private static final byte FIRST_CHAR_INDEX = 0;
    private static final byte SUBSTRING_START_INDEX = 1;
    private Object bean;
    private Map<String, Property> fieldsProperties;
    private Map<String, Method> fieldSetter;

    public Observer(Object bean) {
        this.bean = bean;
        fieldsProperties = new HashMap<>();
        fieldSetter = new HashMap<>();
    }

    /* May be string/number/boolean ? */
    public Observer bind(String fieldName, Property bindedProperty) {
        Property fieldProperty = fieldsProperties.get(fieldName);
        if (fieldProperty == null) {
            fieldProperty = getPropertyImpl(fieldName);
            fieldsProperties.put(fieldName, fieldProperty);
        }
        if (fieldProperty.getClass() == SimpleIntegerProperty.class) {
            Bindings.bindBidirectional(bindedProperty, fieldProperty, new NumberStringConverter());
        } else if (fieldProperty.getClass() == SimpleDoubleProperty.class) {
            Bindings.bindBidirectional(bindedProperty, fieldProperty, new DoubleStringConverter());
        } else if (fieldProperty.getClass() == SimpleFloatProperty.class) {
            Bindings.bindBidirectional(bindedProperty, fieldProperty, new FloatStringConverter());
        } else {
            Bindings.bindBidirectional(bindedProperty, fieldProperty);
        }
        return this;
    }

    public void unbind(String fieldName, Property bindedProperty) {
        Property fieldProperty = fieldsProperties.get(fieldName);
        if (fieldProperty == null) {
            throw new CayenneRuntimeException("Field '" + fieldName + "' not binded.");
        }
        Bindings.unbindBidirectional(bindedProperty, fieldProperty);
    }

    public void unbind() {

    }

    private void updatePropertyValue(String fieldName, Object value) {
        Property property = fieldsProperties.get(fieldName);
        if (property == null) {
            return;
        }
        property.setValue(value);
    }

    public void update(String fieldName, Object value) {
        updatePropertyValue(fieldName, value);
    }

    public void update(String fieldName, int value) {
        updatePropertyValue(fieldName, value);
    }

    public void update(String fieldName, boolean value) {
        updatePropertyValue(fieldName, value);
    }

    private Property getPropertyInstance(Class fieldType) {
        if (fieldType == String.class) {
            return new SimpleStringProperty();
        } else if ((fieldType == int.class) || (fieldType == Integer.class)) {
            return new SimpleIntegerProperty();
        } else if ((fieldType == boolean.class) || (fieldType == Boolean.class)) {
            return new SimpleBooleanProperty();
        } else if ((fieldType == float.class) || (fieldType == Float.class)) {
            return new SimpleFloatProperty();
        } else if ((fieldType == double.class) || (fieldType == Double.class)) {
            return new SimpleDoubleProperty();
        } else {
            return new SimpleObjectProperty();
        }
    }

    private Class getFieldType(String fieldName) throws ClassNotFoundException {
        Class beanClass = Class.forName(bean.getClass().getCanonicalName());
        Class fieldType = null;
        Class parentClass = null;
        while (fieldType == null) {
            try {
                fieldType = beanClass.getDeclaredField(fieldName).getType();
            } catch (NoSuchFieldException e) {}
            // ---------------------------------------
            parentClass = beanClass.getSuperclass();
            while (parentClass != Object.class) {
                try {
                    fieldType = parentClass.getDeclaredField(fieldName).getType();
                } catch (NoSuchFieldException e) {}
                parentClass = parentClass.getSuperclass();
            }
        }
        return fieldType;
    }

    private Property getPropertyImpl(String fieldName) {
        try {
            Class fieldType = getFieldType(fieldName);
            Property property = getPropertyInstance(fieldType);
            property.addListener((observable, oldValue, newValue) -> callBeanSetter(newValue, fieldName));
            return property;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAccessMethodName(String access, String fieldName) {
        StringBuilder builder = new StringBuilder();
        builder
                .append(access)
                .append(Character.toUpperCase(fieldName.charAt(FIRST_CHAR_INDEX)))
                .append(fieldName.substring(SUBSTRING_START_INDEX));
        return builder.toString();
    }

    // Calling setter for field
    private void callBeanSetter(Object value, String fieldName) {
        Method method = null;
        try {
            method = fieldSetter.get(fieldName);
            if (method == null) {
                method = bean.getClass().getMethod(getAccessMethodName("set", fieldName), getFieldType(fieldName));
                fieldSetter.put(fieldName, method);
            }
            if (method != null) {
                method.invoke(bean, value);
            }
        } catch (SecurityException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getBean() {
        return bean;
    }

    public Map<String, Property> getFieldsProperties() {
        return fieldsProperties;
    }

}
