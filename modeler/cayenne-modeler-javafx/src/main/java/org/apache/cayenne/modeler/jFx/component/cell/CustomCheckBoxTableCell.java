package org.apache.cayenne.modeler.jFx.component.cell;

import javafx.scene.control.cell.CheckBoxTableCell;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.controller.dbEntity.DbRelationshipsController;
import org.apache.cayenne.modeler.jFx.component.DbRelationshipConsumer;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

public class CustomCheckBoxTableCell extends CheckBoxTableCell<Observer, Boolean>{

    private DbRelationshipConsumer consumer;

    private DbRelationshipsController dbRelationshipsController;

    public CustomCheckBoxTableCell(DbRelationshipConsumer consumer){
        this.consumer = consumer;
        dbRelationshipsController = BQApplication.getInjector().getInstance(DbRelationshipsController.class);
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        if(!empty) {
            Observer observer = getTableView().getItems().get(getIndex());
            consumer.accept(this);

            if (item != null && dbRelationshipsController.checkForDepPK((DbRelationship) observer.getBean())) {
                DbRelationship reverseRelationship = ((DbRelationship)observer.getBean()).getReverseRelationship();
                if(reverseRelationship != null){
                    ObserverDictionary.getObserver(reverseRelationship).getPropertyWithoutBinding("toDependentPK").setValue(!item);
                }
            }
        }
        super.updateItem(item, empty);
    }
}
