package org.apache.cayenne.modeler.components;

import javafx.scene.control.cell.CheckBoxTableCell;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.controller.DbRelationshipsController;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.util.Consumer;

public class CustomCheckBoxTableCell extends CheckBoxTableCell{

    private Consumer consumer;

    private DbRelationshipsController dbRelationshipsController;

    public CustomCheckBoxTableCell(){
        consumer = BQApplication.getInjector().getInstance(Consumer.class);
        dbRelationshipsController = BQApplication.getInjector().getInstance(DbRelationshipsController.class);
    }

    @Override
    public void updateItem(Object item, boolean empty) {
        if(!empty) {
            Observer observer = (Observer) getTableView().getItems().get(getIndex());
            consumer.consume(this, observer);

            if (item != null && dbRelationshipsController.checkForDepPK((DbRelationship) observer.getBean())) {
                consumer.reverseUpdate(observer, item);
            }
        }
        super.updateItem(item, empty);
    }
}
