package org.apache.cayenne.modeler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.cayenne.modeler.controller.ScreenController;

public class Application extends BQApplication{



    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = getLoader(getClass().getResource("WelcomeScreen.fxml"));
        Pane root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setWidth(1370);
        primaryStage.setHeight(844);

        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(650);

        getInjector().getInstance(ScreenController.class).setScene(scene);
        getInjector().getInstance(ScreenController.class).setUnbindableController(loader.getController());
        getInjector().getInstance(ScreenController.class).setPrimaryStage(primaryStage);

        primaryStage.setTitle("JavaFX Cayenne modeler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
