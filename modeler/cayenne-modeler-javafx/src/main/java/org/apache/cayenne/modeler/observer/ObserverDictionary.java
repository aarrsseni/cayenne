package org.apache.cayenne.modeler.observer;

import java.util.HashMap;
import java.util.Map;

public class ObserverDictionary {
    private static Map<Object, Observer> observers;

    static {
        observers = new HashMap<>();
    }

    public static Observer getObserver(Object entity) {
        Observer observer = observers.get(entity);
        if (observer == null) {
            observer = new Observer(entity);
            observers.put(entity, observer);
        }
        return observer;
    }

    public static Object getObject(Observer observer) {
        for (Object key : observers.keySet()) {
            if (observers.get(key) == observer) {
                return key;
            }
        }
        return null;
    }
}
