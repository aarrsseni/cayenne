package org.apache.cayenne.modeler.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelerUtils {
    public static List<String> getDeleteRules(){
        return new ArrayList<>(Arrays.asList(
                "No Action",
                "Nullify",
                "Cascade",
                "Deny"
        ));
    }

    public static void showAlert(ValidationResult validationResult){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Validation information");
        alert.setHeaderText("Validation Problems");
        ListView<String> listView = new ListView<>();
        listView.setMinWidth(500);
        for(ValidationFailure validationFailure : validationResult.getFailures()){
            listView.getItems().add(validationFailure.getDescription());
        }
        alert.setGraphic(listView);
        alert.showAndWait();
    }

    public static void showErrorAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
