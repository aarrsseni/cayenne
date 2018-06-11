package org.apache.cayenne.modeler.jFx.component;

import com.google.inject.Inject;
import javafx.scene.control.TableCell;
import org.apache.cayenne.modeler.controller.dbEntity.DbRelationshipsController;
import org.apache.cayenne.modeler.observer.Observer;

import java.util.function.Consumer;

public class DbRelationshipConsumer implements Consumer<TableCell>{

    @Inject
    private DbRelationshipsController dbRelationshipsController;

    @Override
    public void accept(TableCell tableCell) {
        Observer observer = (Observer)tableCell.getTableView().getItems().get(tableCell.getIndex());
        if (dbRelationshipsController.getDependentPropertyMap().get(observer.getBean()).getValue()) {
            tableCell.setDisable(false);
        } else {
            tableCell.setDisable(true);
        }
    }
}
