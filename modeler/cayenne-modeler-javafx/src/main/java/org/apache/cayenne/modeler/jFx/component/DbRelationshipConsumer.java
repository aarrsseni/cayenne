package org.apache.cayenne.modeler.jFx.component;

import com.google.inject.Inject;
import javafx.scene.control.TableCell;
import org.apache.cayenne.modeler.controller.dbEntity.DbRelationshipsController;
import org.apache.cayenne.modeler.observer.Observer;

public class DbRelationshipConsumer implements Consumer{

    @Inject
    private DbRelationshipsController dbRelationshipsController;


    @Override
    public void consume(TableCell tableCell, Observer observer) {
        if ((Boolean) dbRelationshipsController.getDependentPropertyMap().get(observer.getBean()).getValue()) {
            tableCell.setDisable(false);
        } else {
            tableCell.setDisable(true);
        }
    }
}
