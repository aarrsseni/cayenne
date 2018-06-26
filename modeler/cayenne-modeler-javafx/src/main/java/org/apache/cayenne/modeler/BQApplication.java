package org.apache.cayenne.modeler;

import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.Objects;

public abstract class BQApplication extends Application{
    static Injector INJECTOR;

    public static Injector getInjector() {
        return Objects.requireNonNull(INJECTOR, "Injector is not initialized. Started outside Bootique?");
    }

    static FXMLLoader getLoader(URL resourceUrl) {
        return getInjector().getInstance(FXMLLoaderFactory.class).getLoader(resourceUrl);
    }
}
