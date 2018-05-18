package org.apache.cayenne.modeler.intercept;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.asm.MethodInterceptor;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/*
 *   After changes in CustomAgent need to rebuild .jar file, which contains CustomAgent.class and MANIFEST.MF
 *   VM Option - -javaagent:"/Users/Arseni/Desktop/cayenne/cayenne/modeler/cayenne-modeler-javafx/modeler-javafx-manifest.jar"
 */
public class CustomAgent {

    private static Set<Class> redefinedClassSet;
    private static List<Class> redefinedClasses;
    static {
        redefinedClasses = new LinkedList<>();
        redefinedClasses.add(DataChannelDescriptor.class);
        redefinedClasses.add(DataMap.class);
        redefinedClasses.add(DbEntity.class);
        redefinedClasses.add(DbAttribute.class);
    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        createClassSet();
        redefinedClassSet.forEach(clazz -> {
            byte[] bytecode = MethodInterceptor.addCallbackToClass(clazz);
            try {
                instrumentation.redefineClasses(new ClassDefinition(clazz, bytecode));
            } catch (ClassNotFoundException | UnmodifiableClassException e) {
                e.printStackTrace();
            }
        });
    }

    // Create set of all redefined class with parents
    // Needed, because field can be in parent class
    private static void createClassSet() {
        redefinedClassSet = new HashSet<>();
        redefinedClasses.forEach(clazz -> {
            redefinedClassSet.add(clazz);
            Class parentClass = clazz.getSuperclass();
            while (parentClass != Object.class) {
                redefinedClassSet.add(parentClass);
                parentClass = parentClass.getSuperclass();
            }
        });
    }

}
