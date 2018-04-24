package org.apache.cayenne.modeler;

import javafx.fxml.FXMLLoader;

import java.net.URL;

public interface FXMLLoaderFactory {
    FXMLLoader getLoader(URL resourceUrl);
}
