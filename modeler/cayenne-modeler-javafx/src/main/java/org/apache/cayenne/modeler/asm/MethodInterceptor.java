package org.apache.cayenne.modeler.asm;

import javassist.*;
import javassist.bytecode.ClassFile;
import org.apache.cayenne.modeler.observer.Observer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class MethodInterceptor {

    /*
     *   For redefined classes setter and getter must have name like - setXxx (getXxx)
     *   Where Xxx - field name with first upper case letter
     */
    private static final String SETTER_REGEX = "set.*";
    private static final byte SETTER_PARAMETER_COUNT = 1;
    private static final Class SETTER_RETURN_TYPE = void.class;
    private static final byte GETTER_PARAMETER_COUNT = 0;
    private static final byte FIELD_NAME_START_INDEX = 3;
    private static final String CALLBACK_FORMAT =
            " org.apache.cayenne.modeler.observer.Observer observer = " +
                    " org.apache.cayenne.modeler.observer.ObserverDictionary.getObserver(this); " +
                    " observer.update(\"%s\", %s); ";

    public static byte[] addCallbackToClass(Class clazz) {
        ClassPool pool = ClassPool.getDefault();
        try {
            Class.forName("org.apache.cayenne.modeler.observer.Observer");
            Class.forName(clazz.getCanonicalName());
            CtClass ctClass = pool.get(clazz.getCanonicalName());
            ClassFile classFile = ctClass.getClassFile();
            getClassSetters(clazz).forEach(setter -> {
                try {
                    CtMethod method = CtMethod.make(
                            classFile.getMethod(setter.getName()), ctClass
                    );
                    String callbackCode = null;
                    if (classHasFieldForSetter(clazz, setter)) {
                        callbackCode = getCallbackCode(getFieldNameFromMethod(setter), true);
                    } else if (classHasGetterForField(clazz, setter)) {
                        callbackCode = getCallbackCode(getFieldNameFromMethod(setter), false);
                    }
                    if (callbackCode != null) {
                        method.insertAfter(callbackCode);
                    }
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            });
            return ctClass.toBytecode();
        } catch (ClassNotFoundException | NotFoundException | CannotCompileException | IOException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static String getCallbackCode(String fieldName, boolean fieldBased) {
        if (fieldBased) {
            return String.format(CALLBACK_FORMAT, fieldName, "this." + fieldName);
        } else {
            return String.format(CALLBACK_FORMAT, fieldName, Observer.getAccessMethodName("get", fieldName) + "()");
        }
    }

    private static boolean classHasGetterForField(Class clazz, Method setter) {
        String fieldName = getFieldNameFromMethod(setter);
        for (Method method : clazz.getDeclaredMethods()) {
            if ((isGetter(method)) && (fieldName.equals(getFieldNameFromMethod(method)))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGetter(Method method) {
        String getterName = Observer.getAccessMethodName("get", getFieldNameFromMethod(method));
        String booleanGetterName = Observer.getAccessMethodName("is", getFieldNameFromMethod(method));
        boolean hasNotParameters = method.getParameterCount() == GETTER_PARAMETER_COUNT;
        return ((method.getName().equals(getterName)) || (method.getName().equals(booleanGetterName))) && hasNotParameters;
    }

    private static boolean isSetter(Method method) {
        boolean hasSetPrefix = method.getName().matches(SETTER_REGEX);
        boolean hasOneParameter = method.getParameterCount() == SETTER_PARAMETER_COUNT;
        boolean returnsVoid = method.getReturnType() == SETTER_RETURN_TYPE;
        return hasSetPrefix && hasOneParameter && returnsVoid;
    }

    // Separates field name from getter or setter
    private static String getFieldNameFromMethod(Method setter) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(Character.toLowerCase(setter.getName().charAt(FIELD_NAME_START_INDEX)))
                .append(setter.getName().substring(FIELD_NAME_START_INDEX + 1));
        return stringBuilder.toString();
    }

    private static boolean classHasFieldForSetter(Class clazz, Method setter) {
        boolean result = false;
        try {
            result = clazz.getDeclaredField(getFieldNameFromMethod(setter)) != null;
        } catch (NoSuchFieldException e) {}
        return result;
    }

    private static List<Method> getClassSetters(Class clazz) throws NoSuchFieldException {
        List<Method> methods = new LinkedList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (isSetter(method)) {
                methods.add(method);
            }
        }
        return methods;
    }
}
